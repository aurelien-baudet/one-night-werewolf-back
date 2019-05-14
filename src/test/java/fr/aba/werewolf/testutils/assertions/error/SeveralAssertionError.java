package fr.aba.werewolf.testutils.assertions.error;

import static java.util.stream.Collectors.joining;

import java.util.List;

import junit.framework.AssertionFailedError;
import lombok.Getter;

@Getter
public class SeveralAssertionError extends AssertionFailedError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final List<AssertionFailedError> failures;

	public SeveralAssertionError(List<AssertionFailedError> failures) {
		super(prepareMessage(failures));
		this.failures = failures;
	}

	private static String prepareMessage(List<AssertionFailedError> failures) {
		return failures
				.stream()
				.map(Object::toString)
				.collect(joining("\n", "There are several failed assertions:\n", ""));
	}
	
	
}
