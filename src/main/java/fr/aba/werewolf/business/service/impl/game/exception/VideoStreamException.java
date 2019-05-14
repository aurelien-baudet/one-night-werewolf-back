package fr.aba.werewolf.business.service.impl.game.exception;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.service.GameException;
import lombok.Getter;

@Getter
public class VideoStreamException extends GameException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Game game;

	public VideoStreamException(String message, Game game) {
		super(message);
		this.game = game;
	}
	
	public VideoStreamException(String message, Game game, Throwable cause) {
		super(message, cause);
		this.game = game;
	}
	
}
