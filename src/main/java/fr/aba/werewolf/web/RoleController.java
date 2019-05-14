package fr.aba.werewolf.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.RoleService;

@RestController
public class RoleController {
	@Autowired
	RoleService roleService;
	@Autowired
	GameService gameService;
	
	@GetMapping("roles")
	public List<Role> getAvailableRoles() {
		return roleService.getAvailableRoles();
	}
	
	@GetMapping("games/{gameId}/roles")
	public List<Role> getPlayingRoles(@PathVariable("gameId") String gameId) throws GameException {
		return roleService.getOrderedPlayingRoles(gameService.getGame(gameId));
	}
}
