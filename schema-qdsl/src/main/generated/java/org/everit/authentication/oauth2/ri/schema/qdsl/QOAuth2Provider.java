/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authentication.oauth2.ri.schema.qdsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QOAuth2Provider is a Querydsl query type for QOAuth2Provider
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QOAuth2Provider extends com.querydsl.sql.RelationalPathBase<QOAuth2Provider> {

    private static final long serialVersionUID = -905508439;

    public static final QOAuth2Provider oAuth2Provider = new QOAuth2Provider("oauth2_provider");

    public class PrimaryKeys {

        public final com.querydsl.sql.PrimaryKey<QOAuth2Provider> oauth2ProviderPk = createPrimaryKey(oauth2ProviderId);

    }

    public class ForeignKeys {

        public final com.querydsl.sql.ForeignKey<QOAuth2ResourceMapping> _oauth2ResourceMappingProviderFk = createInvForeignKey(oauth2ProviderId, "oauth2_provider_id");

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

    public QOAuth2Provider(PathMetadata metadata) {
        super(QOAuth2Provider.class, metadata, "org.everit.authentication.oauth2.ri", "oauth2_provider");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(oauth2ProviderId, ColumnMetadata.named("oauth2_provider_id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(providerName, ColumnMetadata.named("provider_name").withIndex(2).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

