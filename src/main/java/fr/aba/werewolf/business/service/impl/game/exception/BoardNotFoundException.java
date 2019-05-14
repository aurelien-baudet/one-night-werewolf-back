package fr.aba.werewolf.business.service.impl.game.exception;

import fr.aba.werewolf.business.service.GameException;
import lombok.Getter;

@Getter
public class BoardNotFoundException extends GameException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String boardId;

	public BoardNotFoundException(String message, String boardId) {
		super(message);
		this.boardId = boardId;
	}

}
