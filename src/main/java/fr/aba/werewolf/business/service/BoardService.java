package fr.aba.werewolf.business.service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.state.Board;

public interface BoardService {

	Board distributeCards(Game game) throws GameException;

	Board getCurrentBoard(Game game) throws GameException;

	Board updateBoard(Game game, Board board) throws GameException;

	Board restart(Game game);

}
