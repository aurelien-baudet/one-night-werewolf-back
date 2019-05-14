package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class MissingCardOnPlayerBoardError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Player player;
	private final PlayerBoard playerBoard;
	private final Card expectedCard;
	
	public MissingCardOnPlayerBoardError(Player player, PlayerBoard playerBoard, Card expectedCard) {
		super("The player '"+player.getName()+"' (id="+player.getId()+") should have the card with role '"+expectedCard.getRole()+"' (id="+expectedCard.getId()+") but there is no card with this role", playerBoard);
		this.player = player;
		this.playerBoard = playerBoard;
		this.expectedCard = expectedCard;
	}
	
	
}
