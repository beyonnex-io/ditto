/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.gateway.service.endpoints.directives.auth;

import static org.apache.pekko.http.javadsl.server.Directives.extractRequestContext;
import static org.eclipse.ditto.base.model.common.ConditionChecker.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.apache.pekko.http.javadsl.model.HttpHeader;
import org.apache.pekko.http.javadsl.server.AuthorizationFailedRejection;
import org.apache.pekko.http.javadsl.server.Directives;
import org.apache.pekko.http.javadsl.server.RequestContext;
import org.apache.pekko.http.javadsl.server.Route;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.gateway.api.GatewayAuthenticationFailedException;
import org.eclipse.ditto.gateway.service.security.authentication.AuthenticationResult;
import org.eclipse.ditto.gateway.service.security.authentication.jwt.JwtAuthenticationProvider;
import org.eclipse.ditto.gateway.service.util.config.security.DevOpsConfig;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoLoggerFactory;
import org.eclipse.ditto.internal.utils.pekko.logging.ThreadSafeDittoLogger;

import scala.util.Try;

/**
 * Custom Pekko Http directive performing oauth2 with an {@link #expectedSubjects expected subject}.
 */
public final class DevOpsOAuth2AuthenticationDirective implements DevopsAuthenticationDirective {

    private static final ThreadSafeDittoLogger LOGGER =
            DittoLoggerFactory.getThreadSafeLogger(DevOpsOAuth2AuthenticationDirective.class);

    /**
     * The Http basic auth realm for the "ditto-devops" user used for /devops resource.
     */
    public static final String REALM_DEVOPS = "DITTO-DEVOPS";

    /**
     * The Http basic auth realm for the "ditto-devops" user used for /status resource.
     */
    public static final String REALM_STATUS = "DITTO-STATUS";

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final Collection<String> expectedSubjects;

    private DevOpsOAuth2AuthenticationDirective(final JwtAuthenticationProvider jwtAuthenticationProvider,
            final Collection<String> expectedSubjects) {

        this.jwtAuthenticationProvider = checkNotNull(jwtAuthenticationProvider, "jwtAuthenticationProvider");
        this.expectedSubjects = expectedSubjects;
    }

    /**
     * Returns an instance of {@code DevOpsAuthenticationDirective}.
     *
     * @param devOpsConfig the configuration settings of the Gateway service's DevOps endpoint.
     * @param jwtAuthenticationProvider the authentication provider OAuth2 authentication at status resources.
     * @return the instance.
     * @throws NullPointerException if {@code devOpsConfig} is {@code null}.
     */
    public static DevOpsOAuth2AuthenticationDirective status(final DevOpsConfig devOpsConfig,
            final JwtAuthenticationProvider jwtAuthenticationProvider) {

        final Collection<String> expectedSubjects = devOpsConfig.getStatusOAuth2Subjects();
        return new DevOpsOAuth2AuthenticationDirective(jwtAuthenticationProvider, expectedSubjects);
    }

    /**
     * Returns an instance of {@code DevOpsAuthenticationDirective}.
     *
     * @param devOpsConfig the configuration settings of the Gateway service's DevOps endpoint.
     * @param jwtAuthenticationProvider the authentication provider OAuth2 authentication at devops resources.
     * @return the instance.
     * @throws NullPointerException if {@code devOpsConfig} is {@code null}.
     */
    public static DevOpsOAuth2AuthenticationDirective devops(final DevOpsConfig devOpsConfig,
            final JwtAuthenticationProvider jwtAuthenticationProvider) {

        final Collection<String> expectedSubjects = devOpsConfig.getDevopsOAuth2Subjects();
        return new DevOpsOAuth2AuthenticationDirective(jwtAuthenticationProvider, expectedSubjects);
    }

    @Override
    public Route authenticateDevOps(final String realm, final DittoHeaders dittoHeaders, final Route inner) {
        final ThreadSafeDittoLogger logger = LOGGER.withCorrelationId(dittoHeaders);
        logger.debug("DevOps OAuth authentication is enabled for {}.", realm);
        return extractRequestContext(requestContext -> {
            final String authorizationHeaderValue = requestContext.getRequest()
                    .getHeader("authorization")
                    .map(HttpHeader::value)
                    .orElse("");
            logger.debug("Trying to use OAuth2 authentication for authorization header <{}>", authorizationHeaderValue);
            final CompletionStage<AuthenticationResult> authenticationResult =
                    jwtAuthenticationProvider.authenticate(requestContext, dittoHeaders);

            final Function<Try<AuthenticationResult>, Route> handleAuthenticationTry =
                    authenticationResultTry ->
                            handleAuthenticationTry(authenticationResultTry, dittoHeaders, inner, requestContext);

            return Directives.onComplete(authenticationResult, handleAuthenticationTry);
        });
    }

    private Route handleAuthenticationTry(final Try<AuthenticationResult> authenticationResultTry,
            final DittoHeaders dittoHeaders,
            final Route inner,
            final RequestContext requestContext) {

        if (authenticationResultTry.isSuccess()) {
            final AuthenticationResult authenticationResult = authenticationResultTry.get();
            final ThreadSafeDittoLogger logger = LOGGER.withCorrelationId(dittoHeaders);
            if (!authenticationResult.isSuccess()) {
                logger.info("DevOps OAuth authentication was not successful for request: '{}' because of '{}'.",
                        requestContext.getRequest(), authenticationResult.getReasonOfFailure().getMessage());
                return Directives.failWith(authenticationResult.getReasonOfFailure());
            } else {
                final List<String> authorizationSubjectIds =
                        authenticationResult.getAuthorizationContext().getAuthorizationSubjectIds();
                final boolean isAuthorized = expectedSubjects.isEmpty() || authorizationSubjectIds.stream().anyMatch(expectedSubjects::contains);
                if (isAuthorized) {
                    logger.info("DevOps Oauth authentication was successful, user subjects {} were " +
                            "part of expected subjects: {}", authorizationSubjectIds, expectedSubjects);
                    return inner;
                } else {
                    final String message = String.format(
                            "Unauthorized subject(s): <%s>. Expected: <%s>",
                            authorizationSubjectIds, expectedSubjects
                    );
                    final GatewayAuthenticationFailedException reasonOfFailure =
                            GatewayAuthenticationFailedException.fromMessage(message, dittoHeaders);
                    logger.warn("DevOps Oauth authentication failed.", reasonOfFailure);
                    return Directives.failWith(reasonOfFailure);
                }
            }
        }
        return Directives.reject(AuthorizationFailedRejection.get());
    }

}
