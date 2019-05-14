package fr.aba.werewolf.business.service.impl.game.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="werewolf.rules")
public class RulesProperties {
	private int minPlayers;
	private int maxPlayers;
	private int cardsInTheMiddle;		
}