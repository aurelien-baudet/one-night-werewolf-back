package fr.aba.werewolf.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.video.PlayerMetadata;
import fr.aba.werewolf.business.domain.video.SessionMetadata;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.VideoStreamListener;
import fr.aba.werewolf.business.service.VideoStreamService;
import fr.aba.werewolf.business.service.impl.game.exception.PlayerNotFoundException;

@Controller
public class VideoStreamController implements VideoStreamListener {
	@Autowired
	SimpMessagingTemplate template;
	@Autowired
	GameService gameService;
	@Autowired
	VideoStreamService videoService;

	@MessageMapping("games/{gameId}/streams/connect/{group}")
	@SendTo("/topic/games/{gameId}/streams/connect/{group}")
	public SessionMetadata connect(@DestinationVariable("gameId") String gameId, @DestinationVariable("group") String group) throws GameException {
		Game game = gameService.getGame(gameId);
		return videoService.connect(game, group);
	}

	@MessageMapping("games/{gameId}/streams/add/{group}/{playerId}")
	@SendTo("/topic/games/{gameId}/streams/add/{group}/{playerId}")
	public PlayerMetadata connect(@DestinationVariable("gameId") String gameId, @DestinationVariable("group") String group, @DestinationVariable("playerId") String playerId) throws GameException {
		Game game = gameService.getGame(gameId);
		return videoService.registerPlayer(game, group, getPlayer(game, playerId));
	}

	@MessageMapping("games/{gameId}/streams")
	@SendTo("/topic/games/{gameId}/streams")
	public Set<PlayerMetadata> getStreams(@DestinationVariable("gameId") String gameId) throws GameException {
		return videoService.getMetadataForPlayers(gameService.getGame(gameId));
	}
	
	@MessageMapping("games/{gameId}/streams/disconnect/{group}")
	@SendTo("/topic/games/{gameId}/streams/disconnect/{group}")
	public void disconnect(@DestinationVariable("gameId") String gameId, @DestinationVariable("group") String group) throws GameException {
		Game game = gameService.getGame(gameId);
		videoService.disconnect(game, group);
	}

	@Override
	public void streamsUpdated(Game game, Set<PlayerMetadata> meta) {
		template.convertAndSend("/topic/games/"+game.getId()+"/streams", meta);
	}

	private Player getPlayer(Game game, String playerId) throws PlayerNotFoundException {
		return game.getPlayers().stream()
				.filter(p -> p.isSame(playerId))
				.findAny()
				.orElseThrow(() -> new PlayerNotFoundException("The player with id="+playerId+" doesn't exist for game with id="+game.getId(), playerId));
	}
}
