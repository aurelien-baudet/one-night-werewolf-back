package fr.aba.werewolf.business.service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.state.Board;

public interface ActionService {
	Board play(Game game, Player player, Role as, Action action) throws GameException;
	Board viewOwnCard(Game game, Player player) throws GameException;
}
