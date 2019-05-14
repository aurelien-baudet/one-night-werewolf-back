package fr.aba.werewolf.business.service.impl.game.solver;

import static java.util.stream.Collectors.toSet;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.InFrontOfPlayer;

public class SolverUtils {
	
	public static Role getRoleOf(Player player, Board board) {
		return board.getAllPlayerBoards().get(0).getCards()
				.stream()
				// only keep cards that are in front of a player
				.filter(c -> c.getPosition() instanceof InFrontOfPlayer)
				// only keep card in front of the right player
				.filter(c -> ((InFrontOfPlayer) c.getPosition()).isInFrontOf(player))
				.map(c -> c.getRole())
				.findAny()
				.orElseThrow();
	}
	
	public static Set<Player> getPlayersWithRole(Role role, Game game, Board board) {
		return getPlayersWithRole(role.getName(), game, board);
	}
	
	public static Set<Player> getPlayersWithRole(String role, Game game, Board board) {
		// search in all cards (take first player board are they are all the same)
		return board.getAllPlayerBoards().get(0).getCards()
				.stream()
				// find hunter card
				.filter(c -> c.getRole().isSame(role))
				// only keep cards that are in front of a player
				.filter(c -> c.getPosition() instanceof InFrontOfPlayer)
				// get the id of the mathing player
				.map(c -> ((InFrontOfPlayer) c.getPosition()).getPlayerId())
				.map(id -> getPlayerById(game, id))
				.collect(toSet());
	}

	public static Player getPlayerWithRole(String role, Game game, Board board) {
		return getPlayersWithRole(role, game, board)
				.stream()
				.findAny()
				.orElse(null);
	}
	
	public static Player getPlayerById(Game game, String playerId) {
		return game.getPlayers()
				.stream()
				.filter(p -> p.isSame(playerId))
				.findAny()
				.orElseThrow();
	}
}
