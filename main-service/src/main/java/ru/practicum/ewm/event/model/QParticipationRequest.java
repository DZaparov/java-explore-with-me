package ru.practicum.ewm.event.model;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QParticipationRequest is a Querydsl query type for ParticipationRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParticipationRequest extends EntityPathBase<ParticipationRequest> {

    private static final long serialVersionUID = 556701233L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParticipationRequest participationRequest = new QParticipationRequest("participationRequest");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ru.practicum.ewm.user.model.QUser requester;

    public final EnumPath<ParticipationRequestStatus> status = createEnum("status", ParticipationRequestStatus.class);

    public QParticipationRequest(String variable) {
        this(ParticipationRequest.class, forVariable(variable), INITS);
    }

    public QParticipationRequest(Path<? extends ParticipationRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParticipationRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParticipationRequest(PathMetadata metadata, PathInits inits) {
        this(ParticipationRequest.class, metadata, inits);
    }

    public QParticipationRequest(Class<? extends ParticipationRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event"), inits.get("event")) : null;
        this.requester = inits.isInitialized("requester") ? new ru.practicum.ewm.user.model.QUser(forProperty("requester")) : null;
    }

}

