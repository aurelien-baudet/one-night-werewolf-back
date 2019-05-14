package fr.aba.werewolf.business.service.impl.game.solver;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.state.Board;

public interface WinnersSolver {
	Set<Player> computeWinners(Game game, Board board, Set<Player> deads);
}
