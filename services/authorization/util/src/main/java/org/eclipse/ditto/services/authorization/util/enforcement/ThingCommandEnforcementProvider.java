/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.services.authorization.util.enforcement;

import org.eclipse.ditto.signals.commands.things.ThingCommand;

/**
 * Provides {@link Enforcement} for commands of type {@link ThingCommand}.
 */
public class ThingCommandEnforcementProvider implements EnforcementProvider {

    @Override
    public Class getCommandClass() {
        return ThingCommand.class;
    }

    @Override
    public Enforcement createEnforcement(final Enforcement.Context context) {
        return new ThingCommandEnforcement(context);
    }
}
