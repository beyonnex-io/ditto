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

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.internal.utils.config.KnownConfigValue;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;

/**
 * Configuration for Thing-specific WoT validation.
 *
 * @since 3.6.0
 */
@Immutable
public interface ThingValidationConfig {

    /**
     * Returns whether to enforce/validate a thing whenever its description is modified.
     *
     * @return whether to enforce/validate a thing whenever its description is modified.
     */
    boolean isEnforceThingDescriptionModification();

    /**
     * Returns whether to enforce/validate attributes of a thing following the defined WoT properties.
     *
     * @return whether to enforce/validate attributes of a thing following the defined WoT properties.
     */
    boolean isEnforceAttributes();

    /**
     * Returns whether to enforce/validate inbox messages to a thing following the defined WoT action "input".
     *
     * @return whether to enforce/validate inbox messages to a thing following the defined WoT action "input".
     */
    boolean isEnforceInboxMessagesInput();

    /**
     * Returns whether to enforce/validate inbox message responses to a thing following the defined WoT action "output".
     *
     * @return whether to enforce/validate inbox message responses to a thing following the defined WoT action "output".
     */
    boolean isEnforceInboxMessagesOutput();

    /**
     * Returns whether to enforce/validate outbox messages from a thing following the defined WoT event "data".
     *
     * @return whether to enforce/validate outbox messages from a thing following the defined WoT event "data".
     */
    boolean isEnforceOutboxMessages();

    /**
     * Returns whether to forbid deletion of a thing's description.
     *
     * @return whether to forbid deletion of a thing's description.
     */
    boolean isForbidThingDescriptionDeletion();

    /**
     * Returns whether to forbid persisting attributes which are not defined as properties in the WoT model.
     *
     * @return whether to forbid persisting attributes which are not defined as properties in the WoT model.
     */
    boolean isForbidNonModeledAttributes();

    /**
     * Returns whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     *
     * @return whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
     */
    boolean isForbidNonModeledInboxMessages();

    /**
     * Returns whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     *
     * @return whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
     */
    boolean isForbidNonModeledOutboxMessages();

    /**
     * Returns a JSON representation of this configuration.
     *
     * @return the JSON representation.
     */
    JsonObject toJson();

    /**
     * An enumeration of the known config path expressions and their associated default values for
     * {@code ThingValidationConfig}.
     */
    enum ConfigValue implements KnownConfigValue {

        ENFORCE_THING_DESCRIPTION_MODIFICATION("enforce.thing-description-modification", true),

        ENFORCE_ATTRIBUTES("enforce.attributes", true),

        ENFORCE_INBOX_MESSAGES_INPUT("enforce.inbox-messages-input", true),

        ENFORCE_INBOX_MESSAGES_OUTPUT("enforce.inbox-messages-output", true),

        ENFORCE_OUTBOX_MESSAGES("enforce.outbox-messages", true),

        FORBID_THING_DESCRIPTION_DELETION("forbid.thing-description-deletion", true),

        FORBID_NON_MODELED_ATTRIBUTES("forbid.non-modeled-attributes", true),

        FORBID_NON_MODELED_INBOX_MESSAGES("forbid.non-modeled-inbox-messages", true),

        FORBID_NON_MODELED_OUTBOX_MESSAGES("forbid.non-modeled-outbox-messages", true);


        private final String path;
        private final Object defaultValue;

        ConfigValue(final String thePath, final Object theDefaultValue) {
            path = thePath;
            defaultValue = theDefaultValue;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String getConfigPath() {
            return path;
        }
    }

    /**
     * An enumeration of the known JSON field definitions for {@code ThingValidationConfig}.
     */
    final class JsonFields {

        /**
         * JSON field containing whether to enforce/validate a thing whenever its description is modified.
         */
        public static final JsonFieldDefinition<Boolean> ENFORCE_THING_DESCRIPTION_MODIFICATION =
                JsonFieldDefinition.ofBoolean("enforceThingDescriptionModification");

        /**
         * JSON field containing whether to enforce/validate attributes of a thing following the defined WoT properties.
         */
        public static final JsonFieldDefinition<Boolean> ENFORCE_ATTRIBUTES =
                JsonFieldDefinition.ofBoolean("enforceAttributes");

        /**
         * JSON field containing whether to enforce/validate inbox messages to a thing following the defined WoT action "input".
         */
        public static final JsonFieldDefinition<Boolean> ENFORCE_INBOX_MESSAGES_INPUT =
                JsonFieldDefinition.ofBoolean("enforceInboxMessagesInput");

        /**
         * JSON field containing whether to enforce/validate inbox message responses to a thing following the defined WoT action "output".
         */
        public static final JsonFieldDefinition<Boolean> ENFORCE_INBOX_MESSAGES_OUTPUT =
                JsonFieldDefinition.ofBoolean("enforceInboxMessagesOutput");

        /**
         * JSON field containing whether to enforce/validate outbox messages from a thing following the defined WoT event "data".
         */
        public static final JsonFieldDefinition<Boolean> ENFORCE_OUTBOX_MESSAGES =
                JsonFieldDefinition.ofBoolean("enforceOutboxMessages");

        /**
         * JSON field containing whether to forbid deletion of a thing's description.
         */
        public static final JsonFieldDefinition<Boolean> FORBID_THING_DESCRIPTION_DELETION =
                JsonFieldDefinition.ofBoolean("forbidThingDescriptionDeletion");

        /**
         * JSON field containing whether to forbid persisting attributes which are not defined as properties in the WoT model.
         */
        public static final JsonFieldDefinition<Boolean> FORBID_NON_MODELED_ATTRIBUTES =
                JsonFieldDefinition.ofBoolean("forbidNonModeledAttributes");

        /**
         * JSON field containing whether to forbid dispatching of inbox messages which are not defined as actions in the WoT model.
         */
        public static final JsonFieldDefinition<Boolean> FORBID_NON_MODELED_INBOX_MESSAGES =
                JsonFieldDefinition.ofBoolean("forbidNonModeledInboxMessages");

        /**
         * JSON field containing whether to forbid dispatching of outbox messages which are not defined as events in the WoT model.
         */
        public static final JsonFieldDefinition<Boolean> FORBID_NON_MODELED_OUTBOX_MESSAGES =
                JsonFieldDefinition.ofBoolean("forbidNonModeledOutboxMessages");

        private JsonFields() {
            throw new AssertionError();
        }
    }
}
