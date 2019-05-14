package fr.aba.werewolf.business.service.impl.game;

import java.util.function.Consumer;

public interface ListenerManager<L> {
	void register(L listener);
	void unregister(L listener);
	void notifyListeners(Consumer<L> consumer);
}
