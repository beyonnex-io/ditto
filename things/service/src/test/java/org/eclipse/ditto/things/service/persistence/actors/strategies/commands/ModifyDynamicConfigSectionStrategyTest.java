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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoDiagnosticLoggingAdapter;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultVisitor;
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
import org.eclipse.ditto.things.model.devops.commands.ModifyDynamicConfigSection;
import org.eclipse.ditto.things.model.devops.commands.ModifyWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigModified;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link ModifyDynamicConfigSectionStrategy}.
 */
public final class ModifyDynamicConfigSectionStrategyTest {

    private ModifyDynamicConfigSectionStrategy underTest;
    private WotValidationConfigDData ddata;
    private ActorSystem actorSystem;
    private CommandStrategy.Context<WotValidationConfigId> context;

    @Mock
    private DittoDiagnosticLoggingAdapter logger;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        actorSystem = mock(ActorSystem.class);
        ddata = mock(WotValidationConfigDData.class);
        context = mock(CommandStrategy.Context.class);
        
        // Set up logger mock
        when(logger.withCorrelationId(any(WithDittoHeaders.class))).thenReturn(logger);
        when(logger.withCorrelationId(any(DittoHeaders.class))).thenReturn(logger);
        when(logger.withCorrelationId(any(CharSequence.class))).thenReturn(logger);
        when(context.getLog()).thenReturn(logger);
        
        // Set up ddata mock to return a non-null CompletionStage
        when(ddata.add(any())).thenReturn(CompletableFuture.completedFuture(null));
        
        underTest = new ModifyDynamicConfigSectionStrategy(ddata);
    }

    @Test
    public void modifyDynamicConfigSection() {
        // Given
        final WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        final DittoHeaders headers = DittoHeaders.empty();
        final ImmutableValidationContext validationContext = ImmutableValidationContext.of(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "scope1");
        final ImmutableConfigOverrides configOverrides = ImmutableConfigOverrides.of(true, false, null, null);
        final ImmutableDynamicValidationConfig dynamicSection = ImmutableDynamicValidationConfig.of("scope1", validationContext, configOverrides);
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
                Collections.singletonList(dynamicSection),
                WotValidationConfigRevision.of(1L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );
        final ImmutableConfigOverrides newConfigOverrides = ImmutableConfigOverrides.of(false, true, null, null);
        final ImmutableDynamicValidationConfig newSection = ImmutableDynamicValidationConfig.of("scope1", validationContext, newConfigOverrides);
        final ModifyDynamicConfigSection command = ModifyDynamicConfigSection.of(configId, "scope1", newSection, headers);

        // When
        final Result<WotValidationConfigEvent<?>> result = underTest.apply(context, existingConfig, 2L, command);

        // Then
        final ArgumentCaptor<WotValidationConfigEvent<?>> eventCaptor = ArgumentCaptor.forClass(WotValidationConfigEvent.class);
        final ArgumentCaptor<ModifyWotValidationConfigResponse> responseCaptor = ArgumentCaptor.forClass(ModifyWotValidationConfigResponse.class);
        final ResultVisitor<WotValidationConfigEvent<?>> visitor = mock(ResultVisitor.class);

        result.accept(visitor, null);

        verify(visitor).onMutation(eq(command), eventCaptor.capture(), responseCaptor.capture(), eq(false), eq(false), eq(null));

        final WotValidationConfigEvent<?> event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(WotValidationConfigModified.class);
        final WotValidationConfigModified modifiedEvent = (WotValidationConfigModified) event;
        assertThat(modifiedEvent.getEntityId().toString()).isEqualTo(configId.toString());
        // Optionally, check that the new dynamic section is present in the config
        assertThat(modifiedEvent.getConfig().getDynamicConfig().stream().anyMatch(s -> s.getScopeId().equals("scope1") && Boolean.FALSE.equals(s.getConfigOverrides().get().getEnabled().orElse(null)))).isTrue();

        final ModifyWotValidationConfigResponse response = responseCaptor.getValue();
        assertThat(response).isNotNull();
    }
} 