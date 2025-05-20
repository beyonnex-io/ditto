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
package org.eclipse.ditto.things.model.devops.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.things.model.devops.ImmutableDynamicValidationConfig;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.commands.ModifyDynamicConfigSection;
import org.eclipse.ditto.things.model.devops.commands.WotValidationConfigCommand;
import org.junit.jupiter.api.Test;

class ModifyDynamicConfigSectionTest {

    @Test
    void testOfAndGetters() {
        WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        String scopeId = "scope1";
        DittoHeaders headers = DittoHeaders.empty();
        ImmutableDynamicValidationConfig dynamicConfig = ImmutableDynamicValidationConfig.of(scopeId, null, null);
        ModifyDynamicConfigSection command = ModifyDynamicConfigSection.of(configId, scopeId, dynamicConfig, headers);
        assertEquals(configId, command.getEntityId());
        assertEquals(scopeId, command.getScopeId());
        assertEquals(dynamicConfig, command.getDynamicConfigSection());
        assertEquals(headers, command.getDittoHeaders());
    }

    @Test
    void testFromJson() {
        WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        String scopeId = "scope1";
        DittoHeaders headers = DittoHeaders.empty();
        ImmutableDynamicValidationConfig dynamicConfig = ImmutableDynamicValidationConfig.of(scopeId, null, null);
        JsonObject json = JsonFactory.newObjectBuilder()
                .set(WotValidationConfigCommand.JsonFields.CONFIG_ID, configId.toString())
                .set("scopeId", scopeId)
                .set("validationContext", JsonFactory.newObjectBuilder().build())
                .set("configOverrides", JsonFactory.newObjectBuilder().build())
                .build();
        ModifyDynamicConfigSection parsed = ModifyDynamicConfigSection.fromJson(json, headers);
        assertEquals(configId, parsed.getEntityId());
        assertEquals(scopeId, parsed.getScopeId());
        assertNotNull(parsed.getDynamicConfigSection());
    }

    @Test
    void testEqualsAndHashCode() {
        WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        String scopeId = "scope1";
        DittoHeaders headers = DittoHeaders.empty();
        ImmutableDynamicValidationConfig dynamicConfig = ImmutableDynamicValidationConfig.of(scopeId, null, null);
        ModifyDynamicConfigSection c1 = ModifyDynamicConfigSection.of(configId, scopeId, dynamicConfig, headers);
        ModifyDynamicConfigSection c2 = ModifyDynamicConfigSection.of(configId, scopeId, dynamicConfig, headers);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testNullArguments() {
        WotValidationConfigId configId = WotValidationConfigId.of("ns:test-id");
        String scopeId = "scope1";
        DittoHeaders headers = DittoHeaders.empty();
        ImmutableDynamicValidationConfig dynamicConfig = ImmutableDynamicValidationConfig.of(scopeId, null, null);
        assertThrows(NullPointerException.class, () -> ModifyDynamicConfigSection.of(null, scopeId, dynamicConfig, headers));
        assertThrows(NullPointerException.class, () -> ModifyDynamicConfigSection.of(configId, null, dynamicConfig, headers));
        assertThrows(NullPointerException.class, () -> ModifyDynamicConfigSection.of(configId, scopeId, null, headers));
        assertThrows(NullPointerException.class, () -> ModifyDynamicConfigSection.of(configId, scopeId, dynamicConfig, null));
    }
} 