package fr.aba.werewolf.business.service;

import java.util.List;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;

public interface PlayersListener {
	void playersUpdated(Game game, List<Player> players);
}
