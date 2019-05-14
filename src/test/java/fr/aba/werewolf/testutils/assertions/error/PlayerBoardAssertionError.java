package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.state.PlayerBoard;
import junit.framework.AssertionFailedError;
import lombok.Getter;

@Getter
public class PlayerBoardAssertionError extends AssertionFailedError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PlayerBoard playerBoard;
	
	public PlayerBoardAssertionError(String message, PlayerBoard playerBoard) {
		super("[board for player "+playerBoard.getPlayerId()+"] "+message);
		this.playerBoard = playerBoard;
	}
}
