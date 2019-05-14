package fr.aba.werewolf.business.domain.action;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME, property="@type")
@JsonSubTypes({
	@Type(ViewCards.class),
	@Type(HideCards.class),
	@Type(SwitchCards.class)
})
public interface Action {

}
