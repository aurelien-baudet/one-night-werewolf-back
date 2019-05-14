package fr.aba.werewolf.business.service.impl.game;

import static fr.aba.werewolf.business.domain.state.Phase.AWAKE;
import static fr.aba.werewolf.business.domain.state.Phase.SLEEPING;
import static fr.aba.werewolf.business.domain.state.Phase.SUNRISE;
import static fr.aba.werewolf.business.domain.state.Phase.SUNSET;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameLogicService;
import fr.aba.werewolf.business.service.RoleService;
import fr.aba.werewolf.business.service.impl.game.config.RulesProperties;
import fr.aba.werewolf.business.service.impl.game.exception.GameStartException;
import fr.aba.werewolf.business.service.impl.game.exception.NotEnoughCardsException;
import fr.aba.werewolf.business.service.impl.game.exception.NotEnoughPlayersException;
import fr.aba.werewolf.business.service.impl.game.exception.TooManyCardsException;
import fr.aba.werewolf.business.service.impl.game.exception.TooManyPlayersException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandardGameLogicService implements GameLogicService {
	private final RulesProperties rules;
	private final BoardService boardManager;
	private final RoleService roleService;
	
	@Override
	public Board distribute(Game game) throws GameException {
		return boardManager.distributeCards(game);
	}
	
	@Override
	public Board closeEveryoneEyes(Game game) throws GameException {
		checkCanStart(game);
		Board board = new Board(boardManager.getCurrentBoard(game));
		board.setPhase(SUNSET);
		hideAllCards(game, board);
		closeAllEyes(game, board);
		return boardManager.updateBoard(game, board);
	}

	@Override
	public Board wakeUp(Game game) throws GameException {
		Board currentBoard = boardManager.getCurrentBoard(game);
		Role nextRole = roleService.getNextRole(game, currentBoard);
		log.info("[{}] wake up ({})", game.getId(), nextRole == null ? "no one" : nextRole.getName());
		if(nextRole == null) {
			return currentBoard;
		}
		Board board = new Board(currentBoard);
		board.setStarted(true);
		board.setCurrentRole(nextRole);
		board.setPhase(AWAKE);
		Set<Player> playersToWakeUp = getPlayersWithOriginalRole(game, board, nextRole);
		openEyes(game, board, playersToWakeUp);
		return boardManager.updateBoard(game, board);
	}

	@Override
	public Board closeEyes(Game game) throws GameException {
		Board currentBoard = boardManager.getCurrentBoard(game);
		log.info("[{}] close eyes ({})", game.getId(), currentBoard.getCurrentRole() == null ? "no one" : currentBoard.getCurrentRole().getName());
		Board board = new Board(currentBoard);
		board.setPhase(SLEEPING);
		hideAllCards(game, board);
		closeAllEyes(game, board);
		return boardManager.updateBoard(game, board);
	}

	@Override
	public Board wakeUpEveryone(Game game) throws GameException {
		Board currentBoard = boardManager.getCurrentBoard(game);
		log.info("[{}] sunrise", game.getId());
		Board board = new Board(currentBoard);
		board.setCurrentRole(null);
		board.setPhase(SUNRISE);
		openEyes(game, board, new HashSet<>(game.getPlayers()));
		return boardManager.updateBoard(game, board);
	}
	

	@Override
	public Board end(Game game) throws GameException {
		Board currentBoard = boardManager.getCurrentBoard(game);
		log.info("[{}] end of game", game.getId());
		Board board = new Board(currentBoard);
		board.setEnded(true);
		changeVisibilityOfAllCards(game, board, true);
		return boardManager.updateBoard(game, board);
	}


	private void checkCanStart(Game game) throws GameStartException {
		int numPlayers = game.getPlayers().size();
		// check valid number of players
		if(numPlayers < rules.getMinPlayers()) {
			throw new NotEnoughPlayersException(rules.getMinPlayers(), numPlayers);
		}
		if(numPlayers > rules.getMaxPlayers()) {
			throw new TooManyPlayersException(rules.getMaxPlayers(), numPlayers);
		}
		// check that there is the good number of cards
		List<Role> selectedRoles = game.getSelectedRoles();
		int numCards = selectedRoles.size();
		if(numCards < numPlayers + rules.getCardsInTheMiddle()) {
			throw new NotEnoughCardsException(numCards, numPlayers, rules.getCardsInTheMiddle());
		}
		if(numCards < numPlayers + rules.getCardsInTheMiddle()) {
			throw new TooManyCardsException(numCards, numPlayers, rules.getCardsInTheMiddle());
		}
		// TODO: must be at least 2 werewolves ?
		// TODO: there must be 2 "franc-maçon" if selected ?
		
	}
	

	private void openEyes(Game game, Board board, Set<Player> playersToWakeUp) {
		// TODO: indicate to the board that some players screen must be on
		// /!\ if there are several players on the same device, it can't work
	}

	private void hideAllCards(Game game, Board board) {
		changeVisibilityOfAllCards(game, board, false);
	}

	private void closeAllEyes(Game game, Board board) {
		// TODO: indicate to the board that some players screen must be off
		// /!\ if there are several players on the same device, it can't work
	}


	private void changeVisibilityOfAllCards(Game game, Board board, boolean visible) {
		board.getAllPlayerBoards()
			.stream()
			.map(PlayerBoard::getCards)
			.flatMap(List::stream)
			.forEach(card -> card.setVisible(visible));
	}

	private Set<Player> getPlayersWithOriginalRole(Game game, Board board, Role role) {
		return game.getPlayers()
			.stream()
			.filter(player -> role.equals(board.getBoardForPlayer(player).getOriginalRole()))
			.collect(toSet());
	}

}
