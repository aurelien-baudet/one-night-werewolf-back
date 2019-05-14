package fr.aba.werewolf.business.domain.state;

import fr.aba.werewolf.business.domain.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class InFrontOfPlayer implements Position {
	private String playerId;
	
	public InFrontOfPlayer(Player player) {
		super();
		this.playerId = player.getId();
	}

	public InFrontOfPlayer(InFrontOfPlayer other) {
		super();
		this.playerId = other.getPlayerId();
	}

	@Override
	public String toString() {
		return "in front of player "+playerId;
	}
	
	public boolean isInFrontOf(Player player) {
		return player != null && player.isSame(playerId);
	}
	
	public boolean isInFrontOf(String playerId) {
		return this.playerId != null && this.playerId.equals(playerId);
	}
}
