package fr.aba.werewolf.business.service.impl.game.solver;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Vote;
import fr.aba.werewolf.business.domain.state.Board;

public interface DeadsSolver {
	Set<Player> deads(Game game, Board board, Set<Vote> votes);
}
