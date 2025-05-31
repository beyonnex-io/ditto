/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.things.service.persistence.actors.strategies.commands;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoDiagnosticLoggingAdapter;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultVisitor;
import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.things.model.devops.ImmutableConfigOverrides;
import org.eclipse.ditto.things.model.devops.ImmutableDynamicValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableValidationContext;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.things.model.devops.commands.RetrieveAllDynamicConfigSections;
import org.eclipse.ditto.things.model.devops.commands.RetrieveWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link RetrieveAllDynamicConfigSectionsStrategy}.
 */
public final class RetrieveAllDynamicConfigSectionsStrategyTest {

    private RetrieveAllDynamicConfigSectionsStrategy underTest;
    private ActorSystem actorSystem;
    private CommandStrategy.Context<WotValidationConfigId> context;

    @Mock
    private DittoDiagnosticLoggingAdapter logger;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        actorSystem = mock(ActorSystem.class);
        context = mock(CommandStrategy.Context.class);
        
        // Set up logger mock
        when(logger.withCorrelationId(any(WithDittoHeaders.class))).thenReturn(logger);
        when(logger.withCorrelationId(any(DittoHeaders.class))).thenReturn(logger);
        when(logger.withCorrelationId(any(CharSequence.class))).thenReturn(logger);
        when(context.getLog()).thenReturn(logger);
        
        underTest = new RetrieveAllDynamicConfigSectionsStrategy();
    }

    @Test
    public void retrieveAllDynamicConfigSections() {
        // Given
        final WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        final DittoHeaders headers = DittoHeaders.empty();
        final ImmutableValidationContext validationContext1 = ImmutableValidationContext.of(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "scope1");
        final ImmutableConfigOverrides configOverrides1 = ImmutableConfigOverrides.of(true, false, null, null);
        final ImmutableDynamicValidationConfig dynamicSection1 = ImmutableDynamicValidationConfig.of("scope1", validationContext1, configOverrides1);
        final ImmutableValidationContext validationContext2 = ImmutableValidationContext.of(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "scope2");
        final ImmutableConfigOverrides configOverrides2 = ImmutableConfigOverrides.of(false, true, null, null);
        final ImmutableDynamicValidationConfig dynamicSection2 = ImmutableDynamicValidationConfig.of("scope2", validationContext2, configOverrides2);
        final ImmutableWotValidationConfig existingConfig = ImmutableWotValidationConfig.of(
                configId,
                true,
                false,
                ImmutableThingValidationConfig.of(
                    ImmutableThingValidationEnforceConfig.of(true, true, true, true, true),
                    ImmutableThingValidationForbidConfig.of(false, true, false, true)
                ),
                ImmutableFeatureValidationConfig.of(
                    ImmutableFeatureValidationEnforceConfig.of(true, false, true, false, true, false, true),
                    ImmutableFeatureValidationForbidConfig.of(false, true, false, true, false, true)
                ),
                Arrays.asList(dynamicSection1, dynamicSection2),
                WotValidationConfigRevision.of(1L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );
        final RetrieveAllDynamicConfigSections command = RetrieveAllDynamicConfigSections.of(configId, headers);

        // When
        final Result<WotValidationConfigEvent<?>> result = underTest.apply(context, existingConfig, 1L, command);

        // Then
        final ArgumentCaptor<RetrieveWotValidationConfigResponse> responseCaptor = ArgumentCaptor.forClass(RetrieveWotValidationConfigResponse.class);
        final ResultVisitor<WotValidationConfigEvent<?>> visitor = mock(ResultVisitor.class);

        result.accept(visitor, null);

        verify(visitor).onQuery(eq(command), responseCaptor.capture());

        final RetrieveWotValidationConfigResponse response = responseCaptor.getValue();
        assertThat(response).isNotNull();
        final JsonArray dynamicConfigs = response.getValidationConfig().asObject().getValue("dynamicConfigs").get().asArray();
        assertThat(dynamicConfigs).hasSize(2);
        assertThat(dynamicConfigs.stream().anyMatch(jv -> jv.asObject().getValue("scopeId").get().asString().equals("scope1"))).isTrue();
        assertThat(dynamicConfigs.stream().anyMatch(jv -> jv.asObject().getValue("scopeId").get().asString().equals("scope2"))).isTrue();
    }
} 