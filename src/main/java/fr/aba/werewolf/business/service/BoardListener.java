package fr.aba.werewolf.business.service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.state.Board;

public interface BoardListener {
	void boardUpdated(Game game, Board board);
	void boardResetted(Game game, Board board);
}
