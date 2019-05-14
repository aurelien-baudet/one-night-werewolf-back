package fr.aba.werewolf.testutils.assertions;

import static fr.aba.werewolf.testutils.assertions.AssertionUtils.checkForEachPlayer;

import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.testutils.assertions.error.CardInTheMiddleAtWrongPlaceError;
import fr.aba.werewolf.testutils.assertions.error.CardNotInTheMiddleError;
import fr.aba.werewolf.testutils.assertions.error.MissingCardInTheMiddleOnPlayerBoardError;

public class CardsInTheMiddleAssertion extends ParentAssertion<BoardAssertion> {
	private final Board board;
	
	public CardsInTheMiddleAssertion(BoardAssertion parent, Board board) {
		super(parent);
		this.board = board;
	}
	
	public CardsInTheMiddleAssertion place(Card card, int expectedPlace) {
		checkForEachPlayer(board, playerBoard -> checkCardPresentInTheMiddleForPlayer(playerBoard, card));
		checkForEachPlayer(board, playerBoard -> checkCardAtTheRightPlaceForPlayer(playerBoard, card, expectedPlace));
		return this;
	}

	public CardsInTheMiddleAssertion place(String cardId, int expectedPlace) {
		checkForEachPlayer(board, playerBoard -> checkCardPresentInTheMiddleForPlayer(playerBoard, playerBoard.getCardById(cardId)));
		checkForEachPlayer(board, playerBoard -> checkCardAtTheRightPlaceForPlayer(playerBoard, playerBoard.getCardById(cardId), expectedPlace));
		return this;
	}


	private void checkCardPresentInTheMiddleForPlayer(PlayerBoard playerBoard, Card card) {
		if(playerBoard.getCardById(card.getId()) == null) {
			throw new MissingCardInTheMiddleOnPlayerBoardError(playerBoard, card);
		}
		if(!playerBoard.getCardsInTheMiddle().contains(card)) {
			throw new CardNotInTheMiddleError(playerBoard, card);
		}
	}
	
	private void checkCardAtTheRightPlaceForPlayer(PlayerBoard playerBoard, Card card, int expectedPlace) {
		int idx = playerBoard.getCardsInTheMiddle().indexOf(card);
		Card found = playerBoard.getCardsInTheMiddle().get(idx);
		if(!found.getPosition().equals(card.getPosition())) {
			throw new CardInTheMiddleAtWrongPlaceError(playerBoard, card, expectedPlace);
		}
	}
}
