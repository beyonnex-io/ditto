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
import java.util.Optional;

import org.apache.pekko.actor.ActorSystem;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.headers.WithDittoHeaders;
import org.eclipse.ditto.base.model.headers.entitytag.EntityTag;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoDiagnosticLoggingAdapter;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.internal.utils.persistentactors.results.Result;
import org.eclipse.ditto.internal.utils.persistentactors.results.ResultVisitor;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.things.model.devops.commands.RetrieveMergedWotValidationConfig;
import org.eclipse.ditto.things.model.devops.commands.RetrieveMergedWotValidationConfigResponse;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.wot.validation.config.FeatureValidationConfig;
import org.eclipse.ditto.wot.validation.config.ThingValidationConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link RetrieveMergedWotValidationConfigStrategy}.
 */
public final class RetrieveMergedWotValidationConfigStrategyTest {

    private RetrieveMergedWotValidationConfigStrategy underTest;
    private TmValidationConfig staticConfig;
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

        // Mock static config
        staticConfig = mock(TmValidationConfig.class);
        when(staticConfig.isEnabled()).thenReturn(true);
        when(staticConfig.logWarningInsteadOfFailingApiCalls()).thenReturn(false);

        // Mock thing validation config with enforce/forbid
        final ThingValidationConfig thingValidationConfig = mock(ThingValidationConfig.class);
        final ImmutableThingValidationConfig devopsThingValidationConfig = mock(ImmutableThingValidationConfig.class);
        when(devopsThingValidationConfig.getEnforce()).thenReturn(Optional.of(ImmutableThingValidationEnforceConfig.of(true, true, true, true, true)));
        when(devopsThingValidationConfig.getForbid()).thenReturn(Optional.of(ImmutableThingValidationForbidConfig.of(false, true, false, true)));
        when(staticConfig.getThingValidationConfig()).thenReturn(thingValidationConfig);

        // Mock feature validation config with enforce/forbid
        final FeatureValidationConfig featureValidationConfig = mock(FeatureValidationConfig.class);
        final ImmutableFeatureValidationConfig devopsFeatureValidationConfig = mock(ImmutableFeatureValidationConfig.class);
        when(devopsFeatureValidationConfig.getEnforce()).thenReturn(Optional.of(ImmutableFeatureValidationEnforceConfig.of(true, false, true, false, true, false, true)));
        when(devopsFeatureValidationConfig.getForbid()).thenReturn(Optional.of(ImmutableFeatureValidationForbidConfig.of(false, true, false, true, false, true)));
        when(staticConfig.getFeatureValidationConfig()).thenReturn(featureValidationConfig);

        // Mock dynamic config
        when(staticConfig.getDynamicConfig()).thenReturn(Collections.emptyList());

        underTest = new RetrieveMergedWotValidationConfigStrategy(staticConfig);
    }

    @Test
    public void retrieveMergedConfig() {
        // Given
        final WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        final DittoHeaders headers = DittoHeaders.empty();
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
                Collections.emptyList(),
                WotValidationConfigRevision.of(1L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );
        final RetrieveMergedWotValidationConfig command = RetrieveMergedWotValidationConfig.of(configId, headers);

        // When
        final Result<WotValidationConfigEvent<?>> result = underTest.apply(context, existingConfig, 1L, command);

        // Then
        final ArgumentCaptor<RetrieveMergedWotValidationConfigResponse> responseCaptor = ArgumentCaptor.forClass(RetrieveMergedWotValidationConfigResponse.class);
        final ResultVisitor<WotValidationConfigEvent<?>> visitor = mock(ResultVisitor.class);

        result.accept(visitor, null);

        verify(visitor).onQuery(eq(command), responseCaptor.capture());

        final RetrieveMergedWotValidationConfigResponse response = responseCaptor.getValue();
        assertThat(response).isNotNull();
        final JsonObject mergedConfig = response.getConfig().toJson();

        // Verify merged config values
        assertThat(mergedConfig.getValue("enabled").get().asBoolean()).isTrue();
        assertThat(mergedConfig.getValue("logWarningInsteadOfFailingApiCalls").get().asBoolean()).isFalse();

        // Verify thing validation config
        final JsonObject thingConfig = mergedConfig.getValue("thing").get().asObject();
        final JsonObject thingEnforce = thingConfig.getValue("enforce").get().asObject();
        final JsonObject thingForbid = thingConfig.getValue("forbid").get().asObject();
        assertThat(thingEnforce.getValue("thingDescriptionModification").get().asBoolean()).isTrue();
        assertThat(thingEnforce.getValue("properties").get().asBoolean()).isTrue();
        assertThat(thingForbid.getValue("thingDescriptionDeletion").get().asBoolean()).isFalse();
        assertThat(thingForbid.getValue("nonModeledProperties").get().asBoolean()).isTrue();

        // Verify feature validation config
        final JsonObject featureConfig = mergedConfig.getValue("feature").get().asObject();
        final JsonObject featureEnforce = featureConfig.getValue("enforce").get().asObject();
        final JsonObject featureForbid = featureConfig.getValue("forbid").get().asObject();
        assertThat(featureEnforce.getValue("featureDescriptionModification").get().asBoolean()).isTrue();
        assertThat(featureEnforce.getValue("presenceOfModeledFeatures").get().asBoolean()).isFalse();
        assertThat(featureForbid.getValue("featureDescriptionDeletion").get().asBoolean()).isFalse();
        assertThat(featureForbid.getValue("nonModeledFeatures").get().asBoolean()).isTrue();
    }

    @Test
    public void testPreviousEntityTag() {
        // Given
        final WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        final DittoHeaders headers = DittoHeaders.empty();
        final ImmutableThingValidationEnforceConfig thingEnforce = ImmutableThingValidationEnforceConfig.of(true, true, true, true, true);
        final ImmutableThingValidationForbidConfig thingForbid = ImmutableThingValidationForbidConfig.of(false, true, false, true);
        final ImmutableFeatureValidationEnforceConfig featureEnforce = ImmutableFeatureValidationEnforceConfig.of(true, false, true, false, true, false, true);
        final ImmutableFeatureValidationForbidConfig featureForbid = ImmutableFeatureValidationForbidConfig.of(false, true, false, true, false, true);
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
                Collections.emptyList(),
                WotValidationConfigRevision.of(1L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );
        final RetrieveMergedWotValidationConfig command = RetrieveMergedWotValidationConfig.of(configId, headers);

        // When
        final Optional<EntityTag> entityTag = underTest.previousEntityTag(command, existingConfig);

        // Then
        assertThat(entityTag).isPresent();
        assertThat(entityTag.get().toString()).isEqualTo("\"rev:1\"");
    }

    @Test
    public void testNextEntityTag() {
        // Given
        final WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        final DittoHeaders headers = DittoHeaders.empty();
        final ImmutableWotValidationConfig newConfig = ImmutableWotValidationConfig.of(
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
                Collections.emptyList(),
                WotValidationConfigRevision.of(2L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );
        final RetrieveMergedWotValidationConfig command = RetrieveMergedWotValidationConfig.of(configId, headers);

        // When
        final Optional<EntityTag> entityTag = underTest.nextEntityTag(command, newConfig);

        // Then
        assertThat(entityTag).isPresent();
        assertThat(entityTag.get().toString()).isEqualTo("\"rev:2\"");
    }
} 