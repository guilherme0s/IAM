package io.github.guilherme0s.iam.oauth2.authorization;

public class OAuth2AuthorizationException extends RuntimeException {

    private final String errorCode;

    public OAuth2AuthorizationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
