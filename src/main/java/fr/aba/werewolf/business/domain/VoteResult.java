package fr.aba.werewolf.business.domain;

import java.util.Set;

import lombok.Data;

@Data
public class VoteResult {
	private final Set<Vote> votes;
	private final Set<Player> deads;
	private final Set<Player> winners;
	private final Set<Player> loosers;
}
