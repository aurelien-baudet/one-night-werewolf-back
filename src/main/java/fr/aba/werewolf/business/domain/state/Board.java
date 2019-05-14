package fr.aba.werewolf.business.domain.state;

import static java.util.stream.Collectors.toMap;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Board {
	@Id
	private String id;
	private Duration remainingDiscussionDuration;
	private Duration remainingVoteDuration;
	private Map<String, PlayerBoard> boardsPerPlayerId;
	private Role currentRole;
	private boolean distributed;
	private boolean started;
	private boolean ended;
	private Phase phase;
	
	public Board(Duration remainingDiscussionDuration, Duration remainingVoteDuration, Map<String, PlayerBoard> boardsPerPlayerId) {
		this(null, remainingDiscussionDuration, remainingVoteDuration, boardsPerPlayerId, null, false, false, false, null);
	}

	public Board(Duration remainingDiscussionDuration, Duration remainingVoteDuration, boolean distributed, Map<String, PlayerBoard> boardsPerPlayerId) {
		this(null, remainingDiscussionDuration, remainingVoteDuration, boardsPerPlayerId, null, distributed, false, false, null);
	}

	public Board(Board other) {
		super();
		remainingDiscussionDuration = other.getRemainingDiscussionDuration();
		remainingVoteDuration = other.getRemainingVoteDuration();
		currentRole = other.getCurrentRole()==null ? null : new Role(other.getCurrentRole());
		boardsPerPlayerId = other.getBoardsPerPlayerId().entrySet().stream()
				.collect(toMap(Entry::getKey, e -> new PlayerBoard(e.getValue())));
		distributed = other.isDistributed();
		started = other.isStarted();
		ended = other.isEnded();
		phase = other.getPhase();
	}

	public PlayerBoard getBoardForPlayer(Player player) {
		return boardsPerPlayerId.get(player.getId());
	}

	public List<PlayerBoard> getAllPlayerBoards() {
		return new ArrayList<>(boardsPerPlayerId.values());
	}

}
