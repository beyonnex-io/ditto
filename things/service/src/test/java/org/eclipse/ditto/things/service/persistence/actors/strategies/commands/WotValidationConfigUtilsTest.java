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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Collections;

import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableFeatureValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationEnforceConfig;
import org.eclipse.ditto.things.model.devops.ImmutableThingValidationForbidConfig;
import org.eclipse.ditto.things.model.devops.ImmutableWotValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.WotValidationConfigRevision;
import org.eclipse.ditto.wot.api.config.DefaultTmValidationConfig;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.junit.Test;

public class WotValidationConfigUtilsTest {

    @Test
    public void mergeConfigs_returnsStaticConfigIfEntityNull() {
        String configString = "tm-model-validation {\n" +
                "  enabled = true\n" +
                "  log-warning-instead-of-failing-api-calls = false\n" +
                "  thing {\n" +
                "    enforce {\n" +
                "      thing-description-modification = true\n" +
                "      attributes = true\n" +
                "      inbox-messages-input = true\n" +
                "      inbox-messages-output = true\n" +
                "      outbox-messages = true\n" +
                "    }\n" +
                "    forbid {\n" +
                "      thing-description-deletion = true\n" +
                "      non-modeled-attributes = true\n" +
                "      non-modeled-inbox-messages = true\n" +
                "      non-modeled-outbox-messages = true\n" +
                "    }\n" +
                "  }\n" +
                "  feature {\n" +
                "    enforce {\n" +
                "      feature-description-modification = true\n" +
                "      presence-of-modeled-features = true\n" +
                "      properties = true\n" +
                "      desired-properties = true\n" +
                "      inbox-messages-input = true\n" +
                "      inbox-messages-output = true\n" +
                "      outbox-messages = true\n" +
                "    }\n" +
                "    forbid {\n" +
                "      feature-description-deletion = true\n" +
                "      non-modeled-features = true\n" +
                "      non-modeled-properties = true\n" +
                "      non-modeled-desired-properties = true\n" +
                "      non-modeled-inbox-messages = true\n" +
                "      non-modeled-outbox-messages = true\n" +
                "    }\n" +
                "  }\n" +
                "  dynamic-configuration = []\n" +
                "}";
        TmValidationConfig staticConfig = DefaultTmValidationConfig.of(com.typesafe.config.ConfigFactory.parseString(configString));

        ImmutableWotValidationConfig merged = WotValidationConfigUtils.mergeConfigs(null, staticConfig);
        assertThat(merged.isEnabled()).contains(true);
        assertThat(merged.logWarningInsteadOfFailingApiCalls()).contains(false);
        assertThat(merged.getThingConfig()).isPresent();
        assertThat(merged.getFeatureConfig()).isPresent();
        assertThat(merged.getThingConfig().get().getEnforce().get().isThingDescriptionModification().get()).isEqualTo(true);
        assertThat(merged.getFeatureConfig().get().getEnforce().get().isFeatureDescriptionModification().get()).isEqualTo(true);
    }

    @Test
    public void mergeConfigs_mergesEntityAndStaticConfig() {
        ImmutableThingValidationEnforceConfig thingEnforce = ImmutableThingValidationEnforceConfig.of(false, false, false, false, false);
        ImmutableThingValidationForbidConfig thingForbid = ImmutableThingValidationForbidConfig.of(false, false, false, false);
        ImmutableThingValidationConfig thingConfig = ImmutableThingValidationConfig.of(thingEnforce, thingForbid);
        ImmutableFeatureValidationEnforceConfig featureEnforce = ImmutableFeatureValidationEnforceConfig.of(false, false, false, false, false, false, false);
        ImmutableFeatureValidationForbidConfig featureForbid = ImmutableFeatureValidationForbidConfig.of(false, false, false, false, false, false);
        ImmutableFeatureValidationConfig featureConfig = ImmutableFeatureValidationConfig.of(featureEnforce, featureForbid);
        ImmutableWotValidationConfig entity = ImmutableWotValidationConfig.of(
                WotValidationConfigId.of("test:1"),
                false,
                true,
                thingConfig,
                featureConfig,
                Collections.emptyList(),
                WotValidationConfigRevision.of(1L),
                Instant.now(),
                Instant.now(),
                false,
                null
        );
        String configString = "tm-model-validation {\n" +
                "  enabled = true\n" +
                "  log-warning-instead-of-failing-api-calls = false\n" +
                "  thing {\n" +
                "    enforce {\n" +
                "      thing-description-modification = true\n" +
                "      attributes = true\n" +
                "      inbox-messages-input = true\n" +
                "      inbox-messages-output = true\n" +
                "      outbox-messages = true\n" +
                "    }\n" +
                "    forbid {\n" +
                "      thing-description-deletion = true\n" +
                "      non-modeled-attributes = true\n" +
                "      non-modeled-inbox-messages = true\n" +
                "      non-modeled-outbox-messages = true\n" +
                "    }\n" +
                "  }\n" +
                "  feature {\n" +
                "    enforce {\n" +
                "      feature-description-modification = true\n" +
                "      presence-of-modeled-features = true\n" +
                "      properties = true\n" +
                "      desired-properties = true\n" +
                "      inbox-messages-input = true\n" +
                "      inbox-messages-output = true\n" +
                "      outbox-messages = true\n" +
                "    }\n" +
                "    forbid {\n" +
                "      feature-description-deletion = true\n" +
                "      non-modeled-features = true\n" +
                "      non-modeled-properties = true\n" +
                "      non-modeled-desired-properties = true\n" +
                "      non-modeled-inbox-messages = true\n" +
                "      non-modeled-outbox-messages = true\n" +
                "    }\n" +
                "  }\n" +
                "  dynamic-configuration = []\n" +
                "}";
        TmValidationConfig staticConfig = DefaultTmValidationConfig.of(com.typesafe.config.ConfigFactory.parseString(configString));

        ImmutableWotValidationConfig merged = WotValidationConfigUtils.mergeConfigs(entity, staticConfig);
        assertThat(merged.isEnabled()).contains(false); // entity takes precedence
        assertThat(merged.logWarningInsteadOfFailingApiCalls()).contains(true);
        assertThat(merged.getThingConfig()).isPresent();
        assertThat(merged.getFeatureConfig()).isPresent();
        assertThat(merged.getThingConfig().get().getEnforce().get().isThingDescriptionModification().get()).isEqualTo(false);
        assertThat(merged.getFeatureConfig().get().getEnforce().get().isFeatureDescriptionModification().get()).isEqualTo(false);
    }

    @Test
    public void mergeConfigs_throwsOnNullStaticConfig() {
        assertThatThrownBy(() -> WotValidationConfigUtils.mergeConfigs(null, null)).isInstanceOf(NullPointerException.class);
    }
}