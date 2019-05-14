package fr.aba.werewolf.testutils.assertions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParentAssertion<P> {
	private final P parent;
	
	public P and() {
		return parent;
	}
}
