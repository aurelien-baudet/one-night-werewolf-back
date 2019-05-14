package fr.aba.werewolf.business.service.impl.game.exception;

import lombok.Getter;

@Getter
public class TooManyPlayersException extends GameStartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private final int maxPlayers;
	private final int numPlayers;

	public TooManyPlayersException(int maxPlayers, int numPlayers) {
		super("There are too many players to start the game");
		this.maxPlayers = maxPlayers;
		this.numPlayers = numPlayers;
	}

}
