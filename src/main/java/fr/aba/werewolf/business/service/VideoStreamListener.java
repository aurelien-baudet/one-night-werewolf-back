package fr.aba.werewolf.business.service;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.video.PlayerMetadata;

public interface VideoStreamListener {
	void streamsUpdated(Game game, Set<PlayerMetadata> meta);
}
