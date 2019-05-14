package fr.aba.werewolf.business.service.impl.game.exception;

import fr.aba.werewolf.business.domain.Game;

public class TokenGenerationException extends VideoStreamException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenGenerationException(Game game, Throwable cause) {
		super("Failed to generate token", game, cause);
	}

}
