package fr.aba.werewolf.business.service.impl.game.exception;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Vote;
import lombok.Getter;

@Getter
public class MissingVotesException extends GameStartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Game game;
	private final Set<Vote> currentVotes;
	
	
	public MissingVotesException(Game game, Set<Vote> currentVotes) {
		this("There are still some votes missing", game, currentVotes);
	}
	
	public MissingVotesException(String message, Game game, Set<Vote> currentVotes) {
		super(message);
		this.game = game;
		this.currentVotes = currentVotes;
	}
	
}
