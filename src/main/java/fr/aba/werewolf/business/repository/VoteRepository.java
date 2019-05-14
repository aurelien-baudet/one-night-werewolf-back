package fr.aba.werewolf.business.repository;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.aba.werewolf.business.domain.SavedVote;

public interface VoteRepository extends MongoRepository<SavedVote, String> {
	Set<SavedVote> findByGameId(String gameId);
	
	void deleteByGameIdAndVoteVoterId(String gameId, String voterId);

	void deleteByGameId(String id);
}
