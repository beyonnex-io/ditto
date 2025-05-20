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
package org.eclipse.ditto.things.service.persistence.actors;

import java.util.List;
import java.util.Objects;

import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.rql.query.criteria.Criteria;
import org.eclipse.ditto.rql.query.criteria.Predicate;
import org.eclipse.ditto.rql.query.criteria.visitors.CriteriaVisitor;
import org.eclipse.ditto.rql.query.expression.ExistsFieldExpression;
import org.eclipse.ditto.rql.query.expression.FilterFieldExpression;
import org.eclipse.ditto.rql.query.expression.visitors.FieldExpressionVisitor;
import org.eclipse.ditto.rql.query.expression.visitors.FilterFieldExpressionVisitor;

/**
 * Visitor for evaluating criteria against WoT validation config events.
 */
public final class WotValidationConfigCriteriaVisitor implements CriteriaVisitor<Boolean> {

    private final JsonObject eventJson;

    public WotValidationConfigCriteriaVisitor(final JsonObject eventJson) {
        this.eventJson = eventJson;
    }

    @Override
    public Boolean visitAnd(final List<Boolean> conjuncts) {
        return conjuncts.stream().allMatch(Boolean::booleanValue);
    }

    @Override
    public Boolean visitAny() {
        return true;
    }

    @Override
    public Boolean visitExists(final ExistsFieldExpression fieldExpression) {
        final String fieldName = fieldExpression.accept(new FieldExpressionVisitor<String>() {
            @Override
            public String visitAttribute(final String key) {
                return key;
            }

            @Override
            public String visitFeatureIdProperty(final String featureId, final String property) {
                return featureId + "/properties/" + property;
            }

            @Override
            public String visitFeatureIdDesiredProperty(final CharSequence featureId, final CharSequence desiredProperty) {
                return featureId + "/desiredProperties/" + desiredProperty;
            }

            @Override
            public String visitSimple(final String fieldName) {
                return fieldName;
            }

            @Override
            public String visitMetadata(final String metadata) {
                return metadata;
            }

            @Override
            public String visitFeatureDefinition(final String featureId) {
                return featureId + "/definition";
            }

            @Override
            public String visitFeatureDesiredProperties(final CharSequence featureId) {
                return featureId + "/desiredProperties";
            }

            @Override
            public String visitFeatureProperties(final CharSequence featureId) {
                return featureId + "/properties";
            }

            @Override
            public String visitFeature(final String featureId) {
                return featureId;
            }
        });
        return eventJson.contains(fieldName);
    }

    @Override
    public Boolean visitField(final FilterFieldExpression fieldExpression, final Predicate predicate) {
        final String fieldName = fieldExpression.acceptFilterVisitor(new FilterFieldExpressionVisitor<String>() {
            @Override
            public String visitAttribute(final String key) {
                return key;
            }

            @Override
            public String visitFeatureIdProperty(final String featureId, final String property) {
                return featureId + "/properties/" + property;
            }

            @Override
            public String visitFeatureIdDesiredProperty(final CharSequence featureId, final CharSequence property) {
                return featureId + "/desiredProperties/" + property;
            }

            @Override
            public String visitSimple(final String fieldName) {
                return fieldName;
            }

            @Override
            public String visitMetadata(final String metadata) {
                return metadata;
            }

            @Override
            public String visitFeatureDefinition(final String featureId) {
                return featureId + "/definition";
            }
        });
        return eventJson.getValue(fieldName)
                .map(value -> predicate.accept(new PredicateVisitor(value)))
                .orElse(false);
    }

    @Override
    public Boolean visitNor(final List<Boolean> negativeDisjoints) {
        return negativeDisjoints.stream().noneMatch(Boolean::booleanValue);
    }

    @Override
    public Boolean visitOr(final List<Boolean> disjoints) {
        return disjoints.stream().anyMatch(Boolean::booleanValue);
    }

    private static final class PredicateVisitor implements org.eclipse.ditto.rql.query.criteria.visitors.PredicateVisitor<Boolean> {
        private final Object value;

        PredicateVisitor(final Object value) {
            this.value = value;
        }

        @Override
        public Boolean visitEq(final Object value) {
            return Objects.equals(this.value, value);
        }

        @Override
        public Boolean visitGe(final Object value) {
            return compare(value) >= 0;
        }

        @Override
        public Boolean visitGt(final Object value) {
            return compare(value) > 0;
        }

        @Override
        public Boolean visitIn(final List<?> values) {
            return values.contains(this.value);
        }

        @Override
        public Boolean visitLe(final Object value) {
            return compare(value) <= 0;
        }

        @Override
        public Boolean visitLike(final String value) {
            if (this.value instanceof String strValue) {
                return strValue.matches(value.replace("%", ".*"));
            }
            return false;
        }

        @Override
        public Boolean visitILike(final String value) {
            if (this.value instanceof String strValue) {
                return strValue.toLowerCase().matches(value.toLowerCase().replace("%", ".*"));
            }
            return false;
        }

        @Override
        public Boolean visitLt(final Object value) {
            return compare(value) < 0;
        }

        @Override
        public Boolean visitNe(final Object value) {
            return !Objects.equals(this.value, value);
        }

        private int compare(final Object other) {
            if (value instanceof Comparable && other instanceof Comparable) {
                @SuppressWarnings("unchecked")
                final Comparable<Object> comparable = (Comparable<Object>) value;
                return comparable.compareTo(other);
            }
            return 0;
        }
    }
} 