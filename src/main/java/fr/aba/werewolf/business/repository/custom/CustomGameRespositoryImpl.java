package fr.aba.werewolf.business.repository.custom;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.repository.GameRepository;
import fr.aba.werewolf.business.service.impl.game.config.RulesProperties;

@Component("gameRepositoryImpl")
public class CustomGameRespositoryImpl implements CustomGameRepository {
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private RulesProperties rulesConfig;
	
	@Override
	public List<Game> findAllJoinable(Sort sort) {
		return gameRepository.findAll(sort)
				.stream()
				.filter(this::joinable)
				.collect(toList());
	}

	private boolean joinable(Game game) {
		// not enough players yet
		if(game.getPlayers().size() < game.getSelectedRoles().size() - rulesConfig.getCardsInTheMiddle()) {
			return true;
		}
		return false;
	}
}
