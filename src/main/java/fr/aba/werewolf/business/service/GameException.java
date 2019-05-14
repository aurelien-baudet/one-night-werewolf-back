package fr.aba.werewolf.business.service;

public abstract class GameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameException(String message, Throwable cause) {
		super(message, cause);
	}

	public GameException(String message) {
		super(message);
	}

	public GameException(Throwable cause) {
		super(cause);
	}
	
}
