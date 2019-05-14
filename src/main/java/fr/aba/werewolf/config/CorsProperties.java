package fr.aba.werewolf.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="cors")
public class CorsProperties {
	private String path;
	private List<String> allowedOrigins;
	private List<String> allowedMethods;
}
