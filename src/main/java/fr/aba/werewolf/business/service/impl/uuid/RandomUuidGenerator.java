package fr.aba.werewolf.business.service.impl.uuid;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomUuidGenerator implements UuidGenerator {
	private final String name;

	@Override
	public String generate() {
		return name + "-" + UUID.randomUUID().toString();
	}

}
