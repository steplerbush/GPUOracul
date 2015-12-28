package org.oracul.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.SERVICE_UNAVAILABLE, reason="GPU Service is overloaded")
public class QueueOverflowException extends RuntimeException {

}
