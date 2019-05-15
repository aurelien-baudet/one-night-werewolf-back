package fr.aba.werewolf.business.domain;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameOptions {
	private Duration pauseDuration;
	private Duration discussionDuration;
	private boolean guidedMode;
	private String backgroundMusic;
	private float backgroundMusicVolume;
}
