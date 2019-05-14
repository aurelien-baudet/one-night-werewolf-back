package fr.aba.werewolf.business.service.impl.video;

import static io.openvidu.java.client.OpenViduRole.PUBLISHER;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.video.SessionToken;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import io.openvidu.java.client.TokenOptions;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InMemoryOpenViduSessionManager implements OpenViduSessionManager {
	private final OpenVidu openVidu;
	private final Map<String, Session> sessionsByGameId;
	private final Map<String, Set<String>> tokensByGameId;
	
	@Autowired
	public InMemoryOpenViduSessionManager(OpenVidu openVidu) {
		this(openVidu, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
	}

	@Override
	public SessionToken add(Game game, String tokenData) throws OpenViduJavaClientException, OpenViduHttpException {
		Session session = getOrCreateSession(game);
		String token = generateToken(game, tokenData, session);
		addToken(game, token);
		return new SessionToken(token);
	}

	@Override
	public void remove(Game game, SessionToken token) {
		removeToken(game, token.getValue());
		if(isLastUserLeft(game)) {
			sessionsByGameId.remove(game.getId());
		}
	}

	private boolean isLastUserLeft(Game game) {
		Set<String> tokens = getTokens(game);
		return tokens==null || tokens.isEmpty();
	}
	
	private void addToken(Game game, String token) {
		Set<String> tokens = getTokens(game);
		if(tokens == null) {
			tokens = new HashSet<>();
			tokensByGameId.put(game.getId(), tokens);
		}
		tokens.add(token);
	}

	private Set<String> getTokens(Game game) {
		return tokensByGameId.get(game.getId());
	}
	
	private void removeToken(Game game, String token) {
		Set<String> tokens = getTokens(game);
		if(tokens == null) {
			// TODO: no tokens => exception ?
			return;
		}
		tokens.remove(token);
	}

	private Session getOrCreateSession(Game game) throws OpenViduJavaClientException, OpenViduHttpException {
		Session remoteSession = getRemoteSession(game);
		if(remoteSession == null) {
			SessionProperties sessionProps = new SessionProperties.Builder()
//					.customSessionId(game.getId())
					.build();
			remoteSession = openVidu.createSession(sessionProps);
		}
		sessionsByGameId.put(game.getId(), remoteSession);
		return remoteSession;
	}

	private Session getRemoteSession(Game game) throws OpenViduJavaClientException, OpenViduHttpException {
		openVidu.fetch();
		return openVidu.getActiveSessions().stream()
				.filter(s -> isSessionFor(s, game))
				.findAny()
				.orElse(null);
	}

	private boolean isSessionFor(Session session, Game game) {
		Session sessionForGame = sessionsByGameId.get(game.getId());
		if(sessionForGame == null) {
			return false;
		}
		return sessionForGame.getSessionId().equals(session.getSessionId());
	}
	
	private String generateToken(Game game, String tokenData, Session session) throws OpenViduJavaClientException, OpenViduHttpException {
		TokenOptions tokenOptions = new TokenOptions.Builder()
				.data(tokenData)
				.role(PUBLISHER)
				.build();
		return session.generateToken(tokenOptions);
	}

}
