package fr.aba.werewolf.business.domain.state;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardsSwitched {
	private String card1Id;
	private String card2Id;
	
	public CardsSwitched(CardsSwitched other) {
		super();
		card1Id = other.getCard1Id();
		card2Id = other.getCard2Id();
	}
}
