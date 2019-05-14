package fr.aba.werewolf.business.service.impl.game;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.GameOptions;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.PlayersListener;
import fr.aba.werewolf.business.service.impl.game.config.RulesProperties;
import fr.aba.werewolf.business.service.impl.game.config.TimingProperties;
import fr.aba.werewolf.business.service.impl.game.exception.GameNotFoundException;
import fr.aba.werewolf.business.service.impl.game.exception.PlayerNotFoundException;
import fr.aba.werewolf.business.service.impl.uuid.UuidGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemoryGameService implements GameService, ListenerManager<PlayersListener> {
	private final UuidGenerator uuidGenerator;
	private final ListenerManager<PlayersListener> delegate;
	private final Map<String, Game> games;
	private final Map<String, Player> players;
	private final RulesProperties rules;
	private final TimingProperties timing;
	
	public InMemoryGameService(UuidGenerator uuidGenerator, ListenerManager<PlayersListener> delegate, RulesProperties rules, TimingProperties timing) {
		this(uuidGenerator, delegate, new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), rules, timing);
	}

	@Override
	public Game newGame(List<Role> selectedRoles, GameOptions options) {
		if(options == null) {
			options = new GameOptions(timing.getDefaultPauseDuration(), timing.getDefaultDiscussionDuration(), false);
		}
		Game game = new Game(uuidGenerator.generate(), new ArrayList<>(), selectedRoles == null ? new ArrayList<>() : selectedRoles, options, now());
		games.put(game.getId(), game);
		return game;
	}

	@Override
	public Game getGame(String gameId) throws GameException {
		if(!games.containsKey(gameId)) {
			throw new GameNotFoundException("The game with id='"+gameId+"' doesn't exist", gameId);
		}
		return games.get(gameId);
	}
	
	@Override
	public List<Game> getGames() {
		return games.values()
				.stream()
				.sorted((a, b) -> - a.getId().compareTo(b.getId()))
				.collect(toList());
	}

	@Override
	public void addPlayer(Game game, Player player) throws GameException {
		Game savedGame = getGame(game.getId());
		if(canAddPlayer(game, player)) {
			savedGame.getPlayers().add(player);
		}
		notifyListeners(l -> l.playersUpdated(savedGame, savedGame.getPlayers()));
	}

	@Override
	public void removePlayer(Game game, Player player) throws GameException {
		Game savedGame = getGame(game.getId());
		savedGame.getPlayers().remove(player);
		notifyListeners(l -> l.playersUpdated(savedGame, savedGame.getPlayers()));
	}

	@Override
	public List<Player> getPlayers(Game game) throws GameException {
		Game savedGame = getGame(game.getId());
		return savedGame.getPlayers();
	}

	@Override
	public Player newPlayer(String name) throws GameException {
		Player player = new Player(uuidGenerator.generate(), name);
		players.put(player.getId(), player);
		return player;
	}

	@Override
	public Player getPlayer(String playerId) throws GameException {
		if(!players.containsKey(playerId)) {
			throw new PlayerNotFoundException("The player with id='"+playerId+"' doesn't exist", playerId);
		}
		return players.get(playerId);
	}

	@Override
	public void register(PlayersListener listener) {
		this.delegate.register(listener);
	}

	@Override
	public void unregister(PlayersListener listener) {
		this.delegate.unregister(listener);
	}

	@Override
	public void notifyListeners(Consumer<PlayersListener> consumer) {
		this.delegate.notifyListeners(consumer);
	}


	private boolean canAddPlayer(Game game, Player player) {
		int numPlayers = game.getPlayers().size();
		// check valid number of players
		if(numPlayers < rules.getMinPlayers()) {
			return true;
		}
		if(numPlayers >= rules.getMaxPlayers()) {
			return false;
		}
		// check that there is the good number of players according to selected roles
		List<Role> selectedRoles = game.getSelectedRoles();
		int numCards = selectedRoles.size();
		if(numPlayers < numCards - rules.getCardsInTheMiddle()) {
			return true;
		}
		if(numPlayers >= numCards - rules.getCardsInTheMiddle()) {
			return false;
		}
		return false;
	}
}
