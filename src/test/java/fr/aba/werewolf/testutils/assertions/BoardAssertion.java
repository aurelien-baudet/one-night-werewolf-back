package fr.aba.werewolf.testutils.assertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoardAssertion {
	private final Board board;
	
	public BoardAssertion currentRole(Role expectedRole) {
		if(expectedRole == null) {
			assertNull("there shouldn't have a current role on the board as the game is not started", board.getCurrentRole());
		} else {
			assertNotNull("there should have a current role on the board as the game is started", board.getCurrentRole());
			assertEquals("the current role on the board should be "+expectedRole.getName(), expectedRole.getName(), board.getCurrentRole().getName());
		}
		return this;
	}
	
	public PlayerAssertion player(Player player) {
		return new PlayerAssertion(this, board, player);
	}
	
	public CardsInTheMiddleAssertion middle() {
		return new CardsInTheMiddleAssertion(this, board);
	}

	public AllCardsAssertion<BoardAssertion> cards() {
		return new AllCardsAssertion<>(this, board);
	}
}
