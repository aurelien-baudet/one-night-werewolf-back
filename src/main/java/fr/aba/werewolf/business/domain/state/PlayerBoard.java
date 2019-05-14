package fr.aba.werewolf.business.domain.state;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBoard {
	private String playerId;
	private Role originalRole;
	private List<Card> cards;
	private List<CardsSwitched> movements;
	
	public PlayerBoard(Player player, Role originalRole, List<Card> cards) {
		this(player.getId(), new Role(originalRole), cards
				.stream()
				.map(Card::new).collect(toList()), new ArrayList<>());
	}

	public PlayerBoard(PlayerBoard other) {
		super();
		playerId = other.getPlayerId();
		originalRole = other.getOriginalRole();
		cards = other.getCards()
				.stream()
				.map(Card::new)
				.collect(toList());
		movements = other.getMovements()
				.stream()
				.map(CardsSwitched::new)
				.collect(toList());
	}
	
	public Card getCurrentCard() {
		return getCardForPlayer(playerId);
	}
	
	public List<Card> getCardsInTheMiddle() {
		return cards
				.stream()
				.filter(card -> card.getPosition() instanceof InTheMiddle)
				.collect(toList());
	}
	
	public Card getCardForPlayer(Player player) {
		return getCardForPlayer(player.getId());
	}
	
	public Card getCardForPlayer(String playerId) {
		return cards
				.stream()
				.filter(card -> card.getPosition() instanceof InFrontOfPlayer)
				.filter(card -> ((InFrontOfPlayer) card.getPosition()).getPlayerId().equals(playerId))
				.findFirst()
				.orElse(null);
	}
	
	public Card getCardById(String id) {
		for(Card card : cards) {
			if(id.equals(card.getId())) {
				return card;
			}
		}
		return null;
	}

	public void setMovement(CardsSwitched movement) {
		setMovements(new ArrayList<>(asList(movement)));
	}
}
