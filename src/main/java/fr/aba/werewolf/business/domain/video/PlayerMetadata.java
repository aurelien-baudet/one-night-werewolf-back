package fr.aba.werewolf.business.domain.video;

import fr.aba.werewolf.business.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of= {"player", "groupName"})
@ToString()
public class PlayerMetadata {
	private Player player;
	private String groupName;
	private SessionToken token;
}
