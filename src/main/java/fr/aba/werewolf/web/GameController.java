package fr.aba.werewolf.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameRunner;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.web.dto.NewGame;

@RestController
@RequestMapping("games")
public class GameController {
	@Autowired
	GameService gameService;
	@Autowired
	GameRunner runner;

	@PostMapping
	public Game newGame(@RequestBody NewGame game) throws GameException {
		return gameService.newGame(game.getSelectedRoles(), game.getOptions());
	}
	
	@GetMapping("{id}")
	public Game getGame(@PathVariable("id") String id) throws GameException {
		return gameService.getGame(id);
	}
	
	@GetMapping
	public List<Game> listGames() throws GameException {
		return gameService.getGames();
	}

	@PatchMapping("{id}")
	public Game replayGame(@PathVariable("id") String id) throws GameException {
		Game game = gameService.getGame(id);
		runner.restart(game);
		return gameService.getGame(game.getId());
	}

}
