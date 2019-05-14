package fr.aba.werewolf.business.domain.action;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(onConstructor_= {@JsonCreator})
@JsonTypeName("ViewCards")
public class ViewCards implements Action {
	private final List<String> cardIds;
	
	public ViewCards(String... cardIds) {
		this(Arrays.asList(cardIds));
	}
}
