package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class CardInTheMiddleAtWrongPlaceError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PlayerBoard playerBoard;
	private final Card card;
	private final int expectedPosition;
	
	public CardInTheMiddleAtWrongPlaceError(PlayerBoard playerBoard, Card card, int expectedPlace) {
		super("The card with role '"+card.getRole()+"' (id="+card.getId()+") should be in the middle (place "+expectedPlace+") but was "+card.getPosition(), playerBoard);
		this.playerBoard = playerBoard;
		this.card = card;
		this.expectedPosition = expectedPlace;
	}
	
	
}
