package com.zerori.dev.zerori_image;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrResException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public ErrResException(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }

    public ErrResException(final HttpStatus status, final String messageFormat, Object... messageArgs) {
        this.status = status;
        this.message = MessageFormatter.arrayFormat(messageFormat, messageArgs).getMessage();
    }

    public HttpStatus getHttpStatus() {
        return status;
    }

    public ErrorResponseBody getErrorResponseBody() {
        return ErrorResponseBody.builder()
                .message(message)
                .build();
    }

    @Getter
    @Builder
    @ToString
    public static class ErrorResponseBody {
        private String message;
    }
}
