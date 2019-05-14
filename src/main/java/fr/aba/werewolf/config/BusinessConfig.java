package fr.aba.werewolf.config;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Card;
import fr.aba.werewolf.business.service.BoardListener;
import fr.aba.werewolf.business.service.BoardService;
import fr.aba.werewolf.business.service.GameService;
import fr.aba.werewolf.business.service.PlayersListener;
import fr.aba.werewolf.business.service.RoleService;
import fr.aba.werewolf.business.service.VideoStreamListener;
import fr.aba.werewolf.business.service.VoteService;
import fr.aba.werewolf.business.service.VotesListener;
import fr.aba.werewolf.business.service.impl.game.InMemoryBoardService;
import fr.aba.werewolf.business.service.impl.game.InMemoryGameService;
import fr.aba.werewolf.business.service.impl.game.InMemoryListenerManager;
import fr.aba.werewolf.business.service.impl.game.InMemoryVoteService;
import fr.aba.werewolf.business.service.impl.game.ListenerManager;
import fr.aba.werewolf.business.service.impl.game.SimpleListRoleService;
import fr.aba.werewolf.business.service.impl.game.config.RulesProperties;
import fr.aba.werewolf.business.service.impl.game.config.TimingProperties;
import fr.aba.werewolf.business.service.impl.game.solver.DeadsSolver;
import fr.aba.werewolf.business.service.impl.game.solver.StandardGameWinnerSolver;
import fr.aba.werewolf.business.service.impl.game.solver.WinnersSolver;
import fr.aba.werewolf.business.service.impl.shuffle.Shuffler;
import fr.aba.werewolf.business.service.impl.uuid.SequenceUuidGenerator;
import fr.aba.werewolf.business.service.impl.uuid.UuidGenerator;

@Configuration
@EnableScheduling
@EnableMongoAuditing
public class BusinessConfig {

	@Bean
	public ListenerManager<PlayersListener> playersListenerManager() {
		return new InMemoryListenerManager<>();
	}
	
	@Bean
	public ListenerManager<BoardListener> boardListenerManager() {
		return new InMemoryListenerManager<>();
	}

	@Bean
	public ListenerManager<VotesListener> votesListenerManager() {
		return new InMemoryListenerManager<>();
	}
	
	@Bean
	public ListenerManager<VideoStreamListener> videoListenerManager() {
		return new InMemoryListenerManager<>();
	}
	
	@Bean
	public WinnersSolver winnersSolver() {
		return new StandardGameWinnerSolver(
				new HashSet<>(asList(
					"seer",
					"robber",
					"troublemaker",
					"hunter",
					"mason",
					"insomniac",
					"villager",
					"drunk"
				)), 
				new HashSet<>(asList(
					"werewolf",
					"minion"
				)));
	}

	@Bean
	public UuidGenerator cardUuidGenerator() {
		return new SequenceUuidGenerator();
	}
	
	@Bean
	public RoleService roleService() {
		return new SimpleListRoleService(
				asList(
					new Role("villager", ""),
					new Role("villager", ""),
					new Role("villager", ""),
					new Role("werewolf", ""),
					new Role("werewolf", ""),
					new Role("seer", ""),
					new Role("robber", ""),
					new Role("troublemaker", ""),
					new Role("tanner", ""),
					new Role("drunk", ""),
					new Role("hunter", ""),
					new Role("mason", ""),
					new Role("mason", ""),
					new Role("insomniac", ""),
					new Role("minion", ""),
					new Role("doppleganger", "")
				),
				asList(
					new Role("doppleganger", ""),
					new Role("werewolf", ""),
					new Role("minion", ""),
					new Role("mason", ""),
					new Role("seer", ""),
					new Role("robber", ""),
					new Role("troublemaker", ""),
					new Role("drunk", ""),
					new Role("insomniac", ""),
					new Role("doppleganger", "")
				)
			);
	}
	
	@Bean
	public TaskScheduler scheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Configuration
	public static class RegisterListeners {
		@Autowired
		ListenerManager<PlayersListener> playersListenerManager;
		@Autowired
		List<PlayersListener> playersListeners;
		@Autowired
		ListenerManager<BoardListener> boardListenerManager;
		@Autowired
		List<BoardListener> boardListeners;
		@Autowired
		ListenerManager<VotesListener> votesListenerManager;
		@Autowired
		List<VotesListener> votesListeners;
		@Autowired
		ListenerManager<VideoStreamListener> videoListenerManager;
		@Autowired
		List<VideoStreamListener> videoListeners;
		
		@EventListener(classes={ContextRefreshedEvent.class})
		public void registerListener() {
			for(PlayersListener listener : playersListeners) {
				playersListenerManager.register(listener);
			}
			for(BoardListener listener : boardListeners) {
				boardListenerManager.register(listener);
			}
			for(VotesListener listener : votesListeners) {
				votesListenerManager.register(listener);
			}
			for(VideoStreamListener listener : videoListeners) {
				videoListenerManager.register(listener);
			}
		}
	}
	
	@Configuration
	@Profile("in-memory")
	public static class InMemoryConfig {
		@Bean
		public GameService gameService(RulesProperties rulesConfig, ListenerManager<PlayersListener> playersListenerManager, TimingProperties timing) {
			return new InMemoryGameService(gameUuidGenerator(), playersListenerManager, rulesConfig, timing);
		}
		
		@Bean
		@Primary
		public BoardService boardService(UuidGenerator cardUuidGenerator, Shuffler<Card> shuffler, ListenerManager<BoardListener> boardListenerManager, TimingProperties timingConfiguration) {
			return new InMemoryBoardService(cardUuidGenerator, shuffler, boardListenerManager, timingConfiguration);
		}

		@Bean
		public VoteService voteService(DeadsSolver deadsSolver, WinnersSolver winnersSolver, BoardService boardService, ListenerManager<VotesListener> votesListenerManager) {
			return new InMemoryVoteService(deadsSolver, winnersSolver, boardService, votesListenerManager);
		}
		
		@Bean
		public UuidGenerator gameUuidGenerator() {
			return new SequenceUuidGenerator();
		}
		
	}
	
}
