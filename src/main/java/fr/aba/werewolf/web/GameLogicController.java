package fr.aba.werewolf.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.service.ActionService;
import fr.aba.werewolf.business.service.BoardListener;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameLogicService;
import fr.aba.werewolf.business.service.GameRunner;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.impl.game.exception.PlayerNotFoundException;

@Controller
public class GameLogicController implements BoardListener {
	@Autowired
	SimpMessagingTemplate template;
	@Autowired
	ActionService actionService;
	@Autowired
	GameService gameService;
	@Autowired
	GameLogicService gameLogicService;
	@Autowired
	BoardService boardService;
	@Autowired
	GameRunner runner;

	
	@MessageMapping("games/{gameId}/board")
	@SendTo("/topic/games/{gameId}/board")
	public Board getBoard(@DestinationVariable("gameId") String gameId) throws GameException {
		Game game = gameService.getGame(gameId);
		return boardService.getCurrentBoard(game);
	}
	
	@MessageMapping("games/{gameId}/players/{playerId}/play")
	public void play(@DestinationVariable("gameId") String gameId, @DestinationVariable("playerId") String playerId, @Payload Action action) throws GameException {
		Game game = gameService.getGame(gameId);
		Player player = game.getPlayers()
				.stream()
				.filter(p -> p.isSame(playerId)).findFirst()
				.orElseThrow(() -> new PlayerNotFoundException("The player with id="+playerId+" doesn't exist", playerId));
		Board board = boardService.getCurrentBoard(game);
		Role as = board.getBoardForPlayer(player).getOriginalRole();
		actionService.play(game, player, as, action);
	}
	
	@MessageMapping("games/{gameId}/distribute")
	public void distribute(@DestinationVariable("gameId") String gameId) throws GameException {
		gameLogicService.distribute(gameService.getGame(gameId));
	}
	
	@MessageMapping("games/{gameId}/start")
	public void start(@DestinationVariable("gameId") String gameId) throws GameException {
		runner.run(gameService.getGame(gameId));
	}

	@Override
	public void boardUpdated(Game game, Board board) {
		template.convertAndSend("/topic/games/"+game.getId()+"/board", board);
	}

	@Override
	public void boardResetted(Game game, Board board) {
		template.convertAndSend("/topic/games/"+game.getId()+"/restarted", board);
	}	
}
