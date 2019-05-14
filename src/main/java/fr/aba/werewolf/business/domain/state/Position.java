package fr.aba.werewolf.business.domain.state;

public interface Position {
	static Position of(Position other) {
		if(other == null) {
			return null;
		}
		if(other instanceof InTheMiddle) {
			return new InTheMiddle((InTheMiddle) other);
		}
		if(other instanceof InFrontOfPlayer) {
			return new InFrontOfPlayer((InFrontOfPlayer) other);
		}
		throw new IllegalArgumentException("Unknown position type");
	}
}
