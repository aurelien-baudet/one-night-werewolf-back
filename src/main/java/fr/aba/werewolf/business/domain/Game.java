package fr.aba.werewolf.business.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Game {
	@Id
	private String id;
	private List<Player> players;
	private List<Role> selectedRoles;
	private GameOptions gameOptions;
	@CreatedDate
	private LocalDateTime createdDate;

	public Game(List<Player> players, List<Role> selectedRoles, GameOptions gameOptions) {
		this(null, players, selectedRoles, gameOptions, null);
	}
}
