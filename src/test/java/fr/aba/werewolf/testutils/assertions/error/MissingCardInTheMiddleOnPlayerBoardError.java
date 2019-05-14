package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class MissingCardInTheMiddleOnPlayerBoardError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PlayerBoard playerBoard;
	private final Card expectedCard;
	
	public MissingCardInTheMiddleOnPlayerBoardError(PlayerBoard playerBoard, Card card) {
		super("The card with role '"+card.getRole()+"' (id="+card.getId()+") should be on the board but the board doesn't contain this card", playerBoard);
		this.playerBoard = playerBoard;
		this.expectedCard = card;
	}
	
}
