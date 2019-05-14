package fr.aba.werewolf.business.service.impl.shuffle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CollectionsShuffler<T> implements Shuffler<T> {

	@Override
	public List<T> shuffle(List<T> list) {
		ArrayList<T> shuffled = new ArrayList<>(list);
		Collections.shuffle(shuffled);
		return shuffled;
	}

}
