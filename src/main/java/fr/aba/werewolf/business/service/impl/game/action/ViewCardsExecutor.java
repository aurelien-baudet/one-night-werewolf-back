package fr.aba.werewolf.business.service.impl.game.action;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.action.ViewCards;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ViewCardsExecutor implements ActionExecutor {

	@Override
	public boolean supports(Action action) {
		return action instanceof ViewCards;
	}

	@Override
	public Board execute(Board currentBoard, Player player, Role as, Action action) {
		ViewCards viewCardsAction = (ViewCards) action;
		log.info("view cards: {}", viewCardsAction.getCardIds());
		Board boardToUpdate = new Board(currentBoard);
		PlayerBoard playerBoard = boardToUpdate.getBoardForPlayer(player);
		for(String cardId : viewCardsAction.getCardIds()) {
			playerBoard.getCardById(cardId).setVisible(true);
		}
		return boardToUpdate;
	}

}
