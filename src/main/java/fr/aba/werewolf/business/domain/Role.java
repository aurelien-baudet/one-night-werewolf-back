package fr.aba.werewolf.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="name")
public class Role {
	private String name;
	private String description;
	
	public Role(Role other) {
		super();
		name = other.getName();
		description = other.getDescription();
	}
	
	public boolean is(String roleName) {
		return isSame(roleName);
	}
	
	public boolean isSame(String roleName) {
		return name!=null && name.equals(roleName);
	}
	
	public boolean isSame(Role role) {
		return name!=null && name.equals(role.getName());
	}
}
