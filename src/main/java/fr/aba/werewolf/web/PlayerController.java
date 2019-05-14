package fr.aba.werewolf.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.PlayersListener;
import fr.aba.werewolf.web.dto.NewPlayer;

@Controller
public class PlayerController implements PlayersListener {
	@Autowired
	SimpMessagingTemplate template;
	@Autowired
	GameService gameService;
	
	
	@MessageMapping("games/{gameId}/players")
	@SendTo("/topic/games/{gameId}/players")
	public List<Player> getPlayers(@DestinationVariable("gameId") String gameId) throws GameException {
		return gameService.getPlayers(gameService.getGame(gameId));
	}

	@MessageMapping("games/{gameId}/players/add")
	@SendTo("/topic/games/{gameId}/players/add")
	public Player addPlayer(@DestinationVariable("gameId") String gameId, @Payload NewPlayer player) throws GameException {
		Player newPlayer = gameService.newPlayer(player.getName());
		gameService.addPlayer(gameService.getGame(gameId), newPlayer);
		return newPlayer;
	}

	@MessageMapping("games/{gameId}/players/{playerId}/remove")
	@SendTo("/topic/games/{gameId}/players/{playerId}/remove")
	public void removePlayer(@DestinationVariable("gameId") String gameId, @DestinationVariable("playerId") String playerId) throws GameException {
		gameService.removePlayer(gameService.getGame(gameId), gameService.getPlayer(playerId));
	}
	

	public void playersUpdated(Game game, List<Player> players) {
		template.convertAndSend("/topic/games/"+game.getId()+"/players", players);
	}
}
