package fr.aba.werewolf.business.service.impl.shuffle;

import java.util.List;

public interface Shuffler<T> {
	List<T> shuffle(List<T> list);
}
