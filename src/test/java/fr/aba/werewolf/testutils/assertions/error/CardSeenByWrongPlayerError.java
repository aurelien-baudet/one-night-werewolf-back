package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class CardSeenByWrongPlayerError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Player player;
	private final PlayerBoard playerBoard;
	private final String expectedSeenCardId;
	
	public CardSeenByWrongPlayerError(Player player, PlayerBoard playerBoard, String expectedSeenCardId) {
		super("The card "+expectedSeenCardId+" should not be seen by player "+player, playerBoard);
		this.player = player;
		this.playerBoard = playerBoard;
		this.expectedSeenCardId = expectedSeenCardId;
	}
	
	
}
