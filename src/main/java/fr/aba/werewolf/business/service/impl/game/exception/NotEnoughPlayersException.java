package fr.aba.werewolf.business.service.impl.game.exception;

import lombok.Getter;

@Getter
public class NotEnoughPlayersException extends GameStartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private final int minPlayers;
	private final int numPlayers;
	
	public NotEnoughPlayersException(int minPlayers, int numPlayers) {
		super("There are not enough players to start the game");
		this.minPlayers = minPlayers;
		this.numPlayers = numPlayers;
	}
}
