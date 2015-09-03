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
 * QOAuth2UserMapping is a Querydsl query type for QOAuth2UserMapping
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QOAuth2UserMapping extends com.mysema.query.sql.RelationalPathBase<QOAuth2UserMapping> {

    private static final long serialVersionUID = -841237557;

    public static final QOAuth2UserMapping oAuth2UserMapping = new QOAuth2UserMapping("oauth2_user_mapping");

    public class PrimaryKeys {

        public final com.mysema.query.sql.PrimaryKey<QOAuth2UserMapping> oauth2UserMappingPk = createPrimaryKey(oauth2UserMappingId);

    }

    public class ForeignKeys {

        public final com.mysema.query.sql.ForeignKey<org.everit.osgi.resource.ri.schema.qdsl.QResource> oauth2UserMappingResourceFk = createForeignKey(resourceId, "resource_id");

    }

    public final NumberPath<Long> oauth2UserMappingId = createNumber("oauth2UserMappingId", Long.class);

    public final StringPath providerName = createString("providerName");

    public final StringPath providerUniqueUserId = createString("providerUniqueUserId");

    public final NumberPath<Long> resourceId = createNumber("resourceId", Long.class);

    public final PrimaryKeys pk = new PrimaryKeys();

    public final ForeignKeys fk = new ForeignKeys();

    public QOAuth2UserMapping(String variable) {
        super(QOAuth2UserMapping.class, forVariable(variable), "org.everit.authentication.oauth2.ri", "oauth2_user_mapping");
        addMetadata();
    }

    public QOAuth2UserMapping(String variable, String schema, String table) {
        super(QOAuth2UserMapping.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QOAuth2UserMapping(Path<? extends QOAuth2UserMapping> path) {
        super(path.getType(), path.getMetadata(), "org.everit.authentication.oauth2.ri", "oauth2_user_mapping");
        addMetadata();
    }

    public QOAuth2UserMapping(PathMetadata<?> metadata) {
        super(QOAuth2UserMapping.class, metadata, "org.everit.authentication.oauth2.ri", "oauth2_user_mapping");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(oauth2UserMappingId, ColumnMetadata.named("oauth2_user_mapping_id").ofType(-5).withSize(19).notNull());
        addMetadata(providerName, ColumnMetadata.named("provider_name").ofType(12).withSize(256).notNull());
        addMetadata(providerUniqueUserId, ColumnMetadata.named("provider_unique_user_id").ofType(12).withSize(256).notNull());
        addMetadata(resourceId, ColumnMetadata.named("resource_id").ofType(-5).withSize(19).notNull());
    }

}

