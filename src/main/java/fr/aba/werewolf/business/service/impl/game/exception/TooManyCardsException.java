package fr.aba.werewolf.business.service.impl.game.exception;

import lombok.Getter;

@Getter
public class TooManyCardsException extends GameStartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private final int numCards;
	private final int numPlayers;
	private final int cardsInTheMiddle;

	public TooManyCardsException(int numCards, int numPlayers, int cardsInTheMiddle) {
		super("There are too cards or not enough players to start the game");
		this.numCards = numCards;
		this.numPlayers = numPlayers;
		this.cardsInTheMiddle = cardsInTheMiddle;
	}

}
