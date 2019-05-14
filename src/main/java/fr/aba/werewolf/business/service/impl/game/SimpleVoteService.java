package fr.aba.werewolf.business.service.impl.game;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.SavedVote;
import fr.aba.werewolf.business.domain.Vote;
import fr.aba.werewolf.business.domain.VoteResult;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.repository.VoteRepository;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.VoteService;
import fr.aba.werewolf.business.service.VotesListener;
import fr.aba.werewolf.business.service.impl.game.exception.MissingVotesException;
import fr.aba.werewolf.business.service.impl.game.solver.DeadsSolver;
import fr.aba.werewolf.business.service.impl.game.solver.WinnersSolver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class SimpleVoteService implements VoteService, ListenerManager<VotesListener> {
	private final DeadsSolver deadsSolver;
	private final WinnersSolver winnersSolver;
	private final BoardService boardService;
	private final ListenerManager<VotesListener> delegate;
	private final VoteRepository voteRepository;
	

	@Override
	public Set<Vote> getVotes(Game game) {
		return getVotesFor(game);
	}
	
	@Override
	public boolean hasEveryoneVoted(Game game) {
		return getVotesFor(game).size() >= game.getPlayers().size();
	}

	@Override
	public Vote vote(Game game, Player voter, Player against) {
		log.info("[{}] {} has voted against {}", game.getId(), voter.getName(), against.getName());
		Vote vote = getVoteVotedBy(game, voter);
		// a voter may change his vote
		if(vote != null) {
			removeVote(game, vote);
		}
		Vote newVote = new Vote(voter.getId(), against.getId());
		addVote(game, newVote);
		notifyListeners((l) -> l.votesUpdated(game, getVotesFor(game)));
		return newVote;
	}

	@Override
	public VoteResult computeDeadsAndWinners(Game game) throws GameException {
		if(!hasEveryoneVoted(game)) {
			throw new MissingVotesException(game, getVotesFor(game));
		}
		Board board = boardService.getCurrentBoard(game);
		Set<Vote> votes = getVotes(game);
		Set<Player> deads = deadsSolver.deads(game, board, votes);
		Set<Player> winners = winnersSolver.computeWinners(game, board, deads);
		Set<Player> loosers = game.getPlayers()
				.stream()
				.filter(p -> !winners.contains(p))
				.collect(toSet());
		return new VoteResult(votes, deads, winners, loosers);
	}


	@Override
	public void clean(Game game) {
		voteRepository.deleteByGameId(game.getId());
	}

	@Override
	public void register(VotesListener listener) {
		this.delegate.register(listener);
	}

	@Override
	public void unregister(VotesListener listener) {
		this.delegate.unregister(listener);
	}

	@Override
	public void notifyListeners(Consumer<VotesListener> consumer) {
		this.delegate.notifyListeners(consumer);
	}


	private Vote getVoteVotedBy(Game game, Player voter) {
		Set<Vote> votes = getVotesFor(game);
		return votes.stream()
				.filter(v -> v.isVotedBy(voter))
				.findAny()
				.orElse(null);
	}

	private Set<Vote> getVotesFor(Game game) {
		Set<SavedVote> votes = voteRepository.findByGameId(game.getId());
		if(votes == null) {
			votes = new HashSet<>();
		}
		return votes.stream().map(SavedVote::getVote).collect(toSet());
	}

	private void addVote(Game game, Vote newVote) {
		voteRepository.save(new SavedVote(game.getId(), newVote));
	}
	
	private void removeVote(Game game, Vote vote) {
		voteRepository.deleteByGameIdAndVoteVoterId(game.getId(), vote.getVoterId());
	}

}
