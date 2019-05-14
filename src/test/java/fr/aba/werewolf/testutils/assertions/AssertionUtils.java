package fr.aba.werewolf.testutils.assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.testutils.assertions.error.SeveralAssertionError;
import junit.framework.AssertionFailedError;

public class AssertionUtils {
	public static void checkForEachPlayer(Board board, Consumer<PlayerBoard> checkFunc) {
		checkForEachPlayer(board.getAllPlayerBoards(), checkFunc);
	}
	
	public static void checkForEachPlayer(List<PlayerBoard> boards, Consumer<PlayerBoard> checkFunc) {
		// check on each player board that the card is at the right place
		List<AssertionFailedError> failures = new ArrayList<>();
		for(PlayerBoard playerBoard : boards) {
			try {
				checkFunc.accept(playerBoard);
			} catch(AssertionFailedError e) {
				failures.add(e);
			}
		}
		if(failures.isEmpty()) {
			return;
		}
		if(failures.size() == 1) {
			throw failures.get(0);
		}
		throw new SeveralAssertionError(failures);
	}
}
