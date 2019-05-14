package fr.aba.werewolf.business.service;

import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.video.PlayerMetadata;
import fr.aba.werewolf.business.domain.video.SessionMetadata;

public interface VideoStreamService {
	SessionMetadata connect(Game game, String groupName) throws GameException;
	PlayerMetadata registerPlayer(Game game, String groupName, Player player) throws GameException;
	Set<PlayerMetadata> getMetadataForPlayers(Game game) throws GameException;
	void disconnect(Game game, String groupName) throws GameException;
}
