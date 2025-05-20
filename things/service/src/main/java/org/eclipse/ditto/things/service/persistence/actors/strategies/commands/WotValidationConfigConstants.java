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

import javax.annotation.concurrent.Immutable;

/**
 * Constants used in WoT (Web of Things) validation configuration.
 * <p>
 * This class defines all field names used in WoT validation config documents, including top-level fields,
 * thing validation config fields, and feature validation config fields. These constants are used throughout
 * the persistence and validation subsystems to ensure consistency in field naming.
 * </p>
 * <p>
 * This class is immutable and cannot be instantiated.
 * </p>
 */
@Immutable
public final class WotValidationConfigConstants {

    /**
     * Top-level field: whether validation is enabled.
     */
    public static final String FIELD_ENABLED = "enabled";
    /**
     * Top-level field: whether to log warnings instead of failing API calls.
     */
    public static final String FIELD_LOG_WARNING = "logWarning";
    /**
     * Top-level field: thing validation config section.
     */
    public static final String FIELD_THING = "thing";
    /**
     * Top-level field: feature validation config section.
     */
    public static final String FIELD_FEATURE = "feature";
    /**
     * Top-level field: dynamic config sections array.
     */
    public static final String FIELD_DYNAMIC_CONFIG = "dynamicConfig";

    // Thing validation config fields
    public static final String FIELD_ENFORCE_THING_DESCRIPTION_MODIFICATION = "enforceThingDescriptionModification";
    public static final String FIELD_ENFORCE_ATTRIBUTES = "enforceAttributes";
    public static final String FIELD_ENFORCE_INBOX_MESSAGES_INPUT = "enforceInboxMessagesInput";
    public static final String FIELD_ENFORCE_INBOX_MESSAGES_OUTPUT = "enforceInboxMessagesOutput";
    public static final String FIELD_ENFORCE_OUTBOX_MESSAGES = "enforceOutboxMessages";
    public static final String FIELD_FORBID_THING_DESCRIPTION_DELETION = "forbidThingDescriptionDeletion";
    public static final String FIELD_FORBID_NON_MODELED_ATTRIBUTES = "forbidNonModeledAttributes";
    public static final String FIELD_FORBID_NON_MODELED_INBOX_MESSAGES = "forbidNonModeledInboxMessages";
    public static final String FIELD_FORBID_NON_MODELED_OUTBOX_MESSAGES = "forbidNonModeledOutboxMessages";

    // Feature validation config fields
    public static final String FIELD_ENFORCE_FEATURE_DESCRIPTION_MODIFICATION = "enforceFeatureDescriptionModification";
    public static final String FIELD_ENFORCE_PRESENCE_OF_MODELED_FEATURES = "enforcePresenceOfModeledFeatures";
    public static final String FIELD_ENFORCE_PROPERTIES = "enforceProperties";
    public static final String FIELD_ENFORCE_DESIRED_PROPERTIES = "enforceDesiredProperties";
    public static final String FIELD_FORBID_FEATURE_DESCRIPTION_DELETION = "forbidFeatureDescriptionDeletion";
    public static final String FIELD_FORBID_NON_MODELED_FEATURES = "forbidNonModeledFeatures";
    public static final String FIELD_FORBID_NON_MODELED_PROPERTIES = "forbidNonModeledProperties";
    public static final String FIELD_FORBID_NON_MODELED_DESIRED_PROPERTIES = "forbidNonModeledDesiredProperties";

    /**
     * Private constructor to prevent instantiation.
     */
    private WotValidationConfigConstants() {
        throw new AssertionError();
    }
} 