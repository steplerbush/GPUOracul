package org.oracul.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Miramax on 28.12.2015.
 */
@ResponseStatus(value= HttpStatus.SERVICE_UNAVAILABLE, reason="GPU Service is unavailable due to internal error")
public class InternalServiceError extends RuntimeException {
}
