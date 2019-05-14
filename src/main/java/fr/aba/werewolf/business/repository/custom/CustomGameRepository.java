package fr.aba.werewolf.business.repository.custom;

import java.util.List;

import org.springframework.data.domain.Sort;

import fr.aba.werewolf.business.domain.Game;

public interface CustomGameRepository {
	List<Game> findAllJoinable(Sort sort);
}
