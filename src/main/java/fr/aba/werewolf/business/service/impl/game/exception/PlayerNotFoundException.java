package fr.aba.werewolf.business.service.impl.game.exception;

import fr.aba.werewolf.business.service.GameException;
import lombok.Getter;

@Getter
public class PlayerNotFoundException extends GameException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String playerId;

	public PlayerNotFoundException(String message, String playerId) {
		super(message);
		this.playerId = playerId;
	}

}
