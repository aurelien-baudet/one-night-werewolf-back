package fr.aba.werewolf.business.domain.state;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InTheMiddle implements Position {
	private int place;

	public InTheMiddle(InTheMiddle other) {
		super();
		this.place = other.getPlace();
	}

	@Override
	public String toString() {
		return "in the middle (place " + place + ")";
	}
}
