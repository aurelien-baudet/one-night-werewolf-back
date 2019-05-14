package fr.aba.werewolf.testutils.assertions;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.testutils.assertions.error.CardVisibleError;

public class AllCardsAssertion<P> extends ParentAssertion<P> {
	private final List<PlayerBoard> boardsToCheck;
	
	public AllCardsAssertion(P parentAssertion, Board board) {
		super(parentAssertion);
		this.boardsToCheck = board.getAllPlayerBoards();
	}
	
	public AllCardsAssertion(P parentAssertion, List<PlayerBoard> boardsToCheck) {
		super(parentAssertion);
		this.boardsToCheck = boardsToCheck;
	}
	
	public AllCardsAssertion(P parentAssertion, PlayerBoard... boardsToCheck) {
		this(parentAssertion, Arrays.asList(boardsToCheck));
	}

	public AllCardsAssertion<P> hidden() {
		AssertionUtils.checkForEachPlayer(boardsToCheck, this::checkAllCardsHidden);
		return this;
	}
	
	private void checkAllCardsHidden(PlayerBoard playerBoard) {
		List<Card> visibleCards = playerBoard.getCards()
				.stream()
				.filter(card -> card.isVisible())
				.collect(toList());
		if(!visibleCards.isEmpty()) {
			throw new CardVisibleError(playerBoard, visibleCards);
		}
	}
}
