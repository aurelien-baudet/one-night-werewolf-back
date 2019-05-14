package fr.aba.werewolf.business.service.impl.game.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="werewolf.timing")
public class TimingProperties {
	private Duration defaultPauseDuration;
	private Duration defaultDiscussionDuration;
	private Duration closeEveryoneEyesDuration;
	/**
	 * Duration of every wake up in milliseconds (without pause, pause is chosen by players for every game)
	 */
	private Duration wakeUpDuration;
	private Duration closeEyesDuration;
	private Duration wakeUpEveryoneDuration;
	private Duration voteDuration;
	private Duration timerUpdateInterval;
}