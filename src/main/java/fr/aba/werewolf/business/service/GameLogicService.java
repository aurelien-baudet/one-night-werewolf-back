package fr.aba.werewolf.business.service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.state.Board;

public interface GameLogicService {
	Board distribute(Game game) throws GameException;
	Board closeEveryoneEyes(Game game) throws GameException;
	Board wakeUp(Game game) throws GameException;
	Board closeEyes(Game game) throws GameException;
	Board wakeUpEveryone(Game game) throws GameException;
	Board end(Game game) throws GameException;
}
