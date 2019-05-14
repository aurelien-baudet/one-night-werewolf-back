package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class CardNotFoundError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Player player;
	private final PlayerBoard playerBoard;
	private final String expectedCardId;
	
	public CardNotFoundError(Player player, PlayerBoard playerBoard, String expectedCardId) {
		super("The player '"+player.getName()+"' (id="+player.getId()+") should have the card with id='"+expectedCardId+" but there is no card with this id", playerBoard);
		this.player = player;
		this.playerBoard = playerBoard;
		this.expectedCardId = expectedCardId;
	}
	
	
}
