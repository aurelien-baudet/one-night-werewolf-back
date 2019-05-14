package fr.aba.werewolf.web.dto;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.util.Pair;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorWrapper {
	private final String message;
	private final Instant timestamp;
	private final ErrorWrapper cause;
	private final Map<String, Object> context;
	
	public ErrorWrapper(Throwable e) {
		this(e.getMessage(), Instant.now(), e.getCause()==null ? null : new ErrorWrapper(e.getCause()), null);
	}
	
	public ErrorWrapper(Throwable e, Pair<String, Object>... context) {
		this(e.getMessage(), Instant.now(), e.getCause()==null ? null : new ErrorWrapper(e.getCause()), pairsToMap(context));
	}

	private static Map<String, Object> pairsToMap(Pair<String, Object>[] context) {
		return stream(context)
				.collect(toMap(Pair::getFirst, Pair::getSecond));
	}
}
