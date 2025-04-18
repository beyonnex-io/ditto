package org.eclipse.ditto.things.model.devops;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.base.model.entity.id.EntityId;
import org.eclipse.ditto.base.model.entity.type.EntityType;
import org.eclipse.ditto.base.model.entity.id.NamespacedEntityId;

/**
 * Represents a WoT validation config ID.
 */
@Immutable
public final class WotValidationConfigId implements EntityId {

    private static final EntityType ENTITY_TYPE = EntityType.of("wot-validation-config");
    private final NamespacedEntityId id;

    private WotValidationConfigId(final NamespacedEntityId id) {
        this.id = id;
    }

    /**
     * Creates a new WotValidationConfigId from the given string.
     *
     * @param configId the string representation of the config ID.
     * @return the WotValidationConfigId.
     */
    public static WotValidationConfigId of(final String configId) {
        return new WotValidationConfigId(NamespacedEntityId.of(ENTITY_TYPE, configId));
    }

    @Override
    public EntityType getEntityType() {
        return ENTITY_TYPE;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WotValidationConfigId that = (WotValidationConfigId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 