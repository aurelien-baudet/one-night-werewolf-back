package fr.aba.werewolf.testutils.assertions;

import static fr.aba.werewolf.testutils.assertions.AssertionUtils.checkForEachPlayer;
import static java.util.Arrays.asList;

import java.util.List;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.InFrontOfPlayer;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.testutils.assertions.error.CardNotFoundError;
import fr.aba.werewolf.testutils.assertions.error.CardNotSeenByPlayerError;
import fr.aba.werewolf.testutils.assertions.error.CardSeenByWrongPlayerError;
import fr.aba.werewolf.testutils.assertions.error.MissingCardOnPlayerBoardError;
import fr.aba.werewolf.testutils.assertions.error.WrongCardForPlayerError;
import junit.framework.AssertionFailedError;

public class PlayerAssertion extends ParentAssertion<BoardAssertion> {
	private final Board board;
	private final Player player;
	

	public PlayerAssertion(BoardAssertion parent, Board board, Player player) {
		super(parent);
		this.board = board;
		this.player = player;
	}

	public PlayerAssertion hasCard(Card expectedCard) {
		checkForEachPlayer(board, playerBoard -> checkCardForPlayer(player, playerBoard, expectedCard, expectedCard.getId()));
		return this;
	}

	public PlayerAssertion hasCard(String expectedCardId) {
		checkForEachPlayer(board, playerBoard -> checkCardForPlayer(player, playerBoard, playerBoard.getCardById(expectedCardId), expectedCardId));
		return this;
	}

	public PlayerAssertion sees(String... seenCardIds) {
		checkForEachPlayer(board, playerBoard -> checkCardSeen(player, playerBoard, asList(seenCardIds)));
		return this;
	}

	public PlayerAssertion doesNotSee(List<String> seenCardIds) {
//		checkForEachPlayer(board, playerBoard -> checkCardNotSeen(player, playerBoard, seenCardIds));
		checkCardNotSeen(player, board.getBoardForPlayer(player), seenCardIds);
		return this;
	}

	public PlayerAssertion doesNotSee(String... seenCardIds) {
		doesNotSee(asList(seenCardIds));
		return this;
	}

	public AllCardsAssertion<PlayerAssertion> cards() {
		return new AllCardsAssertion<>(this, board.getBoardForPlayer(player));
	}
	
	private void checkCardForPlayer(Player player, PlayerBoard playerBoard, Card expectedCard, String expectedCardId) throws AssertionFailedError {
		if(expectedCard == null) {
			throw new CardNotFoundError(player, playerBoard, expectedCardId);
		}
		if(playerBoard.getCardById(expectedCard.getId()) == null) {
			throw new MissingCardOnPlayerBoardError(player, playerBoard, expectedCard);
		}
		// ensure that for any player board the position is right
		if(isCardInFrontOfPlayer(player, playerBoard)) {
			throw new WrongCardForPlayerError(player, playerBoard, expectedCard);
		}
		// ensure that getCurrentCard() returns the right value for the player
		if(isCurrentPlayerBoard(player, playerBoard) && !isCurrentPlayerCard(playerBoard, expectedCard)) {
			throw new WrongCardForPlayerError(player, playerBoard, expectedCard);
		}
	}

	private boolean isCardInFrontOfPlayer(Player player, PlayerBoard playerBoard) {
		return playerBoard.getCards().stream().noneMatch(c -> c.getPosition().equals(new InFrontOfPlayer(player)));
	}

	private boolean isCurrentPlayerCard(PlayerBoard playerBoard, Card expectedCard) {
		return playerBoard.getCurrentCard().equals(expectedCard);
	}

	private boolean isCurrentPlayerBoard(Player player, PlayerBoard playerBoard) {
		return player.getId().equals(playerBoard.getPlayerId());
	}

	private void checkCardSeen(Player player, PlayerBoard playerBoard, List<String> seenCardIds) {
		// ensure that player sees all cards he should see
		for(String seenCardId : seenCardIds) {
			if(isCurrentPlayerBoard(player, playerBoard) && !playerBoard.getCardById(seenCardId).isVisible()) {
				throw new CardNotSeenByPlayerError(player, playerBoard, seenCardId);
			}
			if(!isCurrentPlayerBoard(player, playerBoard) && playerBoard.getCardById(seenCardId).isVisible()) {
				throw new CardSeenByWrongPlayerError(player, playerBoard, seenCardId);
			}
		}
	}

	private void checkCardNotSeen(Player player, PlayerBoard playerBoard, List<String> notSeenCardIds) {
		// ensure that player can't see the cards
		for(String notSeenCardId : notSeenCardIds) {
			if(playerBoard.getCardById(notSeenCardId).isVisible()) {
				throw new CardSeenByWrongPlayerError(player, playerBoard, notSeenCardId);
			}
		}
	}
}
