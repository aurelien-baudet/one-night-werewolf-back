package fr.aba.werewolf.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Vote;
import fr.aba.werewolf.business.domain.VoteResult;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.VoteService;
import fr.aba.werewolf.business.service.VotesListener;

@Controller
public class VoteController implements VotesListener {
	@Autowired
	GameService gameService;
	@Autowired
	VoteService voteService;
	@Autowired
	SimpMessagingTemplate template;

	
	@MessageMapping("games/{gameId}/votes/vote")
	@SendTo("/topic/games/{gameId}/votes/vote")
	public Vote vote(@DestinationVariable("gameId") String gameId, @Payload Vote vote) throws GameException {
		return voteService.vote(gameService.getGame(gameId), gameService.getPlayer(vote.getVoterId()), gameService.getPlayer(vote.getAgainstId()));
	}

	@MessageMapping("games/{gameId}/votes/list")
	@SendTo("/topic/games/{gameId}/votes/list")
	public Set<Vote> list(@DestinationVariable("gameId") String gameId) throws GameException {
		return voteService.getVotes(gameService.getGame(gameId));
	}

	@MessageMapping("games/{gameId}/votes/has-everyone-voted")
	@SendTo("/topic/games/{gameId}/votes/has-everyone-voted")
	public boolean hasEveryoneVoted(@DestinationVariable("gameId") String gameId) throws GameException {
		return voteService.hasEveryoneVoted(gameService.getGame(gameId));
	}

	@MessageMapping("games/{gameId}/votes/result")
	@SendTo("/topic/games/{gameId}/votes/result")
	public VoteResult result(@DestinationVariable("gameId") String gameId) throws GameException {
		return voteService.computeDeadsAndWinners(gameService.getGame(gameId));
	}

	@Override
	public void votesUpdated(Game game, Set<Vote> votes) {
		template.convertAndSend("/topic/games/"+game.getId()+"/votes/list", votes);
	}

}
