package fr.aba.werewolf.business.service.impl.game.exception;

import fr.aba.werewolf.business.service.GameException;
import lombok.Getter;

@Getter
public class GameNotFoundException extends GameException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String gameId;

	public GameNotFoundException(String message, String gameId) {
		super(message);
		this.gameId = gameId;
	}

}
