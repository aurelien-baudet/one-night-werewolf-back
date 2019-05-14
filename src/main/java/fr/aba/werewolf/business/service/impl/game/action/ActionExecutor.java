package fr.aba.werewolf.business.service.impl.game.action;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.state.Board;

public interface ActionExecutor {
	boolean supports(Action action);
	Board execute(Board currentBoard, Player player, Role as, Action action);
}
