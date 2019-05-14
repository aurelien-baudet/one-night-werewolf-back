package fr.aba.werewolf.business.service;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Vote;
import fr.aba.werewolf.business.domain.VoteResult;

public interface VoteService {

	Set<Vote> getVotes(Game game);
	boolean hasEveryoneVoted(Game game);
	Vote vote(Game game, Player voter, Player against) throws GameException;
	VoteResult computeDeadsAndWinners(Game game) throws GameException;
	void clean(Game game);

}
