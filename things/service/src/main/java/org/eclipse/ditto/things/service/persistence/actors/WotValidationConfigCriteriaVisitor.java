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
package org.eclipse.ditto.things.service.persistence.actors;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.rql.query.criteria.Criteria;
import org.eclipse.ditto.rql.query.criteria.visitors.CriteriaVisitor;
import org.eclipse.ditto.rql.query.expression.ExistsFieldExpression;
import org.eclipse.ditto.rql.query.expression.FilterFieldExpression;

/**
 * Visitor for evaluating whether a WoT validation config event matches a given criteria.
 */
final class WotValidationConfigCriteriaVisitor implements CriteriaVisitor<Boolean> {

    private final JsonObject eventJson;

    WotValidationConfigCriteriaVisitor(final JsonObject eventJson) {
        this.eventJson = eventJson;
    }

    @Override
    public Boolean visitAny() {
        return true;
    }

    @Override
    public Boolean visitExists(final ExistsFieldExpression fieldExpression) {
        return eventJson.getValue(fieldExpression.getFieldName()).isPresent();
    }

    @Override
    public Boolean visitField(final FilterFieldExpression fieldExpression, final Predicate<JsonValue> predicate) {
        return eventJson.getValue(fieldExpression.getFieldName())
                .map(predicate::test)
                .orElse(false);
    }

    @Override
    public Boolean visitAnd(final List<Boolean> conjuncts) {
        return conjuncts.stream().allMatch(Boolean::booleanValue);
    }

    @Override
    public Boolean visitOr(final List<Boolean> disjuncts) {
        return disjuncts.stream().anyMatch(Boolean::booleanValue);
    }

    @Override
    public Boolean visitNor(final List<Boolean> negativeDisjuncts) {
        return !visitOr(negativeDisjuncts);
    }
} 