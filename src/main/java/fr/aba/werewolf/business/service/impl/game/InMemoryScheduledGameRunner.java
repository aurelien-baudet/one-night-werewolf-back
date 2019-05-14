package fr.aba.werewolf.business.service.impl.game;

import static fr.aba.werewolf.business.domain.state.Phase.AWAKE;
import static fr.aba.werewolf.business.domain.state.Phase.DISCUSS;
import static fr.aba.werewolf.business.domain.state.Phase.SLEEPING;
import static fr.aba.werewolf.business.domain.state.Phase.SUNRISE;
import static fr.aba.werewolf.business.domain.state.Phase.SUNSET;
import static fr.aba.werewolf.business.domain.state.Phase.VOTE;
import static java.time.Duration.ofMillis;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Phase;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameLogicService;
import fr.aba.werewolf.business.service.GameRunner;
import fr.aba.werewolf.business.service.RoleService;
import fr.aba.werewolf.business.service.VoteService;
import fr.aba.werewolf.business.service.impl.game.config.TimingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_= {@Autowired})
public class InMemoryScheduledGameRunner implements GameRunner {
	private final TimingProperties timingConfiguration;
	private final TaskScheduler scheduler;
	private final GameLogicService gameLogicService;
	private final BoardService boardService;
	private final RoleService roleService;
	private final VoteService voteService;
	private final Map<String, ScheduledFuture<?>> tasksByGameId;

	public InMemoryScheduledGameRunner(TimingProperties timingConfiguration, TaskScheduler scheduler, GameLogicService gameLogicService, BoardService boardService, RoleService roleService, VoteService voteService) {
		this(timingConfiguration, scheduler, gameLogicService, boardService, roleService, voteService, new ConcurrentHashMap<>());
	}
	

	@Override
	public void run(Game game) throws GameException {
		gameLogicService.closeEveryoneEyes(game);
		log.debug("[{}] sunset", game.getId());
		ScheduledFuture<?> scheduled = scheduler.schedule(() -> next(game), new GameTrigger(game));
		tasksByGameId.put(game.getId(), scheduled);
	}

	@Override
	public void start(Game game) throws GameException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void restart(Game game) throws GameException {
		stop(game);
		boardService.restart(game);
		voteService.clean(game);
	}

	@Override
	public void pause(Game game) throws GameException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(Game game) throws GameException {
		ScheduledFuture<?> scheduled = tasksByGameId.remove(game.getId());
		if(scheduled != null) {
			scheduled.cancel(true);
		}
	}
	
	@RequiredArgsConstructor
	private class GameTrigger implements Trigger {
		private final Game game;

		@Override
		public Date nextExecutionTime(TriggerContext triggerContext) {
			try {
				// first trigger to wait while everyone closes his eyes
				if(triggerContext.lastActualExecutionTime() == null) {
					return after(timingConfiguration.getCloseEveryoneEyesDuration().toMillis());
				}
				Board currentBoard = boardService.getCurrentBoard(game);
				Phase phase = currentBoard.getPhase();
				if(AWAKE.equals(phase)) {
					return after(timingConfiguration.getWakeUpDuration().toMillis() + game.getGameOptions().getPauseDuration().toMillis());
				} else if(SLEEPING.equals(phase)) {
					return after(timingConfiguration.getCloseEyesDuration().toMillis());
				} else if(SUNRISE.equals(phase)) {
					return after(timingConfiguration.getWakeUpEveryoneDuration().toMillis());
				} else if(DISCUSS.equals(phase)) {
					return after(timingConfiguration.getTimerUpdateInterval().toMillis());
				} else if(VOTE.equals(phase)) {
					return after(timingConfiguration.getTimerUpdateInterval().toMillis());
				}
				return null;
			} catch(GameException e) {
				// TODO: handle exceptions
				return null;
			}
		}
		
		private Date after(long durationMs) {
			log.trace("[{}] wait for {}s", game.getId(), durationMs/1000);
			return new Date(Instant.now().plusMillis(durationMs).toEpochMilli());
		}
	}

	private void next(Game game) {
		try {
			Board currentBoard = boardService.getCurrentBoard(game);
			Role currentRole = currentBoard.getCurrentRole();
			Role nextRole = roleService.getNextRole(game, currentBoard);
			// wake up the current role
			if(shouldWakeUp(game, currentBoard, currentRole, nextRole)) {
				log.debug("[{}] wake up ({})", game.getId(), roleName(nextRole));
				gameLogicService.wakeUp(game);
				return;
			}
			// if someone is currently woken up => close eyes
			if(shouldCloseEyes(game, currentBoard, currentRole)) {
				log.debug("[{}] close eyes ({})", game.getId(), roleName(currentRole));
				gameLogicService.closeEyes(game);
				return;
			}
			// the night has ended and it's sunrise
			// => wake up everyone and start discussion
			if(isSunrise(game, currentBoard, currentRole, nextRole)) {
				log.debug("[{}] sunrise", game.getId());
				gameLogicService.wakeUpEveryone(game);
				return;
			}
			// it's the day
			// => discuss to find who who is a werewolf
			if(isDiscussionTime(game, currentBoard, currentRole)) {
				log.debug("[{}] discussion time", game.getId());
				updateDiscussState(game, currentBoard);
				return;
			}
			// the discussion is over
			// => now you need to vote
			if(isVoteTime(game, currentBoard, currentRole)) {
				log.debug("[{}] prepare to vote", game.getId());
				updateVoteState(game, currentBoard);
				return;
			}
			// vote has been done
			// => end of the game
			if(isEndOfGame(game, currentBoard, currentRole)) {
				gameLogicService.end(game);
				// game is ended => remove scheduled task
				tasksByGameId.remove(game.getId()).cancel(true);				
			}
		} catch (GameException e) {
			// TODO: handle exception
		}
	}




	private boolean shouldWakeUp(Game game, Board currentBoard, Role currentRole, Role nextRole) {
		Phase previousGameState = currentBoard.getPhase();
		// already awake
		// => do not wake up
		if(AWAKE.equals(previousGameState)) {
			return false;
		}
		// game has just begun
		// => wake up first role (werewolves)
		if(SUNSET.equals(previousGameState)) {
			return true;
		}
		// there was already at least one turn (previous condition)
		// but no more role on the board
		// => the game has ended
		if(currentRole == null || SUNRISE.equals(previousGameState)) {
			return false;
		}
		// if everyone is sleeping
		// and it is not the end of the game (previous condition)
		// => must wake up someone (if there is someone)
		return SLEEPING.equals(previousGameState) && nextRole!=null;
	}


	private boolean shouldCloseEyes(Game game, Board currentBoard, Role currentRole) {
		Phase previousGameState = currentBoard.getPhase();
		// already sleeping
		// => do not close eyes
		if(SLEEPING.equals(previousGameState)) {
			return false;
		}
		// game has just begun
		// => do not close eyes
		if(SUNSET.equals(previousGameState)) {
			return false;
		}
		// discussing
		// => not the time to close eyes anymore
		if(DISCUSS.equals(previousGameState)) {
			return false;
		}
		// voting
		// => not the time to close eyes anymore
		if(VOTE.equals(previousGameState)) {
			return false;
		}
		// there was already at least one turn (previous condition)
		// but no more role on the board
		// so the game is ending
		// => close eyes of everyone just before opening end of the night
		if(currentRole == null && !SUNRISE.equals(previousGameState)) {
			return true;
		}
		// if someone was awake
		// and it is not the end of the game (previous condition)
		// => must wake up someone
		return AWAKE.equals(previousGameState);
	}


	private boolean isSunrise(Game game, Board currentBoard, Role currentRole, Role nextRole) {
		Phase previousGameState = currentBoard.getPhase();
		// game has just begun
		// => not the end of the night
		if(SUNSET.equals(previousGameState)) {
			return false;
		}
		// someone still awake
		// => must close his eyes before sunrise
		if(AWAKE.equals(previousGameState)) {
			return false;
		}
		// there was already at least one turn (previous condition)
		// but no more role on the board
		// and everyone is sleeping
		// so the game is ending
		// => this is sunrise
		if(nextRole == null && SLEEPING.equals(previousGameState)) {
			return true;
		}
		return false;
	}
	
	private boolean isDiscussionTime(Game game, Board currentBoard, Role currentRole) {
		Phase previousGameState = currentBoard.getPhase();
		// it's the day
		// => time to start discussing who are the werewolves
		if(SUNRISE.equals(previousGameState)) {
			return true;
		}
		// timer is not over
		// => keep on discussing
		if(DISCUSS.equals(previousGameState) && currentBoard.getRemainingDiscussionDuration().toMillis() > 0) {
			return true;
		}
		return false;
	}
	
	private boolean isVoteTime(Game game, Board currentBoard, Role currentRole) {
		Phase previousGameState = currentBoard.getPhase();
		// discussion is over
		// => time to vote
		if(DISCUSS.equals(previousGameState) && currentBoard.getRemainingDiscussionDuration().toMillis() <= 0) {
			return true;
		}
		// timer is not over
		// => keep on voting
		if(VOTE.equals(previousGameState) && currentBoard.getRemainingVoteDuration().toMillis() > 0) {
			return true;
		}
		// if some players have not voted yet
		// => wait for their vote
		if(VOTE.equals(previousGameState) && !voteService.hasEveryoneVoted(game)) {
			return true;
		}
		return false;
	}

	private boolean isEndOfGame(Game game, Board currentBoard, Role currentRole) {
		Phase previousGameState = currentBoard.getPhase();
		// vote timer is over
		// and everyone has voted
		// => the game is over
		if(VOTE.equals(previousGameState) && currentBoard.getRemainingVoteDuration().toMillis() <= 0 && voteService.hasEveryoneVoted(game)) {
			return true;
		}
		return false;
	}

	private void updateDiscussState(Game game, Board currentBoard) throws GameException {
		Board board = updatePhase(game, currentBoard, DISCUSS);
		board = updateRemainingDiscussionDuration(game, board);
		boardService.updateBoard(game, board);
	}

	private void updateVoteState(Game game, Board currentBoard) throws GameException {
		Board board = updatePhase(game, currentBoard, VOTE);
		board = updateRemainingVoteDuration(game, board);
		boardService.updateBoard(game, board);
	}

	private Board updatePhase(Game game, Board currentBoard, Phase phase) {
		Board board = new Board(currentBoard);
		board.setPhase(phase);
		return board;
	}

	private Board updateRemainingDiscussionDuration(Game game, Board currentBoard) {
		Board board = new Board(currentBoard);
		long currentRemainingDuration = board.getRemainingDiscussionDuration().toMillis();
		long interval = timingConfiguration.getTimerUpdateInterval().toMillis();
		long remainingDuration = Math.max(0, currentRemainingDuration - interval);
		board.setRemainingDiscussionDuration(ofMillis(remainingDuration));
		return board;
	}
	
	private Board updateRemainingVoteDuration(Game game, Board currentBoard) {
		Board board = new Board(currentBoard);
		long currentRemainingDuration = board.getRemainingVoteDuration().toMillis();
		long interval = timingConfiguration.getTimerUpdateInterval().toMillis();
		long remainingDuration = Math.max(0, currentRemainingDuration - interval);
		board.setRemainingVoteDuration(ofMillis(remainingDuration));
		return board;
	}
	
	private String roleName(Role role) {
		return role==null ? "none" : role.getName();
	}


}
