package org.everit.authentication.oauth2.ri.schema.qdsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;




/**
 * QOAuth2Provider is a Querydsl query type for QOAuth2Provider
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QOAuth2Provider extends com.mysema.query.sql.RelationalPathBase<QOAuth2Provider> {

    private static final long serialVersionUID = -905508439;

    public static final QOAuth2Provider oAuth2Provider = new QOAuth2Provider("oauth2_provider");

    public class PrimaryKeys {

        public final com.mysema.query.sql.PrimaryKey<QOAuth2Provider> oauth2ProviderPk = createPrimaryKey(oauth2ProviderId);

    }

    public class ForeignKeys {

        public final com.mysema.query.sql.ForeignKey<QOAuth2ResourceMapping> _oauth2ResourceMappingProviderFk = createInvForeignKey(oauth2ProviderId, "oauth2_provider_id");

    }

    public final NumberPath<Long> oauth2ProviderId = createNumber("oauth2ProviderId", Long.class);

    public final StringPath providerName = createString("providerName");

    public final PrimaryKeys pk = new PrimaryKeys();

    public final ForeignKeys fk = new ForeignKeys();

    public QOAuth2Provider(String variable) {
        super(QOAuth2Provider.class, forVariable(variable), "org.everit.authentication.oauth2.ri", "oauth2_provider");
        addMetadata();
    }

    public QOAuth2Provider(String variable, String schema, String table) {
        super(QOAuth2Provider.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QOAuth2Provider(Path<? extends QOAuth2Provider> path) {
        super(path.getType(), path.getMetadata(), "org.everit.authentication.oauth2.ri", "oauth2_provider");
        addMetadata();
    }

    public QOAuth2Provider(PathMetadata<?> metadata) {
        super(QOAuth2Provider.class, metadata, "org.everit.authentication.oauth2.ri", "oauth2_provider");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(oauth2ProviderId, ColumnMetadata.named("oauth2_provider_id").ofType(-5).withSize(19).notNull());
        addMetadata(providerName, ColumnMetadata.named("provider_name").ofType(12).withSize(256).notNull());
    }

}

