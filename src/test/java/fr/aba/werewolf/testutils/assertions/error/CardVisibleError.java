package fr.aba.werewolf.testutils.assertions.error;

import java.util.List;

import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class CardVisibleError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PlayerBoard playerBoard;
	private final List<Card> visibleCards;
	
	public CardVisibleError(PlayerBoard playerBoard, List<Card> visibleCards) {
		super("The cards "+visibleCards+" are visible but they shouldn't be visible", playerBoard);
		this.playerBoard = playerBoard;
		this.visibleCards = visibleCards;
	}
	
}
