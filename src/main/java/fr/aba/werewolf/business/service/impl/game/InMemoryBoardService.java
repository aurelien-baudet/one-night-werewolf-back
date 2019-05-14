package fr.aba.werewolf.business.service.impl.game;

import static java.time.Duration.ofMillis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.InFrontOfPlayer;
import fr.aba.werewolf.business.domain.state.InTheMiddle;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.business.service.BoardListener;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.impl.game.config.TimingProperties;
import fr.aba.werewolf.business.service.impl.shuffle.Shuffler;
import fr.aba.werewolf.business.service.impl.uuid.UuidGenerator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InMemoryBoardService implements BoardService, ListenerManager<BoardListener> {
	private final UuidGenerator cardUuidGenerator;
	private final Shuffler<Card> shuffler;
	private final ListenerManager<BoardListener> delegate;
	private final TimingProperties timingConfig;
	private final Map<String, Board> boardsPerGameId;

	public InMemoryBoardService(UuidGenerator cardUuidGenerator, Shuffler<Card> shuffler, ListenerManager<BoardListener> delegate, TimingProperties timingConfig) {
		this(cardUuidGenerator, shuffler, delegate, timingConfig, new ConcurrentHashMap<>());
	}
	
	@Override
	public Board distributeCards(Game game) {
		// convert roles to cards to be placed in the board
		List<Card> cards = new ArrayList<>();
		for(Role role : game.getSelectedRoles()) {
			cards.add(new Card(cardUuidGenerator.generate(), role));
		}
		// shuffle cards
		List<Card> shuffledCards = shuffler.shuffle(cards);
		// distribute cards to players
		Iterator<Card> cardsIterator = shuffledCards.iterator();
		for(Player player : game.getPlayers()) {
			cardsIterator.next().setPosition(new InFrontOfPlayer(player));
		}
		// distribute cards in the middle
		int place = 0;
		while(cardsIterator.hasNext()) {
			cardsIterator.next().setPosition(new InTheMiddle(place++));
		}
		// prepare board for each player
		Map<String, PlayerBoard> playerBoards = new HashMap<>();
		for(Player player : game.getPlayers()) {
			Card card = getCardForPlayer(shuffledCards, player);
			Role originalRole = card.getRole();
			playerBoards.put(player.getId(), new PlayerBoard(player, originalRole, shuffledCards));
		}
		// prepare game board
		Board board = new Board(game.getGameOptions().getDiscussionDuration(), 
				ofMillis(timingConfig.getVoteDuration().toMillis()), 
				true,
				playerBoards);
		updateBoard(game, board);
		return board;
	}

	private Card getCardForPlayer(List<Card> cards, Player player) {
		Card card = cards.stream()
				.filter(c -> c.getPosition() instanceof InFrontOfPlayer)
				.filter(c -> ((InFrontOfPlayer) c.getPosition()).isInFrontOf(player))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("card not found for player "+player.getId()));
		return card;
	}

	@Override
	public Board getCurrentBoard(Game game) {
		return boardsPerGameId.get(game.getId());
	}

	@Override
	public Board updateBoard(Game game, Board board) {
		boardsPerGameId.put(game.getId(), board);
		notifyListeners(l -> l.boardUpdated(game, board));
		return board;
	}

	@Override
	public Board restart(Game game) {
		Board board = distributeCards(game);
		Board newBoard = updateBoard(game, board);
		notifyListeners(l -> l.boardResetted(game, newBoard));
		return newBoard;
	}

	@Override
	public void register(BoardListener listener) {
		delegate.register(listener);
	}

	@Override
	public void unregister(BoardListener listener) {
		delegate.unregister(listener);
	}

	@Override
	public void notifyListeners(Consumer<BoardListener> consumer) {
		delegate.notifyListeners(consumer);
	}

}
