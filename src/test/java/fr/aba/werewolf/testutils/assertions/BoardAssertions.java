package fr.aba.werewolf.testutils.assertions;

import fr.aba.werewolf.business.domain.state.Board;

public class BoardAssertions {
	public static BoardAssertion assertThat(Board board) {
		return new BoardAssertion(board);
	}
}
