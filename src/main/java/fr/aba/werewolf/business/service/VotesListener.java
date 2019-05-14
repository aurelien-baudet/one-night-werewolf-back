package fr.aba.werewolf.business.service;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Vote;

public interface VotesListener {
	void votesUpdated(Game game, Set<Vote> votes);
}
