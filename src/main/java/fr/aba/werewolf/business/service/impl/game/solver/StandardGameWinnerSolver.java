package fr.aba.werewolf.business.service.impl.game.solver;

import static fr.aba.werewolf.business.service.impl.game.solver.SolverUtils.getPlayerWithRole;
import static fr.aba.werewolf.business.service.impl.game.solver.SolverUtils.getRoleOf;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.domain.Player;
import fr.aba.werewolf.business.domain.Role;
import fr.aba.werewolf.business.domain.state.Board;
import fr.aba.werewolf.business.domain.state.Card;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StandardGameWinnerSolver implements WinnersSolver {
	private final Set<String> villagerTeam;
	private final Set<String> werewolvesTeam;

	@Override
	public Set<Player> computeWinners(Game game, Board board, Set<Player> deads) {
		/*
		 * The village team wins:
		 * 
		 * 1) If at least one Werewolf dies. Even if one or more players who are not
		 * Werewolves die in addition to a Werewolf dying, everyone on the village team
		 * wins.
		 * 
		 * 2) If no one is a Werewolf and no one dies. It is possible for no one to be a
		 * Werewolf if all Werewolf cards are in the center.
		 */
		boolean villagersWin = atLeastOneWerewolfIsKilled(deads, board) || noOneIsWerewolf(game, board) && noOneDied(deads);
		/*
		 * The werewolf team only wins if at least one player is a Werewolf and no
		 * Werewolves are killed
		 */
		boolean werewolvesWin = atLeastOnePlayerIsWerewolf(game, board) && noWerewolvesAreKilled(deads, board);
		boolean tannerWins = false;
		if (tannerPlaying(game, board)) {
			// The Tanner only wins if he dies.
			boolean tannerDied = isTannerDead(deads, board);
			tannerWins = tannerDied;
			// If the Tanner dies and no Werewolves die, the Werewolves do not win
			if(noWerewolvesAreKilled(deads, board) && tannerDied) {
				werewolvesWin = false;
			}
			// If the Tanner dies and a Werewolf also dies, the village team wins too.
			if(tannerDied && atLeastOneWerewolfIsKilled(deads, board)) {
				villagersWin = true;
			}
			// The Tanner is considered a member of the village (but is not on their
			// team), so if the Tanner dies when all werewolves
			// are in the center, the village team loses
			if(tannerDied && allWerewolvesAreInCenter(game, board)) {
				villagersWin = false;
			}
		}
		// compute winners
		Set<Player> winners = new HashSet<>();
		if(villagersWin) {
			winners.addAll(getVillagers(game, board));
		}
		if(werewolvesWin) {
			winners.addAll(getWerewolves(game, board));
		}
		if(tannerWins) {
			winners.add(getTanner(game, board));
		}
		return winners;
	}

	private boolean noOneDied(Set<Player> deads) {
		return deads.isEmpty();
	}

	private boolean atLeastOnePlayerIsWerewolf(Game game, Board board) {
		return game.getPlayers()
				.stream()
				.map(p -> getRoleOf(p, board))
				.anyMatch(r -> r.is("werewolf"));
	}

	private boolean noOneIsWerewolf(Game game, Board board) {
		return game.getPlayers()
				.stream()
				.map(p -> getRoleOf(p, board))
				.noneMatch(r -> r.is("werewolf"));
	}

	private boolean tannerPlaying(Game game, Board board) {
		return getPlayerWithRole("tanner", game, board) != null;
	}

	private boolean isTannerDead(Set<Player> deads, Board board) {
		return deads
				.stream()
				.map(p -> getRoleOf(p, board))
				.anyMatch(r -> r.is("tanner"));
	}

	private boolean noWerewolvesAreKilled(Set<Player> deads, Board board) {
		return deads
				.stream()
				.map(p -> getRoleOf(p, board))
				.noneMatch(r -> r.is("werewolf"));
	}

	private boolean atLeastOneWerewolfIsKilled(Set<Player> deads, Board board) {
		return deads
				.stream()
				.map(p -> getRoleOf(p, board))
				.anyMatch(r -> r.is("werewolf"));
	}

	private boolean allWerewolvesAreInCenter(Game game, Board board) {
		long howManyWerewolves = game.getSelectedRoles()
				.stream()
				.filter(r -> r.is("werewolf"))
				.count();
		long howManyWerewolvesInCenter = board.getAllPlayerBoards().get(0).getCardsInTheMiddle()
				.stream()
				.map(Card::getRole)
				.filter(r -> r.is("werewolf"))
				.count();
		return howManyWerewolves == howManyWerewolvesInCenter;
	}

	private Set<Player> getVillagers(Game game, Board board) {
		return game.getPlayers()
				.stream()
				.filter(p -> isInVillagerTeam(p, game, board))
				.collect(toSet());
	}

	private boolean isInVillagerTeam(Player player, Game game, Board board) {
		Role role = getRoleOf(player, board);
		return villagerTeam
				.stream()
				.anyMatch(role::is);
	}

	private Set<Player> getWerewolves(Game game, Board board) {
		return game.getPlayers()
				.stream()
				.filter(p -> isInWerewolfTeam(p, game, board))
				.collect(toSet());
	}

	private boolean isInWerewolfTeam(Player player, Game game, Board board) {
		Role role = getRoleOf(player, board);
		return werewolvesTeam
				.stream()
				.anyMatch(role::is);
	}
	
	private Player getTanner(Game game, Board board) {
		return getPlayerWithRole("tanner", game, board);
	}
}
