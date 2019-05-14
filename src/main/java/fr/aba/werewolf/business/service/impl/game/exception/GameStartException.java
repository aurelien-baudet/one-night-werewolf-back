package fr.aba.werewolf.business.service.impl.game.exception;

import fr.aba.werewolf.business.service.GameException;

public class GameStartException extends GameException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameStartException(String message, Throwable cause) {
		super(message, cause);
	}

	public GameStartException(String message) {
		super(message);
	}

	public GameStartException(Throwable cause) {
		super(cause);
	}

}
