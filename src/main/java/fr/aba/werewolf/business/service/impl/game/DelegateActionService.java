package fr.aba.werewolf.business.service.impl.game;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.Action;
import fr.aba.werewolf.business.domain.action.ViewCards;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.service.ActionService;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.impl.game.action.ActionExecutor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DelegateActionService implements ActionService {
	private final List<ActionExecutor> delegates;
	private final BoardService boardService;
	
	@Override
	public Board play(Game game, Player player, Role as, Action action) throws GameException {
		for(ActionExecutor delegate : delegates) {
			if(delegate.supports(action)) {
				Board newBoard = delegate.execute(boardService.getCurrentBoard(game), player, as, action);
				boardService.updateBoard(game, newBoard);
				return newBoard;
			}
		}
		return boardService.getCurrentBoard(game);
	}

	@Override
	public Board viewOwnCard(Game game, Player player) throws GameException {
		Card card = boardService.getCurrentBoard(game).getBoardForPlayer(player).getCurrentCard();
		return play(game, player, null, new ViewCards(card.getId()));
	}

}
