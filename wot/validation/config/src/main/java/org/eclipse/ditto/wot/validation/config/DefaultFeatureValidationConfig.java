/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.wot.validation.config;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.internal.utils.config.ScopedConfig;
import org.eclipse.ditto.json.JsonObject;

import com.typesafe.config.Config;

/**
 * Default implementation of {@link FeatureValidationConfig}.
 */
@Immutable
public final class DefaultFeatureValidationConfig implements FeatureValidationConfig {

    private static final String CONFIG_PATH = "feature";

    private final boolean enforceFeatureDescriptionModification;
    private final boolean enforcePresenceOfModeledFeatures;
    private final boolean enforceProperties;
    private final boolean enforceDesiredProperties;
    private final boolean enforceInboxMessagesInput;
    private final boolean enforceInboxMessagesOutput;
    private final boolean enforceOutboxMessages;
    private final boolean forbidFeatureDescriptionDeletion;
    private final boolean forbidNonModeledFeatures;
    private final boolean forbidNonModeledProperties;
    private final boolean forbidNonModeledDesiredProperties;
    private final boolean forbidNonModeledInboxMessages;
    private final boolean forbidNonModeledOutboxMessages;

    private DefaultFeatureValidationConfig(final ScopedConfig scopedConfig) {
        this.enforceFeatureDescriptionModification = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_FEATURE_DESCRIPTION_MODIFICATION.getConfigPath());
        this.enforcePresenceOfModeledFeatures = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_PRESENCE_OF_MODELED_FEATURES.getConfigPath());
        this.enforceProperties = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_PROPERTIES.getConfigPath());
        this.enforceDesiredProperties = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_DESIRED_PROPERTIES.getConfigPath());
        this.enforceInboxMessagesInput = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_INBOX_MESSAGES_INPUT.getConfigPath());
        this.enforceInboxMessagesOutput = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_INBOX_MESSAGES_OUTPUT.getConfigPath());
        this.enforceOutboxMessages = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.ENFORCE_OUTBOX_MESSAGES.getConfigPath());
        this.forbidFeatureDescriptionDeletion = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.FORBID_FEATURE_DESCRIPTION_DELETION.getConfigPath());
        this.forbidNonModeledFeatures = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.FORBID_NON_MODELED_FEATURES.getConfigPath());
        this.forbidNonModeledProperties = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.FORBID_NON_MODELED_PROPERTIES.getConfigPath());
        this.forbidNonModeledDesiredProperties = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.FORBID_NON_MODELED_DESIRED_PROPERTIES.getConfigPath());
        this.forbidNonModeledInboxMessages = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.FORBID_NON_MODELED_INBOX_MESSAGES.getConfigPath());
        this.forbidNonModeledOutboxMessages = scopedConfig.getBoolean(FeatureValidationConfig.ConfigValue.FORBID_NON_MODELED_OUTBOX_MESSAGES.getConfigPath());
    }

    /**
     * Returns an instance of {@code DefaultFeatureValidationConfig} based on the settings of the specified Config.
     *
     * @param config is supposed to provide the settings of the feature validation config at {@value #CONFIG_PATH}.
     * @return the instance.
     * @throws org.eclipse.ditto.internal.utils.config.DittoConfigError if {@code config} is invalid.
     */
    public static DefaultFeatureValidationConfig of(final Config config) {
        return new DefaultFeatureValidationConfig(DefaultScopedConfig.newInstance(config, CONFIG_PATH));
    }

    @Override
    public boolean isEnforceFeatureDescriptionModification() {
        return enforceFeatureDescriptionModification;
    }

    @Override
    public boolean isEnforcePresenceOfModeledFeatures() {
        return enforcePresenceOfModeledFeatures;
    }

    @Override
    public boolean isEnforceProperties() {
        return enforceProperties;
    }

    @Override
    public boolean isEnforceDesiredProperties() {
        return enforceDesiredProperties;
    }

    @Override
    public boolean isEnforceInboxMessagesInput() {
        return enforceInboxMessagesInput;
    }

    @Override
    public boolean isEnforceInboxMessagesOutput() {
        return enforceInboxMessagesOutput;
    }

    @Override
    public boolean isEnforceOutboxMessages() {
        return enforceOutboxMessages;
    }

    @Override
    public boolean isForbidFeatureDescriptionDeletion() {
        return forbidFeatureDescriptionDeletion;
    }

    @Override
    public boolean isForbidNonModeledFeatures() {
        return forbidNonModeledFeatures;
    }

    @Override
    public boolean isForbidNonModeledProperties() {
        return forbidNonModeledProperties;
    }

    @Override
    public boolean isForbidNonModeledDesiredProperties() {
        return forbidNonModeledDesiredProperties;
    }

    @Override
    public boolean isForbidNonModeledInboxMessages() {
        return forbidNonModeledInboxMessages;
    }

    @Override
    public boolean isForbidNonModeledOutboxMessages() {
        return forbidNonModeledOutboxMessages;
    }

    @Override
    public JsonObject toJson() {
        return JsonObject.newBuilder()
                .set(FeatureValidationConfig.JsonFields.ENFORCE_FEATURE_DESCRIPTION_MODIFICATION, enforceFeatureDescriptionModification)
                .set(FeatureValidationConfig.JsonFields.ENFORCE_PRESENCE_OF_MODELED_FEATURES, enforcePresenceOfModeledFeatures)
                .set(FeatureValidationConfig.JsonFields.ENFORCE_PROPERTIES, enforceProperties)
                .set(FeatureValidationConfig.JsonFields.ENFORCE_DESIRED_PROPERTIES, enforceDesiredProperties)
                .set(FeatureValidationConfig.JsonFields.ENFORCE_INBOX_MESSAGES_INPUT, enforceInboxMessagesInput)
                .set(FeatureValidationConfig.JsonFields.ENFORCE_INBOX_MESSAGES_OUTPUT, enforceInboxMessagesOutput)
                .set(FeatureValidationConfig.JsonFields.ENFORCE_OUTBOX_MESSAGES, enforceOutboxMessages)
                .set(FeatureValidationConfig.JsonFields.FORBID_FEATURE_DESCRIPTION_DELETION, forbidFeatureDescriptionDeletion)
                .set(FeatureValidationConfig.JsonFields.FORBID_NON_MODELED_FEATURES, forbidNonModeledFeatures)
                .set(FeatureValidationConfig.JsonFields.FORBID_NON_MODELED_PROPERTIES, forbidNonModeledProperties)
                .set(FeatureValidationConfig.JsonFields.FORBID_NON_MODELED_DESIRED_PROPERTIES, forbidNonModeledDesiredProperties)
                .set(FeatureValidationConfig.JsonFields.FORBID_NON_MODELED_INBOX_MESSAGES, forbidNonModeledInboxMessages)
                .set(FeatureValidationConfig.JsonFields.FORBID_NON_MODELED_OUTBOX_MESSAGES, forbidNonModeledOutboxMessages)
                .build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DefaultFeatureValidationConfig that = (DefaultFeatureValidationConfig) o;
        return enforceFeatureDescriptionModification == that.enforceFeatureDescriptionModification &&
                enforcePresenceOfModeledFeatures == that.enforcePresenceOfModeledFeatures &&
                enforceProperties == that.enforceProperties &&
                enforceDesiredProperties == that.enforceDesiredProperties &&
                enforceInboxMessagesInput == that.enforceInboxMessagesInput &&
                enforceInboxMessagesOutput == that.enforceInboxMessagesOutput &&
                enforceOutboxMessages == that.enforceOutboxMessages &&
                forbidFeatureDescriptionDeletion == that.forbidFeatureDescriptionDeletion &&
                forbidNonModeledFeatures == that.forbidNonModeledFeatures &&
                forbidNonModeledProperties == that.forbidNonModeledProperties &&
                forbidNonModeledDesiredProperties == that.forbidNonModeledDesiredProperties &&
                forbidNonModeledInboxMessages == that.forbidNonModeledInboxMessages &&
                forbidNonModeledOutboxMessages == that.forbidNonModeledOutboxMessages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enforceFeatureDescriptionModification, enforcePresenceOfModeledFeatures,
                enforceProperties, enforceDesiredProperties, enforceInboxMessagesInput,
                enforceInboxMessagesOutput, enforceOutboxMessages, forbidFeatureDescriptionDeletion,
                forbidNonModeledFeatures, forbidNonModeledProperties, forbidNonModeledDesiredProperties,
                forbidNonModeledInboxMessages, forbidNonModeledOutboxMessages);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "enforceFeatureDescriptionModification=" + enforceFeatureDescriptionModification +
                ", enforcePresenceOfModeledFeatures=" + enforcePresenceOfModeledFeatures +
                ", enforceProperties=" + enforceProperties +
                ", enforceDesiredProperties=" + enforceDesiredProperties +
                ", enforceInboxMessagesInput=" + enforceInboxMessagesInput +
                ", enforceInboxMessagesOutput=" + enforceInboxMessagesOutput +
                ", enforceOutboxMessages=" + enforceOutboxMessages +
                ", forbidFeatureDescriptionDeletion=" + forbidFeatureDescriptionDeletion +
                ", forbidNonModeledFeatures=" + forbidNonModeledFeatures +
                ", forbidNonModeledProperties=" + forbidNonModeledProperties +
                ", forbidNonModeledDesiredProperties=" + forbidNonModeledDesiredProperties +
                ", forbidNonModeledInboxMessages=" + forbidNonModeledInboxMessages +
                ", forbidNonModeledOutboxMessages=" + forbidNonModeledOutboxMessages +
                "]";
    }
} 