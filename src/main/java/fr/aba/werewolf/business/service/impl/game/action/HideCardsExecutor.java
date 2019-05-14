package fr.aba.werewolf.business.service.impl.game.action;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.action.HideCards;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HideCardsExecutor implements ActionExecutor {

	@Override
	public boolean supports(Action action) {
		return action instanceof HideCards;
	}

	@Override
	public Board execute(Board currentBoard, Player player, Role as, Action action) {
		HideCards hideCardsAction = (HideCards) action;
		log.info("hide cards: {}", hideCardsAction.getCardIds());
		Board boardToUpdate = new Board(currentBoard);
		PlayerBoard playerBoard = boardToUpdate.getBoardForPlayer(player);
		for(String cardId : hideCardsAction.getCardIds()) {
			playerBoard.getCardById(cardId).setVisible(false);
		}
		return boardToUpdate;
	}

}
