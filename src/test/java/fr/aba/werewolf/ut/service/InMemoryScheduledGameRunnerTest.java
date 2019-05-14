package fr.aba.werewolf.ut.service;

import static fr.aba.werewolf.business.domain.state.Phase.AWAKE;
import static fr.aba.werewolf.business.domain.state.Phase.SLEEPING;
import static fr.aba.werewolf.business.domain.state.Phase.SUNRISE;
import static fr.aba.werewolf.business.domain.state.Phase.SUNSET;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.STRICT_STUBS;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameLogicService;
import fr.aba.werewolf.business.service.RoleService;
import fr.aba.werewolf.business.service.VoteService;
import fr.aba.werewolf.business.service.impl.game.InMemoryScheduledGameRunner;
import fr.aba.werewolf.business.service.impl.game.config.TimingProperties;

public class InMemoryScheduledGameRunnerTest {
	@Rule public MockitoRule mockito = MockitoJUnit.rule().strictness(STRICT_STUBS);
	
	@Mock TimingProperties timingConfig;
	@Mock TaskScheduler scheduler;
	@Spy GameLogicService logicService;
	@Mock BoardService boardService;
	@Mock RoleService roleService;
	@Mock VoteService voteService;
	@Mock(answer=CALLS_REAL_METHODS) HashMap<String, ScheduledFuture<?>> tasks;
	@Mock ScheduledFuture<?> task;
	@Mock Game game;
	@Mock Board board;
	Role werewolf = new Role("werewolf", "");
	Role seer = new Role("seer", "");
	Role robber = new Role("robber", "");
	Role troublemaker = new Role("troublemaker", "");
	
	InMemoryScheduledGameRunner runner;
	Runnable runnable;
	
	@Before
	public void setup() throws GameException {
		when(game.getId()).thenReturn("1");
		when(scheduler.schedule(any(Runnable.class), any(Trigger.class))).then(new Answer<ScheduledFuture<?>>() {
			@Override
			public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
				runnable = invocation.getArgument(0);
				return task;
			}
		});
		when(boardService.getCurrentBoard(any())).thenReturn(board);
		when(board.getPhase())
			.thenReturn(SUNSET)
			.thenReturn(AWAKE, AWAKE)		// test with current phase is done 2 times
			.thenReturn(SLEEPING)
			.thenReturn(AWAKE, AWAKE)
			.thenReturn(SLEEPING)
			.thenReturn(AWAKE, AWAKE)
			.thenReturn(SLEEPING)
			.thenReturn(AWAKE, AWAKE)
			.thenReturn(SLEEPING, SLEEPING, SLEEPING)	// test with current phase is done 2 times here
			.thenReturn(SUNRISE);
		when(board.getCurrentRole())
			.thenReturn(null)			// start of the game
			.thenReturn(werewolf)		// wake up
			.thenReturn(werewolf)		// close eyes
			.thenReturn(seer)			// wake up
			.thenReturn(seer)			// close eyes
			.thenReturn(robber)			// wake up
			.thenReturn(robber)			// close eyes
			.thenReturn(troublemaker)	// wake up
			.thenReturn(null);			// end of game
		when(roleService.getNextRole(any(), any()))
			.thenReturn(werewolf, werewolf)
			.thenReturn(seer, seer)
			.thenReturn(robber, robber)
			.thenReturn(troublemaker, troublemaker)
			.thenReturn(null);
		runner = new InMemoryScheduledGameRunner(timingConfig, scheduler, logicService, boardService, roleService, voteService, tasks);
	}
	
	@Test
	public void standardGameTest() throws GameException {
		// start game => sunset
		runner.run(game);
		verify(logicService, times(1)).closeEveryoneEyes(game);
		// call next => wake up werewolves
		runnable.run();
		verify(logicService, times(1)).wakeUp(game);
		// call next => close eyes
		runnable.run();
		verify(logicService, times(1)).closeEyes(game);
		// call next => wake up seer
		runnable.run();
		verify(logicService, times(2)).wakeUp(game);
		// call next => close eyes
		runnable.run();
		verify(logicService, times(2)).closeEyes(game);
		// call next => wake up robber
		runnable.run();
		verify(logicService, times(3)).wakeUp(game);
		// call next => close eyes
		runnable.run();
		verify(logicService, times(3)).closeEyes(game);
		// call next => wake up troublemaker
		runnable.run();
		verify(logicService, times(4)).wakeUp(game);
		// call next => close eyes
		runnable.run();
		verify(logicService, times(4)).closeEyes(game);
		// call next => sunrise
		runnable.run();
		verify(logicService, times(1)).wakeUpEveryone(game);
	}

}
