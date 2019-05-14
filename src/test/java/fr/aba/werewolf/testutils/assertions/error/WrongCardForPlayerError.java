package fr.aba.werewolf.testutils.assertions.error;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.Getter;

@Getter
public class WrongCardForPlayerError extends PlayerBoardAssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Player player;
	private final PlayerBoard playerBoard;
	private final Card expectedCard;
	
	public WrongCardForPlayerError(Player player, PlayerBoard playerBoard, Card expectedCard) {
		super("The player '"+player.getName()+"' (id="+player.getId()+") should have the card with role '"+expectedCard.getRole()+"' (id="+expectedCard.getId()+") but he has the card '"+playerBoard.getCurrentCard().getRole()+"' (id="+playerBoard.getCurrentCard().getId()+") instead", playerBoard);
		this.player = player;
		this.playerBoard = playerBoard;
		this.expectedCard = expectedCard;
	}
	
	
}
