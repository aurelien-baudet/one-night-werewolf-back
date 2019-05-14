package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class CardNotInTheMiddleError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final PlayerBoard playerBoard;
	private final Card expectedCard;

	public CardNotInTheMiddleError(PlayerBoard playerBoard, Card card) {
		super("The card with role '"+card.getRole()+"' (id="+card.getId()+") should be in the middle but is placed "+card.getPosition()+" instead", playerBoard);
		this.playerBoard = playerBoard;
		this.expectedCard = card;
	}
}
