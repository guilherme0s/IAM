package io.github.guilherme0s.iam.oauth2.authorization;

import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import io.github.guilherme0s.iam.oauth2.OAuth2ErrorCodes;
import io.github.guilherme0s.iam.oauth2.OAuth2ParameterNames;
import io.micronaut.http.HttpParameters;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/oauth2/authorize")
@Secured(SecurityRule.IS_ANONYMOUS)
public class OAuth2AuthorizationController {

    private static final Set<String> SUPPORTED_RESPONSE_TYPE = Set.of("code");

    @Get
    public void authorize(HttpParameters parameters) {
        String responseType = getSingleValuedParameter(parameters, OAuth2ParameterNames.RESPONSE_TYPE);
        if (responseType == null || responseType.isBlank()) {
            throw missingRequiredParameter(OAuth2ParameterNames.RESPONSE_TYPE);
        }
        if (!SUPPORTED_RESPONSE_TYPE.contains(responseType)) {
            throw new OAuth2AuthorizationException(OAuth2ErrorCodes.UNSUPPORTED_RESPONSE_TYPE,
                    "Unsupported response_type: '%s'. Allowed values: [%s]"
                            .formatted(responseType, SUPPORTED_RESPONSE_TYPE));
        }

        String clientId = getSingleValuedParameter(parameters, OAuth2ParameterNames.CLIENT_ID);
        if (clientId == null || clientId.isBlank()) {
            throw missingRequiredParameter(OAuth2ParameterNames.CLIENT_ID);
        }

        String redirectUri = getSingleValuedParameter(parameters, OAuth2ParameterNames.REDIRECT_URI);
        if (redirectUri == null || redirectUri.isBlank()) {
            throw missingRequiredParameter(OAuth2ParameterNames.REDIRECT_URI);
        }

        String scopeValue = getSingleValuedParameter(parameters, OAuth2ParameterNames.SCOPE);
        if (scopeValue == null || scopeValue.isBlank()) {
            throw missingRequiredParameter(OAuth2ParameterNames.SCOPE);
        }
        Set<String> scopes = Set.of(scopeValue.trim().split("\\s+"));

        String state = getSingleValuedParameter(parameters, OAuth2ParameterNames.STATE);

        String codeChallenge = getSingleValuedParameter(parameters, OAuth2ParameterNames.CODE_CHALLENGE);
        if (codeChallenge == null || codeChallenge.isBlank()) {
            throw missingRequiredParameter(OAuth2ParameterNames.CODE_CHALLENGE);
        }

        String codeChallengeMethod = getSingleValuedParameter(parameters, OAuth2ParameterNames.CODE_CHALLENGE_METHOD);
        if (codeChallengeMethod == null || codeChallengeMethod.isBlank()) {
            throw missingRequiredParameter(OAuth2ParameterNames.CODE_CHALLENGE_METHOD);
        }
    }

    @Nullable
    private static String getSingleValuedParameter(HttpParameters parameters, String parameterName) {
        List<String> values = parameters.getAll(parameterName);
        if (values.size() > 1) {
            throw new OAuth2AuthorizationException(OAuth2ErrorCodes.INVALID_REQUEST,
                    "Parameter must not appear more than once: " + parameterName);
        }
        return values.isEmpty() ? null : values.getFirst();
    }

    private static OAuth2AuthorizationException missingRequiredParameter(String parameterName) {
        return new OAuth2AuthorizationException(OAuth2ErrorCodes.INVALID_REQUEST,
                "Missing required parameter: " + parameterName);
    }
}
