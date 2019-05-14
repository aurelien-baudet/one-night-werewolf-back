package fr.aba.werewolf.business.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Document
@ToString()
@EqualsAndHashCode(of="id")
@AllArgsConstructor
@NoArgsConstructor
public class Player {
	@Id
	private String id;
	private String name;
	
	public Player(String name) {
		this(null, name);
	}
	
	public boolean isSame(Player player) {
		return id != null && id.equals(player.getId());
	}

	public boolean isSame(String playerId) {
		return id != null && id.equals(playerId);
	}
}
