package fr.aba.werewolf.business.service.impl.game.solver;

import static fr.aba.werewolf.business.service.impl.game.solver.SolverUtils.getPlayerById;
import static fr.aba.werewolf.business.service.impl.game.solver.SolverUtils.getPlayerWithRole;
import static fr.aba.werewolf.business.service.impl.game.solver.SolverUtils.getRoleOf;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Vote;
import fr.aba.werewolf.business.domain.state.Board;

@Service
public class StandardGameDeadsSolver implements DeadsSolver {

	@Override
	public Set<Player> deads(Game game, Board board, Set<Vote> votes) {
		Set<Player> deads = new HashSet<>();
		// If no player receives more than one vote, no one dies
		if(everyoneReceivedOnlyOneVote(votes)) {
			return emptySet();
		}
		// The player with the most votes dies
		// In case of a tie, all players tied with the most votes die
		deads.addAll(getPlayersWithMostVotes(votes, game));
		// If hunter dies
		// Each player that has voted against the hunter dies as well
		if(isHunterDead(deads, game, board)) {
			for(Player player : game.getPlayers()) {
				if(isPointedByHunter(player, votes, game, board)) {
					deads.add(player);
				}
			}
		}
		return deads;
	}

	private boolean everyoneReceivedOnlyOneVote(Set<Vote> votes) {
		return numberOfVotesAgainstOnePlayer(votes).allMatch(numVotes -> numVotes == 1);
	}

	private Stream<Integer> numberOfVotesAgainstOnePlayer(Set<Vote> votes) {
		return groupVotesByAgainstId(votes).entrySet()
				.stream()
				.map(e -> e.getValue().size());
	}

	private boolean isPointedByHunter(Player player, Set<Vote> votes, Game game, Board board) {
		Player hunter = getPlayerWithRole("hunter", game, board);
		// if no hunter played by anyone
		// => can't point to anyone
		if(hunter == null) {
			return false;
		}
		Vote hunterVote = getVoteOf(hunter, votes);
		return hunterVote.isVoteAgainst(player);
	}

	

	private Set<Player> getPlayersWithMostVotes(Set<Vote> votes, Game game) {
		int maxVotes = numberOfVotesAgainstOnePlayer(votes)
				.max(Integer::compare)
				.orElse(0);
		return groupVotesByAgainstId(votes).entrySet()
				.stream()
				.filter(e -> e.getValue().size() == maxVotes)
				.map(Entry::getKey)
				.map(id -> getPlayerById(game, id))
				.collect(toSet());
	}

	private Map<String, List<Vote>> groupVotesByAgainstId(Set<Vote> votes) {
		return votes.stream()
				.collect(groupingBy(Vote::getAgainstId));
	}
	
	private Vote getVoteOf(Player player, Set<Vote> votes) {
		return votes
				.stream()
				.filter(v -> v.isVotedBy(player))
				.findAny()
				.orElseThrow();
	}
	
	private boolean isHunterDead(Set<Player> deads, Game game, Board board) {
		return deads
				.stream()
				.map(p -> getRoleOf(p, board))
				.anyMatch(r -> r.is("hunter"));
	}
}
