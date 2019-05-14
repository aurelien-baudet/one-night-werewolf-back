package fr.aba.werewolf.business.service.impl.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InMemoryListenerManager<L> implements ListenerManager<L> {
	private final List<L> listeners;
	
	public InMemoryListenerManager() {
		this(new ArrayList<>());
	}
	
	@Override
	public void register(L listener) {
		listeners.add(listener);
	}

	@Override
	public void unregister(L listener) {
		listeners.remove(listener);
	}

	@Override
	public void notifyListeners(Consumer<L> consumer) {
		for(L listener : listeners) {
			consumer.accept(listener);
		}
	}

}
