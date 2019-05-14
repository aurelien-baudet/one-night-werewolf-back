package fr.aba.werewolf.business.service.impl.game;

import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.GameOptions;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.repository.GameRepository;
import fr.aba.werewolf.business.repository.PlayerRepository;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.PlayersListener;
import fr.aba.werewolf.business.service.impl.game.config.RulesProperties;
import fr.aba.werewolf.business.service.impl.game.config.TimingProperties;
import fr.aba.werewolf.business.service.impl.game.exception.GameNotFoundException;
import fr.aba.werewolf.business.service.impl.game.exception.PlayerNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimpleGameService implements GameService, ListenerManager<PlayersListener> {
	private final ListenerManager<PlayersListener> delegate;
	private final GameRepository gameRepository;
	private final PlayerRepository playerRepository;
	private final RulesProperties rules;
	private final TimingProperties timing;
	
	@Override
	public Game newGame(List<Role> selectedRoles, GameOptions options) {
		if(options == null) {
			options = new GameOptions(timing.getDefaultPauseDuration(), timing.getDefaultDiscussionDuration(), false);
		}
		Game game = new Game(new ArrayList<>(), selectedRoles == null ? new ArrayList<>() : selectedRoles, options);
		return gameRepository.save(game);
	}

	@Override
	public Game getGame(String gameId) throws GameException {
		return gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException("The game with id='"+gameId+"' doesn't exist", gameId));
	}
	
	@Override
	public List<Game> getGames() {
		return gameRepository.findAllJoinable(Sort.by(DESC, "createdDate"));
	}

	@Override
	public void addPlayer(Game game, Player player) throws GameException {
		Game savedGame = getGame(game.getId());
		if(canAddPlayer(game, player)) {
			savedGame.getPlayers().add(player);
		}
		Game updatedGame = gameRepository.save(savedGame);
		notifyListeners(l -> l.playersUpdated(savedGame, updatedGame.getPlayers()));
	}

	@Override
	public void removePlayer(Game game, Player player) throws GameException {
		Game savedGame = getGame(game.getId());
		savedGame.getPlayers().remove(player);
		Game updatedGame = gameRepository.save(savedGame);
		notifyListeners(l -> l.playersUpdated(savedGame, updatedGame.getPlayers()));
	}

	@Override
	public List<Player> getPlayers(Game game) throws GameException {
		Game savedGame = getGame(game.getId());
		return savedGame.getPlayers();
	}

	@Override
	public Player newPlayer(String name) throws GameException {
		Player player = new Player(name);
		return playerRepository.save(player);
	}

	@Override
	public Player getPlayer(String playerId) throws GameException {
		return playerRepository.findById(playerId).orElseThrow(() -> new PlayerNotFoundException("The player with id='"+playerId+"' doesn't exist", playerId));
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
