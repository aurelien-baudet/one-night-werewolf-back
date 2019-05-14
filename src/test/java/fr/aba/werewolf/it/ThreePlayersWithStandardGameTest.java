package fr.aba.werewolf.it;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.GameOptions;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.action.SwitchCards;
import fr.aba.werewolf.business.domain.action.ViewCards;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.service.ActionService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.GameLogicService;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.RoleService;
import fr.aba.werewolf.business.service.VoteService;
import fr.aba.werewolf.business.service.impl.shuffle.Shuffler;
import fr.aba.werewolf.business.service.impl.uuid.UuidGenerator;
import fr.aba.werewolf.testutils.assertions.BoardAssertions;
import fr.aba.werewolf.testutils.assertions.DebugUtils;
import fr.aba.werewolf.testutils.assertions.error.PlayerBoardAssertionError;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("tests")
public class ThreePlayersWithStandardGameTest {
	@Autowired
	GameService gameService;
	@Autowired
	GameLogicService gameLogicService;
	@Autowired
	ActionService actionService;
	@Autowired
	RoleService roleService;
	@Autowired
	VoteService voteService;
	@MockBean(name="cardUuidGenerator")
	UuidGenerator cardUuidGenerator;
	@MockBean
	Shuffler<Card> shuffler;

	private Game game;
	private Player player1;
	private Player player2;
	private Player player3;
	private Role werewolf1 = new Role("werewolf", "wolf 1");
	private Role werewolf2 = new Role("werewolf", "wolf 2");
	private Role seer = new Role("seer", "");
	private Role robber = new Role("robber", "");
	private Role troublemaker = new Role("troublemaker", "");
	private Role villager = new Role("villager", "");
	

	@Before
	public void prepareGame() throws GameException {
		// mock
		when(cardUuidGenerator.generate()).thenReturn(
				"card-1", 
				"card-2", 
				"card-3", 
				"card-4", 
				"card-5", 
				"card-6");
		when(shuffler.shuffle(anyList())).thenReturn(asList(
				new Card("card-3", seer), 			// player1
				new Card("card-5", troublemaker), 	// player2
				new Card("card-1", werewolf1), 		// player3
				new Card("card-4", robber), 		// middle (place 1)
				new Card("card-6", villager), 		// middle (place 2)
				new Card("card-2", werewolf2) 		// middle (place 3)
				));
		// prepare the game
		List<Role> selectedRoles = asList(
				werewolf1,
				werewolf2,
				seer,
				robber,
				troublemaker,
				villager);
		game = gameService.newGame(selectedRoles, new GameOptions(ofSeconds(5), ofSeconds(3), false));
		player1 = new Player("1", "Guigui");
		player2 = new Player("2", "Yo");
		player3 = new Player("3", "Cécé");
		gameService.addPlayer(game, player1);
		gameService.addPlayer(game, player2);
		gameService.addPlayer(game, player3);
	}
	
	@Test
	public void play() throws GameException {
		try {
			distributeCards();
			/**
			 * each player looks his card
			 */
			player1LooksHisCard();
			player2LooksHisCard();
			player3LooksHisCard();
			/**
			 * Everyone, close your eyes
			 */
			closeAllEyes();
			/**
			 * "Werewolves, wake up and look for other werewolves"
			 * 
			 * werewolf is alone so he can see a card in the middle
			 */
			werewolvesWakeUp();
			werewolfAloneLooksCardInTheMiddle("card-4");
			/**
			 * "Werewolves, close your eyes. Seer, wake up.
			 * You may look at another player’s card or two
			 * of the center cards"
			 * 
			 * seer looks two of the center cards
			 */
			werewolvesCloseYourEyes();
			seerWakeUp();
			seerLooksTwoCardsInTheMiddle("card-6", "card-2");
			/**
			 * "Seer, close your eyes. Robber, wake up.
			 * You may exchange your card with another
			 * player’s card, and then view your new card."
			 */
			seerCloseYourEyes();
			robberWakeUp();
			// robber doesn't play
			// robberExchangeCardWith(player)
			/**
			 * "Robber, close your eyes. Troublemaker,
			 * wake up. You may exchange cards between
			 * two other players"
			 * 
			 * troublemaker (player2) exchanges cards between player1 and player3
			 */
			robberCloseYourEyes();
			troublemakerWakeUp();
			troublemakerExchangesTwoCards("card-3", "card-1");
			/**
			 * "Troublemaker, close your eyes"
			 */
			troublemakerCloseYourEyes();
			/**
			 * "Everyone, Wake up!"
			 */
			everyOneWakeUp();
			/**
			 * Discuss about who are the wolves...
			 * wait for x minutes maximum
			 */
			discuss();
			/**
			 * vote
			 */
			vote();
		} catch(PlayerBoardAssertionError e) {
			log.error(e.getMessage());
			log.error("current board:\n{}", DebugUtils.displayBoard(e.getPlayerBoard()));
			throw e;
		}
	}

	private Board distributeCards() throws GameException {
		Board initialBoard = gameLogicService.distribute(game);
		BoardAssertions.assertThat(initialBoard)
			.currentRole(null)
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
		return initialBoard;
	}

	private void player1LooksHisCard() throws GameException {
		Board board = actionService.viewOwnCard(game, player1);
		BoardAssertions.assertThat(board)
			.currentRole(null)
			.player(player1)
				.hasCard("card-3")
				.sees("card-3")
				.doesNotSee(allExcept("card-3"))
				.and()
			.player(player2)
				.hasCard("card-5")
				.cards()
					.hidden()
					.and()
				.and()
			.player(player3)
				.hasCard("card-1")
				.cards()
					.hidden()
					.and()
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void player2LooksHisCard() throws GameException {
		Board board = actionService.viewOwnCard(game, player2);
		BoardAssertions.assertThat(board)
			.currentRole(null)
			.player(player1)
				.hasCard("card-3")
				.sees("card-3")
				.doesNotSee(allExcept("card-3"))
				.and()
			.player(player2)
				.hasCard("card-5")
				.sees("card-5")
				.doesNotSee(allExcept("card-5"))
				.and()
			.player(player3)
				.hasCard("card-1")
				.cards()
					.hidden()
					.and()
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void player3LooksHisCard() throws GameException {
		Board board = actionService.viewOwnCard(game, player3);
		BoardAssertions.assertThat(board)
			.currentRole(null)
			.player(player1)
				.hasCard("card-3")
				.sees("card-3")
				.doesNotSee(allExcept("card-3"))
				.and()
			.player(player2)
				.hasCard("card-5")
				.sees("card-5")
				.doesNotSee(allExcept("card-5"))
				.and()
			.player(player3)
				.hasCard("card-1")
				.sees("card-1")
				.doesNotSee(allExcept("card-1"))
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void closeAllEyes() throws GameException {
		Board board = gameLogicService.closeEyes(game);
		BoardAssertions.assertThat(board)
			.currentRole(null)
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}
	
	private void werewolvesWakeUp() throws GameException {
		BoardAssertions.assertThat(gameLogicService.wakeUp(game))
			.currentRole(new Role("werewolf", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private Board werewolfAloneLooksCardInTheMiddle(String cardId) throws GameException {
		Board turn1Board = actionService.play(game, player3, werewolf1, new ViewCards(cardId));
		// no card moved & player3 sees first card in the middle
		BoardAssertions.assertThat(turn1Board)
			.currentRole(new Role("werewolf", ""))
			.player(player1)
				.hasCard("card-3")
				.cards()
					.hidden()
					.and()
				.and()
			.player(player2)
				.hasCard("card-5")
				.cards()
					.hidden()
					.and()
				.and()
			.player(player3)
				.hasCard("card-1")
				.sees(cardId)
				.doesNotSee(allExcept(cardId))
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
		return turn1Board;
	}

	private void werewolvesCloseYourEyes() throws GameException {
		BoardAssertions.assertThat(gameLogicService.closeEyes(game))
			.currentRole(new Role("werewolf", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	
	private void seerWakeUp() throws GameException {
		BoardAssertions.assertThat(gameLogicService.wakeUp(game))
			.currentRole(new Role("seer", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void seerLooksTwoCardsInTheMiddle(String card1, String card2) throws GameException {
		Board turn2Board = actionService.play(game, player1, seer, new ViewCards(card1, card2));
		// no card moved & player3 sees first card in the middle
		BoardAssertions.assertThat(turn2Board)
			.currentRole(new Role("seer", ""))
			.player(player1)
				.hasCard("card-3")
				.sees(card1, card2)
				.doesNotSee(allExcept(card1, card2))
				.and()
			.player(player2)
				.hasCard("card-5")
				.cards()
					.hidden()
					.and()
				.and()
			.player(player3)
				.hasCard("card-1")
				.cards()
					.hidden()
					.and()
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void seerCloseYourEyes() throws GameException {
		BoardAssertions.assertThat(gameLogicService.closeEyes(game))
			.currentRole(new Role("seer", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void robberWakeUp() throws GameException {
		BoardAssertions.assertThat(gameLogicService.wakeUp(game))
			.currentRole(new Role("robber", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void robberCloseYourEyes() throws GameException {
		BoardAssertions.assertThat(gameLogicService.closeEyes(game))
			.currentRole(new Role("robber", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}
	
	private void troublemakerWakeUp() throws GameException {
		BoardAssertions.assertThat(gameLogicService.wakeUp(game))
			.currentRole(new Role("troublemaker", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-3")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-1")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}


	private void troublemakerExchangesTwoCards(String player1Card, String player3Card) throws GameException {
		Board board = actionService.play(game, player1, seer, new SwitchCards(player1Card, player3Card));
		BoardAssertions.assertThat(board)
			.currentRole(new Role("troublemaker", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard(player3Card)
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard(player1Card)
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}


	private void troublemakerCloseYourEyes() throws GameException {
		BoardAssertions.assertThat(gameLogicService.closeEyes(game))
			.currentRole(new Role("troublemaker", ""))
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-1")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-3")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}


	private void everyOneWakeUp() throws GameException {
		BoardAssertions.assertThat(gameLogicService.wakeUpEveryone(game))
			.currentRole(null)
			.cards()
				.hidden()
				.and()
			.player(player1)
				.hasCard("card-1")
				.and()
			.player(player2)
				.hasCard("card-5")
				.and()
			.player(player3)
				.hasCard("card-3")
				.and()
			.middle()
				.place("card-4", 0)
				.place("card-6", 1)
				.place("card-2", 2);
	}

	private void discuss() {
		// TODO Auto-generated method stub
		
	}

	private void vote() throws GameException {
		// player1 votes against player3
		// player2 votes against player3
		// player3 votes against player1
		voteService.vote(game, player1, player3);
		voteService.vote(game, player2, player3);
		voteService.vote(game, player3, player1);
		// TODO: add assertions
	}


	private List<String> allExcept(String... cards) {
		return asList("card-1", "card-2", "card-3", "card-4", "card-5", "card-6")
			.stream()
			.filter(id -> !asList(cards).contains(id))
			.collect(toList());
	}

}
