package fr.aba.werewolf.business.service.impl.game.action;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.action.SwitchCards;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.CardsSwitched;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.business.domain.state.Position;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SwitchCardsExecutor implements ActionExecutor {

	@Override
	public boolean supports(Action action) {
		return action instanceof SwitchCards;
	}

	@Override
	public Board execute(Board currentBoard, Player switcher, Role as, Action action) {
		SwitchCards switchCardsAction = (SwitchCards) action;
		log.info("switch cards: {} <-> {}", switchCardsAction.getCard1Id(), switchCardsAction.getCard2Id());
		Board boardToUpdate = new Board(currentBoard);
		// Exchange cards for all players
		String card1 = switchCardsAction.getCard1Id();
		String card2 = switchCardsAction.getCard2Id();
		for(PlayerBoard board : boardToUpdate.getAllPlayerBoards()) {
			switchCards(board, card1, card2);
		}
		// But only the one who exchanged cards can view it in the game
		PlayerBoard switcherBoard = boardToUpdate.getBoardForPlayer(switcher);
		setCardMovement(switcherBoard, card1, card2);
		return boardToUpdate;
	}

	
	private void switchCards(PlayerBoard board, String card1, String card2) {
		Position card1Pos = board.getCardById(card1).getPosition();
		Position card2Pos = board.getCardById(card2).getPosition();
		board.getCardById(card1).setPosition(card2Pos);
		board.getCardById(card2).setPosition(card1Pos);
	}
	
	private void setCardMovement(PlayerBoard board, String card1, String card2) {
		board.setMovement(new CardsSwitched(card1, card2));
	}

}
