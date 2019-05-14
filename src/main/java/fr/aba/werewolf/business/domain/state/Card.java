package fr.aba.werewolf.business.domain.state;

import fr.aba.werewolf.business.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Card {
	private String id;
	private Role role;
	private boolean visible;
	private Position position;
	
	public Card(String id, Role role) {
		this(id, role, false, null);
	}
	
	public Card(Card other) {
		super();
		id = other.getId();
		role = other.getRole();
		position = Position.of(other.getPosition());
		visible = other.isVisible();
	}
}
