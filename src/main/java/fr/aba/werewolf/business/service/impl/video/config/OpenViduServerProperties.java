package fr.aba.werewolf.business.service.impl.video.config;

import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("openvidu")
public class OpenViduServerProperties {
	private URL serverUrl;
	private String secret;
}
