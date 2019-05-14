package fr.aba.werewolf.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Vote {
	private String voterId;
	private String againstId;
	
	public Vote(Vote other) {
		super();
		voterId = other.getVoterId();
		againstId = other.getAgainstId();
	}
	
	
	public boolean isVotedBy(Player player) {
		return player != null && player.isSame(voterId);
	}

	public boolean isVotedBy(String playerId) {
		return voterId != null && voterId.equals(playerId);
	}
	
	public boolean isVoteAgainst(Player player) {
		return player != null && player.isSame(againstId);
	}

	public boolean isVoteAgainst(String playerId) {
		return againstId != null && againstId.equals(playerId);
	}
}
