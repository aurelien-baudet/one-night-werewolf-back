package fr.aba.werewolf.business.domain.action;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;

@Data
@JsonTypeName("SwitchCards")
public class SwitchCards implements Action {
	private final String card1Id;
	private final String card2Id;
}
