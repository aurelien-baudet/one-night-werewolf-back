package fr.aba.werewolf.business.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Document
@NoArgsConstructor
public class SavedVote {
	@Id
	private String id;
	private String gameId;
	private Vote vote;
	
	public SavedVote(String gameId, Vote vote) {
		this(null, gameId, vote);
	}
	
}
