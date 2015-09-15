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

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;




/**
 * QOAuth2ResourceMapping is a Querydsl query type for QOAuth2ResourceMapping
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QOAuth2ResourceMapping extends com.mysema.query.sql.RelationalPathBase<QOAuth2ResourceMapping> {

    private static final long serialVersionUID = -354370008;

    public static final QOAuth2ResourceMapping oAuth2ResourceMapping = new QOAuth2ResourceMapping("oauth2_resource_mapping");

    public class PrimaryKeys {

        public final com.mysema.query.sql.PrimaryKey<QOAuth2ResourceMapping> oauth2ResourceMappingPk = createPrimaryKey(oauth2ResourceMappingId);

    }

    public class ForeignKeys {

        public final com.mysema.query.sql.ForeignKey<org.everit.osgi.resource.ri.schema.qdsl.QResource> oauth2ResourceMappingResourceFk = createForeignKey(resourceId, "resource_id");

        public final com.mysema.query.sql.ForeignKey<QOAuth2Provider> oauth2ResourceMappingProviderFk = createForeignKey(oauth2ProviderId, "oauth2_provider_id");

    }

    public final NumberPath<Long> oauth2ProviderId = createNumber("oauth2ProviderId", Long.class);

    public final NumberPath<Long> oauth2ResourceMappingId = createNumber("oauth2ResourceMappingId", Long.class);

    public final StringPath providerUniqueUserId = createString("providerUniqueUserId");

    public final NumberPath<Long> resourceId = createNumber("resourceId", Long.class);

    public final PrimaryKeys pk = new PrimaryKeys();

    public final ForeignKeys fk = new ForeignKeys();

    public QOAuth2ResourceMapping(String variable) {
        super(QOAuth2ResourceMapping.class, forVariable(variable), "org.everit.authentication.oauth2.ri", "oauth2_resource_mapping");
        addMetadata();
    }

    public QOAuth2ResourceMapping(String variable, String schema, String table) {
        super(QOAuth2ResourceMapping.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QOAuth2ResourceMapping(Path<? extends QOAuth2ResourceMapping> path) {
        super(path.getType(), path.getMetadata(), "org.everit.authentication.oauth2.ri", "oauth2_resource_mapping");
        addMetadata();
    }

    public QOAuth2ResourceMapping(PathMetadata<?> metadata) {
        super(QOAuth2ResourceMapping.class, metadata, "org.everit.authentication.oauth2.ri", "oauth2_resource_mapping");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(oauth2ProviderId, ColumnMetadata.named("oauth2_provider_id").ofType(-5).withSize(19).notNull());
        addMetadata(oauth2ResourceMappingId, ColumnMetadata.named("oauth2_resource_mapping_id").ofType(-5).withSize(19).notNull());
        addMetadata(providerUniqueUserId, ColumnMetadata.named("provider_unique_user_id").ofType(12).withSize(256).notNull());
        addMetadata(resourceId, ColumnMetadata.named("resource_id").ofType(-5).withSize(19).notNull());
    }

}

