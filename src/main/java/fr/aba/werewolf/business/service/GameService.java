package fr.aba.werewolf.business.service;

import java.util.List;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.GameOptions;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;

public interface GameService {
	Game newGame(List<Role> selectedRoles, GameOptions options) throws GameException;
	Game getGame(String gameId) throws GameException;
	List<Game> getGames();
	Player newPlayer(String name) throws GameException;
	Player getPlayer(String playerId) throws GameException;
	List<Player> getPlayers(Game game) throws GameException;
	void addPlayer(Game game, Player player) throws GameException;
	void removePlayer(Game game, Player player) throws GameException;
}
