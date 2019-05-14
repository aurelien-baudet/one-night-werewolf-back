package fr.aba.werewolf.ut.service;

import static java.util.Arrays.asList;
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
import fr.aba.werewolf.business.domain.Vote;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.domain.state.InFrontOfPlayer;
import fr.aba.werewolf.business.domain.state.InTheMiddle;
import fr.aba.werewolf.business.domain.state.PlayerBoard;
import fr.aba.werewolf.business.service.impl.game.solver.StandardGameDeadsSolver;

public class StandardGameDeadsSolverTest {
	@Rule public MockitoRule mockito = MockitoJUnit.rule();

	StandardGameDeadsSolver solver;
	
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
		solver = new StandardGameDeadsSolver();
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
		when(werewolf1.getRole()).thenReturn(new Role("werewolf", ""));
		when(werewolf2.getRole()).thenReturn(new Role("werewolf", ""));
		when(seer.getRole()).thenReturn(new Role("seer", ""));
		when(robber.getRole()).thenReturn(new Role("robber", ""));
		when(troublemaker.getRole()).thenReturn(new Role("troublemaker", ""));
		when(hunter.getRole()).thenReturn(new Role("hunter", ""));
		when(tanner.getRole()).thenReturn(new Role("tanner", ""));
		when(insomniac.getRole()).thenReturn(new Role("insomniac", ""));
	}
	
	@Test
	public void noPlayerReceivesMoreThanOneVoteNoOneDies() {
		// every one has voted against only one player
		Set<Player> deads = solver.deads(game, board, new HashSet<>(asList(
				new Vote("1", "2"),
				new Vote("2", "3"),
				new Vote("3", "4"),
				new Vote("4", "5"),
				new Vote("5", "1"))));
		assertThat(deads).isEmpty();
	}
	

	@Test
	public void playerWithMostVotesDiesWithoutHunter() {
		// hunter is in the center
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// every one has voted against only one player
		Set<Player> deads = solver.deads(game, board, new HashSet<>(asList(
				new Vote("1", "2"),
				new Vote("2", "1"),
				new Vote("3", "2"),
				new Vote("4", "2"),
				new Vote("5", "1"))));
		assertThat(deads).containsExactlyInAnyOrder(player2);
	}


	@Test
	public void playerWithMostVotesDiesWithHunter() {
		// hunter is hold by player2
		when(werewolf1.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(hunter.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// every one has voted against only one player
		Set<Player> deads = solver.deads(game, board, new HashSet<>(asList(
				new Vote("1", "2"),
				new Vote("2", "1"),
				new Vote("3", "2"),
				new Vote("4", "2"),
				new Vote("5", "1"))));
		assertThat(deads).containsExactlyInAnyOrder(player2, player1);
	}


	@Test
	public void allPlayersTiedWithTheMostVotesDieWithoutHunter() {
		// hunter is in the center
		when(hunter.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(werewolf1.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// every one has voted against only one player
		Set<Player> deads = solver.deads(game, board, new HashSet<>(asList(
				new Vote("1", "3"),
				new Vote("2", "1"),
				new Vote("3", "2"),
				new Vote("4", "2"),
				new Vote("5", "1"))));
		assertThat(deads).containsExactlyInAnyOrder(player1, player2);
	}
	
	@Test
	public void allPlayersTiedWithTheMostVotesDieWithHunter() {
		// hunter is hold by player1
		when(werewolf1.getPosition()).thenReturn(new InTheMiddle(0));
		when(werewolf2.getPosition()).thenReturn(new InTheMiddle(1));
		when(seer.getPosition()).thenReturn(new InTheMiddle(2));
		when(robber.getPosition()).thenReturn(new InFrontOfPlayer(player1));
		when(hunter.getPosition()).thenReturn(new InFrontOfPlayer(player2));
		when(troublemaker.getPosition()).thenReturn(new InFrontOfPlayer(player3));
		when(tanner.getPosition()).thenReturn(new InFrontOfPlayer(player4));
		when(insomniac.getPosition()).thenReturn(new InFrontOfPlayer(player5));
		// every one has voted against only one player
		Set<Player> deads = solver.deads(game, board, new HashSet<>(asList(
				new Vote("1", "2"),
				new Vote("2", "3"),
				new Vote("3", "2"),
				new Vote("4", "1"),
				new Vote("5", "1"))));
		assertThat(deads).containsExactlyInAnyOrder(player1, player2, player3);
	}
}
