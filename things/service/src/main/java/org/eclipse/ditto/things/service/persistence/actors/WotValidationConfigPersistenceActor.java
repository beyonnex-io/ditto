package org.eclipse.ditto.things.service.persistence.actors;

import java.time.Instant;
import javax.annotation.concurrent.Immutable;
import javax.annotation.Nullable;

import org.apache.pekko.actor.Props;
import org.eclipse.ditto.base.model.exceptions.DittoRuntimeExceptionBuilder;
import org.eclipse.ditto.base.model.headers.DittoHeaders;
import org.eclipse.ditto.base.model.json.JsonSchemaVersion;
import org.eclipse.ditto.base.model.signals.commands.Command;
import org.eclipse.ditto.internal.utils.config.DefaultScopedConfig;
import org.eclipse.ditto.internal.utils.persistence.mongo.config.ActivityCheckConfig;
import org.eclipse.ditto.internal.utils.persistence.mongo.config.DefaultActivityCheckConfig;
import org.eclipse.ditto.internal.utils.persistence.mongo.config.DefaultSnapshotConfig;
import org.eclipse.ditto.internal.utils.persistence.mongo.config.SnapshotConfig;
import org.eclipse.ditto.internal.utils.persistence.mongo.streaming.MongoReadJournal;
import org.eclipse.ditto.internal.utils.persistentactors.AbstractPersistenceActor;
import org.eclipse.ditto.internal.utils.persistentactors.commands.CommandStrategy;
import org.eclipse.ditto.internal.utils.persistentactors.commands.DefaultContext;
import org.eclipse.ditto.internal.utils.persistentactors.events.EventStrategy;
import org.eclipse.ditto.things.model.devops.WotValidationConfigId;
import org.eclipse.ditto.things.model.devops.events.WotValidationConfigEvent;
import org.eclipse.ditto.things.model.devops.exceptions.WotValidationConfigException;
import org.eclipse.ditto.things.service.persistence.actors.strategies.commands.WotValidationConfigCommandStrategies;
import org.eclipse.ditto.things.service.persistence.actors.strategies.commands.WotValidationConfigDData;
import org.eclipse.ditto.things.service.persistence.mongo.MongoWotValidationConfigRepository;
import org.eclipse.ditto.wot.validation.config.TmValidationConfig;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoDiagnosticLoggingAdapter;
import org.eclipse.ditto.internal.utils.pekko.logging.DittoLoggerFactory;

/**
 * PersistenceActor which manages the state of a WoT validation configuration.
 */
@Immutable
public final class WotValidationConfigPersistenceActor 
        extends AbstractPersistenceActor<Command<?>, TmValidationConfig, WotValidationConfigId, String, WotValidationConfigEvent> {

    /**
     * The prefix of the persistenceId for WoT validation configs.
     */
    static final String PERSISTENCE_ID_PREFIX = "wot-validation-config:";

    /**
     * The ID of the journal plugin this persistence actor uses.
     */
    static final String JOURNAL_PLUGIN_ID = "pekko-contrib-mongodb-persistence-things-journal";

    /**
     * The ID of the snapshot plugin this persistence actor uses.
     */
    static final String SNAPSHOT_PLUGIN_ID = "pekko-contrib-mongodb-persistence-things-snapshots";

    private final String persistenceId;
    private final MongoReadJournal mongoReadJournal;
    private final WotValidationConfigDData ddata;
    private final MongoWotValidationConfigRepository mongoRepository;
    private final DefaultContext<String> context;
    private final SnapshotConfig snapshotConfig;
    private final ActivityCheckConfig activityCheckConfig;
    private final DittoDiagnosticLoggingAdapter log;

    private WotValidationConfigPersistenceActor(
            final String persistenceId,
            final MongoReadJournal mongoReadJournal,
            final WotValidationConfigDData ddata,
            final MongoWotValidationConfigRepository mongoRepository) {
        super(WotValidationConfigId.of(persistenceId), mongoReadJournal);
        this.persistenceId = persistenceId;
        this.mongoReadJournal = mongoReadJournal;
        this.ddata = ddata;
        this.mongoRepository = mongoRepository;
        this.log = DittoLoggerFactory.getDiagnosticLoggingAdapter(this);
        this.context = DefaultContext.getInstance(persistenceId, log, getContext().getSystem());
        final var config = DefaultScopedConfig.dittoScoped(getContext().getSystem().settings().config());
        this.snapshotConfig = DefaultSnapshotConfig.of(config);
        this.activityCheckConfig = DefaultActivityCheckConfig.of(config);
    }

    /**
     * Creates Props for this actor.
     *
     * @param persistenceId The persistence ID of the actor.
     * @param mongoReadJournal The MongoDB read journal.
     * @param ddata The distributed data.
     * @param mongoRepository The MongoDB repository.
     * @return The Props.
     */
    public static Props props(final String persistenceId,
            final MongoReadJournal mongoReadJournal,
            final WotValidationConfigDData ddata,
            final MongoWotValidationConfigRepository mongoRepository) {
        return Props.create(WotValidationConfigPersistenceActor.class, persistenceId, mongoReadJournal, ddata, mongoRepository);
    }

    @Override
    public String persistenceId() {
        return persistenceId;
    }

    @Override
    protected Class getEventClass() {
        return WotValidationConfigEvent.class;
    }


    @Override
    protected boolean entityExistsAsDeleted() {
        return false;
    }

    @Override
    protected DittoRuntimeExceptionBuilder<?> newNotAccessibleExceptionBuilder() {
        return WotValidationConfigException.newBuilder()
                .message("The WoT validation config with ID '" + "' is not accessible.");
    }

    @Override
    protected DittoRuntimeExceptionBuilder<?> newHistoryNotAccessibleExceptionBuilder(final long revision) {
        return WotValidationConfigException.newBuilder()
                .message("The history of the WoT validation config with ID '" +
                        "' and revision " + revision + " is not accessible.");
    }

    @Override
    protected DittoRuntimeExceptionBuilder<?> newHistoryNotAccessibleExceptionBuilder(final Instant timestamp) {
        return WotValidationConfigException.newBuilder()
                .message("The history of the WoT validation config with ID '" +
                        "' at timestamp " + timestamp + " is not accessible.");
    }

    @Override
    protected JsonSchemaVersion getEntitySchemaVersion(final TmValidationConfig entity) {
        return JsonSchemaVersion.LATEST;
    }

    @Override
    protected DefaultContext<String> getStrategyContext() {
        return context;
    }

    @Override
    protected CommandStrategy<Command<?>, TmValidationConfig, String, WotValidationConfigEvent> getCreatedStrategy() {
        return null;
    }

    @Override
    protected CommandStrategy<? extends Command<?>, TmValidationConfig, String, WotValidationConfigEvent> getDeletedStrategy() {
        return null;
    }

    @Override
    protected EventStrategy<WotValidationConfigEvent, TmValidationConfig> getEventStrategy() {
        return null;
    }

    @Override
    protected boolean isEntityAlwaysAlive() {
        return false;
    }



    @Override
    public String journalPluginId() {
        return JOURNAL_PLUGIN_ID;
    }

    @Override
    public String snapshotPluginId() {
        return SNAPSHOT_PLUGIN_ID;
    }

    @Override
    protected boolean shouldSendResponse(final DittoHeaders dittoHeaders) {
        return dittoHeaders.isResponseRequired();
    }

    @Override
    protected SnapshotConfig getSnapshotConfig() {
        return snapshotConfig;
    }

    @Override
    protected ActivityCheckConfig getActivityCheckConfig() {
        return activityCheckConfig;
    }

    @Override
    protected void publishEvent(@Nullable final TmValidationConfig previousEntity, final WotValidationConfigEvent event) {
        // No event publishing required for now
    }

} 