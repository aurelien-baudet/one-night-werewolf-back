package fr.aba.werewolf.business.service.impl.video;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.video.PlayerMetadata;
import fr.aba.werewolf.business.domain.video.SessionMetadata;
import fr.aba.werewolf.business.domain.video.SessionToken;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.VideoStreamListener;
import fr.aba.werewolf.business.service.VideoStreamService;
import fr.aba.werewolf.business.service.impl.game.ListenerManager;
import fr.aba.werewolf.business.service.impl.game.exception.TokenGenerationException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class InMemoryOpenViduVideoStreamService implements VideoStreamService, ListenerManager<VideoStreamListener> {
	private final OpenViduSessionManager sessionManager;
	private final ListenerManager<VideoStreamListener> delegate;
	private final List<SessionMetadata> sessionMetadatas;
	private final List<PlayerMetadata> metadatas;
	
	@Autowired
	public InMemoryOpenViduVideoStreamService(OpenViduSessionManager sessionManager, ListenerManager<VideoStreamListener> delegate) {
		this(sessionManager, delegate, new ArrayList<>(), new ArrayList<>());
	}

	@Override
	public SessionMetadata connect(Game game, String groupName) throws GameException {
		try {
			SessionToken token = sessionManager.add(game, groupName);
			SessionMetadata sessionMetadata = new SessionMetadata(groupName, token);
			sessionMetadatas.add(sessionMetadata);
			return sessionMetadata;
		} catch (OpenViduHttpException | OpenViduJavaClientException e) {
			throw new TokenGenerationException(game, e);
		}
	}

	@Override
	public PlayerMetadata registerPlayer(Game game, String groupName, Player player) throws GameException {
		log.debug("registering player id={} in group={}", player.getId(), groupName);
		SessionToken token = getToken(groupName);
		removeOldMetadata(player, groupName);
		PlayerMetadata metadata = new PlayerMetadata(player, groupName, token);
		metadatas.add(metadata);
		notifyListeners(l -> l.streamsUpdated(game, getMetadataForPlayers(game)));
		return metadata;
	}

	@Override
	public Set<PlayerMetadata> getMetadataForPlayers(Game game) {
		Set<PlayerMetadata> metas = game.getPlayers().stream()
				.flatMap(this::filterMatchingMetadataForPlayer)
				.collect(toSet());
		return metas;
	}

	@Override
	public void disconnect(Game game, String groupName) throws GameException  {
		// FIXME: clean memory once a player quit the game. For the moment every time the page is changed or reloaded, the player is disconnected...
//		List<PlayerMetadata> toBeRemoved = new ArrayList<>();
//		for(PlayerMetadata meta : new CopyOnWriteArrayList<>(metadatas)) {
//			if(meta.getGroupName().equals(groupName)) {
//				toBeRemoved.add(meta);
//				sessionManager.remove(game, meta.getToken());
//			}
//		}
//		metadatas.removeAll(toBeRemoved);
		notifyListeners(l -> l.streamsUpdated(game, getMetadataForPlayers(game)));
	}

	@Override
	public void register(VideoStreamListener listener) {
		delegate.register(listener);
	}

	@Override
	public void unregister(VideoStreamListener listener) {
		delegate.unregister(listener);
	}

	@Override
	public void notifyListeners(Consumer<VideoStreamListener> consumer) {
		delegate.notifyListeners(consumer);
	}

	private boolean isMetadataForPlayer(Player p, PlayerMetadata m) {
		return m.getPlayer().isSame(p);
	}
	
	private SessionToken getToken(String group) {
		return sessionMetadatas.stream()
				.filter(s -> s.getGroupName().equals(group))
				.map(SessionMetadata::getToken)
				.findAny()
				.orElse(null);	// TODO: throw ?
	}

	private Stream<PlayerMetadata> filterMatchingMetadataForPlayer(Player p) {
		return metadatas.stream().filter(m -> isMetadataForPlayer(p, m));
	}
	
	private void removeOldMetadata(Player player, String groupName) {
		metadatas.removeIf(m -> m.getGroupName().equals(groupName) && m.getPlayer().isSame(player));
	}
}
