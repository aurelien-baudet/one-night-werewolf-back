package fr.aba.werewolf.web.error;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.aba.werewolf.business.service.impl.game.exception.GameNotFoundException;
import fr.aba.werewolf.web.dto.ErrorWrapper;

@ControllerAdvice
@RestControllerAdvice
public class BusinessExceptionTranslator {

	@MessageExceptionHandler(GameNotFoundException.class)
	@ExceptionHandler(GameNotFoundException.class)
	@ResponseStatus(code=HttpStatus.NOT_FOUND)
	public ErrorWrapper handle(GameNotFoundException e) {
		return new ErrorWrapper(e, Pair.of("gameId", e.getGameId()));
	}
}
