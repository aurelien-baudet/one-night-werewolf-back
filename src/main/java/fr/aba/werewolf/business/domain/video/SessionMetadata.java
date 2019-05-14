package fr.aba.werewolf.business.domain.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionMetadata {
	private String groupName;
	private SessionToken token;
}
