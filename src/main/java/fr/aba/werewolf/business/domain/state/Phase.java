package fr.aba.werewolf.business.domain.state;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat(shape=Shape.STRING)
public enum Phase {
	SUNSET,
	AWAKE,
	SLEEPING,
	SUNRISE,
	DISCUSS,
	VOTE;
}