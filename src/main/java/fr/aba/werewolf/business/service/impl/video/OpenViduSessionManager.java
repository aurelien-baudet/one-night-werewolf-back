package fr.aba.werewolf.business.service.impl.video;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.video.SessionToken;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;

public interface OpenViduSessionManager {
	SessionToken add(Game game, String tokenData) throws OpenViduJavaClientException, OpenViduHttpException;
	void remove(Game game, SessionToken token);
}
