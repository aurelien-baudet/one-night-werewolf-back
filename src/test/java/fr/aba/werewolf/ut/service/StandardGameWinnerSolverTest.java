package fr.aba.werewolf.ut.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.InFrontOfPlayer;
import fr.aba.werewolf.business.domain.state.InTheMiddle;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.business.service.impl.game.solver.StandardGameWinnerSolver;

public class StandardGameWinnerSolverTest {
	@Rule public MockitoRule mockito = MockitoJUnit.rule();

	StandardGameWinnerSolver solver;
	
	@Mock Board board;
	@Mock PlayerBoard playerBoard;
	@Mock Game game;
	
	Player player1 = new Player("1", "");
	Player player2 = new Player("2", "");
	Player player3 = new Player("3", "");
	Player player4 = new Player("4", "");
	Player player5 = new Player("5", "");
	
	@Mock Card werewolf1;
	@Mock Card werewolf2;
	@Mock Card seer;
	@Mock Card robber;
	@Mock Card troublemaker;
	@Mock Card hunter;
	@Mock Card tanner;
	@Mock Card insomniac;
	
	@Before
	public void setup() {
		solver = new StandardGameWinnerSolver(
				new HashSet<>(asList(
					"seer",
					"robber",
					"troublemaker",
					"hunter",
					"mason",
					"insomniac",
					"villager"
				)), 
				new HashSet<>(asList(
					"werewolf",
					"minion"
				)));
		when(board.getAllPlayerBoards()).thenReturn(asList(playerBoard));
		when(playerBoard.getCards()).thenReturn(asList(
				werewolf1,
				werewolf2,
				seer,
				robber,
				troublemaker,
				hunter,
				tanner,
				insomniac));
		when(game.getPlayers()).thenReturn(asList(
				player1, 
				player2, 
				player3, 
				player4, 
				player5));
		when(game.getSelectedRoles()).thenReturn(asList(
				new Role("werewolf", ""),
				new Role("werewolf", ""),
				new Role("seer", ""),
				new Role("robber", ""),
				new Role("troublemaker", ""),
				new Role("hunter", ""),
				new Role("tanner", ""),
				new Role("insomniac", "")));
		when(werewolf1.getRole()).thenReturn(new Role("werewolf", ""));
		when(werewolf2.getRole()).thenReturn(new Role("werewolf", ""));
		when(seer.getRole()).thenReturn(new Role("seer", ""));
		when(robber.getRole()).thenReturn(new Role("robber", ""));
		when(troublemaker.getRole()).thenReturn(new Role("troublemaker", ""));
		when(hunter.getRole()).thenReturn(new Role("hunter", ""));
		when(tanner.getRole()).thenReturn(new Role("tanner", ""));
		when(insomniac.getRole()).thenReturn(new Role("insomniac", ""));
	}
	
	/****************************/
	/** playing without tanner **/
	/****************************/
	
	@Test
	public void everyoneIsVillagerAndVoteInSameDirectionSoEveryoneWins() {
		// given
		when(tanner.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(werewolf1.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(hunter.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, emptySet());
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1, player2, player3, player4, player5);
	}
	

	@Test
	public void everyoneVotedInSameDirectionButOneWasWerewolfSoWerewolfWins() {
		// given
		when(tanner.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(hunter.getPosition()).thenReturn(new InTheMiddle(2));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, emptySet());
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1);
	}
	

	@Test
	public void werewolfDiedSoVillagersWin() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(tanner.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player2)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1, player3, player4, player5);
	}


	@Test
	public void werewolfDiedSoVillagersWinAndTheSecondWerewolfLoosesToo() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(insomniac.getPosition()).thenReturn(new InTheMiddle(1));
		when(tanner.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(werewolf2.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player2)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1, player3, player4);
	}
	

	@Test
	public void werewolfSurvivedSoWerewolvesWin() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(insomniac.getPosition()).thenReturn(new InTheMiddle(1));
		when(tanner.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(werewolf2.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player3)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player2, player5);
	}
	

	/*************************/
	/** playing with tanner **/
	/*************************/

	
	@Test
	public void everyoneIsVillagerAndVoteInSameDirectionSoVillagersWinsAndTannerLooses() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(werewolf1.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, emptySet());
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1, player2, player3, player5);
	}
	

	@Test
	public void everyoneVotedInSameDirectionButOneWasWerewolfSoWerewolfWinsAndTannerLooses() {
		// given
		when(robber.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(hunter.getPosition()).thenReturn(new InTheMiddle(2));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(seer.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, emptySet());
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1);
	}
	

	@Test
	public void werewolfDiedSoVillagersWinAndTannerLooses() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player2)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1, player3, player5);
	}


	@Test
	public void werewolfDiedSoVillagersWinAndTheSecondWerewolfLoosesTooAndTannerLooses() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(insomniac.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(werewolf2.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player2)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player1, player3);
	}
	

	@Test
	public void werewolfSurvivedSoWerewolvesWinAndTannerLooses() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(insomniac.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(werewolf2.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player3)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player2, player5);
	}


	@Test
	public void tannerDiesAndNoWerewolfDiesSoTannerWins() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(insomniac.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(werewolf2.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player4)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player4);
	}
	

	@Test
	public void tannerDiesAndWerewolfAlsoDiesSoTannerWinsAndVillagersToo() {
		// given
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(insomniac.getPosition()).thenReturn(new InTheMiddle(1));
		when(tanner.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(werewolf2.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player4, player5)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player4, player1, player3);
	}
	

	@Test
	public void tannerDiesAndAllWerewolfAreInCenterSoTannerWinsAndVillagersLoose() {
		// given
		when(werewolf1.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(hunter.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// when
		Set<Player> winners = solver.computeWinners(game, board, new HashSet<>(asList(player4, player5)));
		// then
		assertThat(winners).containsExactlyInAnyOrder(player4);
	}
}
