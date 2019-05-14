package fr.aba.werewolf.business.service;

import fr.aba.werewolf.business.domain.Game;

public interface GameRunner {
	void run(Game game) throws GameException;
	void start(Game game) throws GameException;
	void restart(Game game) throws GameException;
	void pause(Game game) throws GameException;
	void stop(Game game) throws GameException;
}
