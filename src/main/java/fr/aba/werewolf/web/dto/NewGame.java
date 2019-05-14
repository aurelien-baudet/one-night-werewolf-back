package fr.aba.werewolf.web.dto;

import java.util.List;

import fr.aba.werewolf.business.domain.GameOptions;
import fr.aba.werewolf.business.domain.Role;
import lombok.Data;

@Data
public class NewGame {
	private List<Role> selectedRoles;
	private GameOptions options;
}
