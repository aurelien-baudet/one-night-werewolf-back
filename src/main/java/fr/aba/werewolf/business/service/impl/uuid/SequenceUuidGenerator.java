package fr.aba.werewolf.business.service.impl.uuid;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequenceUuidGenerator implements UuidGenerator {
	private int current = 0;

	@Override
	public String generate() {
		current++;
		return ""+current;
	}

}
