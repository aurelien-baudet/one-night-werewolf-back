package fr.aba.werewolf.testutils.assertions;

import java.util.HashMap;
import java.util.Map;

import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.InFrontOfPlayer;
import fr.aba.werewolf.business.domain.state.InTheMiddle;
import fr.aba.werewolf.business.domain.state.PlayerBoard;

public class DebugUtils {
	
	public static String displayBoard(Board board) {
		String boardAsText = "";
		for(PlayerBoard playerBoard : board.getAllPlayerBoards()) {
			boardAsText += displayBoard(playerBoard) + "\n\n---------------------------------\n";
		}
		return boardAsText;
	}
	
	public static String displayBoard(PlayerBoard board) {
		String boardAsText = "[board for player "+board.getPlayerId()+"]\n";
		for(String line : inlineCards(board).values()) {
			boardAsText += line + "\n";
		}
		return boardAsText;
	}

	private static Map<Integer, String> inlineCards(PlayerBoard board) {
		Map<Integer, String> lines = new HashMap<>();
		for(Card card : board.getCards()) {
			String[] cardAsTextLines = displayCard(card).split("\n");
			for(int i=0 ; i<cardAsTextLines.length ; i++) {
				String line = lines.get(i);
				String newLineContent = (line==null ? "" : line+"   ") + cardAsTextLines[i];
				lines.put(i, newLineContent);
			}
		}
		return lines;
	}
	
	public static String displayCard(Card card) {
		String cardId = card.getId();
		boolean visible = card.isVisible();
		Role role = card.getRole();
		String cardAsText = "┌────────┐\n"
							+ line(cardId)
							+ line("")
							+ line(visible ? "visible" : "hidden")
							+ line("")
							+ line(truncate(role.getName()))
							+ "└────────┘\n";
		if(card.getPosition() instanceof InFrontOfPlayer) {
			String playerId = ((InFrontOfPlayer) card.getPosition()).getPlayerId();
			cardAsText += "☺ "+pad(playerId, 8) + "\n";
		} else if(card.getPosition() instanceof InTheMiddle) {
			int place = ((InTheMiddle) card.getPosition()).getPlace();
			cardAsText += "[" + pad(""+place, 8) + "]\n";
		}
		return cardAsText;
	}
	
	private static String line(String textToDecorate) {
		return "│" + pad(textToDecorate, 8)+ "│\n";
	}

	private static String pad(String textToDecorate, int size) {
		String paddedStr = textToDecorate;
		for(int i=textToDecorate.length() ; i<size ; i++) {
			paddedStr += " ";
		}
		return paddedStr;
	}
	
	private static String truncate(String text) {
		return text.length() < 8 ? text : text.substring(0, 8);
	}
}
