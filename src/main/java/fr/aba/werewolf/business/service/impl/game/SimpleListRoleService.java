package fr.aba.werewolf.business.service.impl.game;

import static java.util.stream.Collectors.toList;

import java.util.List;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.service.RoleService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleListRoleService implements RoleService {
	private final List<Role> availableRoles;
	private final List<Role> playingRoles;

	@Override
	public List<Role> getAvailableRoles() {
		return availableRoles;
	}

	@Override
	public List<Role> getOrderedPlayingRoles(Game game) {
		return playingRoles.stream()
			.filter(role -> game.getSelectedRoles().contains(role))
			.collect(toList());
	}

	@Override
	public Role getNextRole(Game game, Board board) {
		List<Role> roles = getOrderedPlayingRoles(game);
		Role currentRole = board.getCurrentRole();
		if(currentRole == null) {
			return roles.get(0);
		}
		int idx = roles.indexOf(currentRole);
		if(idx == -1) {
			throw new IllegalStateException("The current role is not present in playing roles");
		}
		if(idx+1 > roles.size()-1) {
			return null;
		}
		return roles.get(idx + 1);
	}

}
