package fr.aba.werewolf.business.service;

import java.util.List;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;

public interface RoleService {
	List<Role> getAvailableRoles();
	List<Role> getOrderedPlayingRoles(Game game);
	Role getNextRole(Game game, Board board);
}
