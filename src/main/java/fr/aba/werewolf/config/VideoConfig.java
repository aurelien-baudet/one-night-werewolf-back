package fr.aba.werewolf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.aba.werewolf.business.service.impl.video.config.OpenViduServerProperties;
import io.openvidu.java.client.OpenVidu;

@Configuration
public class VideoConfig {
	@Bean
	public OpenVidu openVidu(OpenViduServerProperties props) {
		return new OpenVidu(props.getServerUrl().toExternalForm(), props.getSecret());
	}
}
