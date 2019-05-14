package fr.aba.werewolf.ut.service;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.STRICT_STUBS;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Phase;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameException;
import fr.aba.werewolf.business.service.RoleService;
import fr.aba.werewolf.business.service.impl.game.StandardGameLogicService;
import fr.aba.werewolf.business.service.impl.game.config.RulesProperties;

public class StandardGameLogicServiceTest {
	@Rule public MockitoRule mockito = MockitoJUnit.rule().strictness(STRICT_STUBS);
	
	@Mock RulesProperties rules;
	@Mock BoardService boardService;
	@Mock RoleService roleService;
	@Mock Game game;
	Board board;
	Phase currentPhase;
	Role werewolf = new Role("werewolf", "");
	Role seer = new Role("seer", "");
	Role robber = new Role("robber", "");
	Role troublemaker = new Role("troublemaker", "");
	
	StandardGameLogicService logicService;
	
	
	@Before
	public void setup() throws GameException {
		when(game.getId()).thenReturn("1");
		board = new Board(ofMinutes(5), ofSeconds(3), new HashMap<>());
		when(boardService.distributeCards(any())).thenReturn(board);
		when(boardService.getCurrentBoard(any())).thenAnswer(new Answer<Board>() {
			@Override
			public Board answer(InvocationOnMock invocation) throws Throwable {
				return board;
			}
		});
		when(boardService.updateBoard(any(), any())).thenAnswer(new Answer<Board>() {
			@Override
			public Board answer(InvocationOnMock invocation) throws Throwable {
				board = invocation.getArgument(1);
				currentPhase = board.getPhase();
				return board;
			}
		});
		when(roleService.getNextRole(any(), any()))
			.thenReturn(werewolf)		// shouldWakeUp condition
			.thenReturn(werewolf)		// wake up log
			.thenReturn(seer)			// shouldWakeUp condition
			.thenReturn(seer)			// wake up log
			.thenReturn(robber)			// shouldWakeUp condition
			.thenReturn(robber)			// wake up log
			.thenReturn(troublemaker)	// shouldWakeUp condition
			.thenReturn(null);
		logicService = new StandardGameLogicService(rules, boardService, roleService);
	}
	
	@Test
	public void standardGameTest() throws GameException {
		// start game => sunset
		logicService.distribute(game);
		assertThat(currentPhase).isEqualTo(null);
		// close everyone eyes
		logicService.closeEveryoneEyes(game);
		assertThat(currentPhase).isEqualTo(Phase.SUNSET);
		// wake up werewolves
		logicService.wakeUp(game);
		assertThat(currentPhase).isEqualTo(Phase.AWAKE);
		// close eyes
		logicService.closeEyes(game);
		assertThat(currentPhase).isEqualTo(Phase.SLEEPING);
		// wake up seer
		logicService.wakeUp(game);
		assertThat(currentPhase).isEqualTo(Phase.AWAKE);
		// close eyes
		logicService.closeEyes(game);
		assertThat(currentPhase).isEqualTo(Phase.SLEEPING);
		// wake up robber
		logicService.wakeUp(game);
		assertThat(currentPhase).isEqualTo(Phase.AWAKE);
		// close eyes
		logicService.closeEyes(game);
		assertThat(currentPhase).isEqualTo(Phase.SLEEPING);
		// wake up troublemaker
		logicService.wakeUp(game);
		assertThat(currentPhase).isEqualTo(Phase.AWAKE);
		// close eyes
		logicService.closeEyes(game);
		assertThat(currentPhase).isEqualTo(Phase.SLEEPING);
		// sunrise
		logicService.wakeUpEveryone(game);
		assertThat(currentPhase).isEqualTo(Phase.SUNRISE);
	}

}
