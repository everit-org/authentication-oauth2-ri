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
package org.everit.authentication.oauth2.ri.internal;

import java.util.Objects;
import java.util.Optional;

import org.everit.authentication.oauth2.ri.schema.qdsl.QOAuth2UserMapping;
import org.everit.osgi.props.PropertyManager;
import org.everit.osgi.querydsl.support.QuerydslSupport;
import org.everit.osgi.resource.ResourceService;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.everit.osgi.resource.ri.schema.qdsl.QResource;
import org.everit.osgi.transaction.helper.api.TransactionHelper;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;

/**
 * Default OAuth2 specific {@link ResourceIdResolver}.
 */
public class OAuth2ResourceIdResolverImpl implements ResourceIdResolver {

  private static final String PROP_OAUTH2_PROVIDER_REGISTRATION_RESOURCE_ID =
      "oauth2.provider.registration.resource.id";

  private PropertyManager propertyManager;

  private QuerydslSupport querydslSupport;

  private ResourceService resourceService;

  private TransactionHelper transactionHelper;

  /**
   * Consturctor.
   *
   * @param propertyManager
   *          the {@link PropertyManager} instance. Cannot be <code>null</code>!
   * @param querydslSupport
   *          the {@link QuerydslSupport} instance. Cannot be <code>null</code>!
   * @param resourceService
   *          the {@link ResourceService} instance. Cannot be <code>null</code>!
   * @param transactionHelper
   *          the {@link TransactionHelper} instance. Cannot be <code>null</code>!
   *
   * @throws NullPointerException
   *           if one of the parameters is <code>null</code>.
   */
  public OAuth2ResourceIdResolverImpl(final PropertyManager propertyManager,
      final QuerydslSupport querydslSupport, final ResourceService resourceService,
      final TransactionHelper transactionHelper) {
    this.propertyManager = Objects.requireNonNull(propertyManager,
        "The propertyManager cannot be null.");
    this.querydslSupport = Objects.requireNonNull(querydslSupport,
        "The querydslSupport cannot be null.");
    this.resourceService = Objects.requireNonNull(resourceService,
        "The resourceService cannot be null.");
    this.transactionHelper = Objects.requireNonNull(transactionHelper,
        "The transactionHelper cannot be null.");

    init();
  }

  private Long createResourceId(final String providerName, final String uniqueUserId) {
    Long createdResourceId = resourceService.createResource();

    querydslSupport.execute((connection, configuration) -> {
      QOAuth2UserMapping qoauth2UserMapping = QOAuth2UserMapping.oAuth2UserMapping;
      return new SQLInsertClause(connection, configuration, qoauth2UserMapping)
          .set(qoauth2UserMapping.resourceId, createdResourceId)
          .set(qoauth2UserMapping.providerName, providerName)
          .set(qoauth2UserMapping.providerUniqueUserId, uniqueUserId)
          .execute();
    });

    return createdResourceId;
  }

  private long getOrCreateResourceId(final String providerName, final String uniqueUserId) {
    Long resourceId = selectResourceId(providerName, uniqueUserId);

    if (resourceId == null) {
      resourceId = transactionHelper.required(() -> {
        lockRegistration();
        Long checkResourceId = selectResourceId(providerName, uniqueUserId);
        if (checkResourceId != null) {
          return checkResourceId;
        }

        return createResourceId(providerName, uniqueUserId);
      });
    }
    return resourceId;
  }

  @Override
  public Optional<Long> getResourceId(final String uniqueIdentifier) {
    String[] splitUniqueIdentifier = uniqueIdentifier.split(";");
    long resourceId =
        getOrCreateResourceId(splitUniqueIdentifier[0], splitUniqueIdentifier[1]);
    return Optional.ofNullable(resourceId);
  }

  private void init() {
    String propResourceId = propertyManager
        .getProperty(PROP_OAUTH2_PROVIDER_REGISTRATION_RESOURCE_ID);
    if (propResourceId == null) {
      long resourceId = resourceService.createResource();
      propertyManager.addProperty(PROP_OAUTH2_PROVIDER_REGISTRATION_RESOURCE_ID,
          String.valueOf(resourceId));
    }
  }

  private void lockRegistration() {
    String propResourceId = propertyManager
        .getProperty(PROP_OAUTH2_PROVIDER_REGISTRATION_RESOURCE_ID);
    querydslSupport.execute((connection, configuration) -> {
      QResource qResource = QResource.resource;
      return new SQLQuery(connection, configuration)
          .from(qResource)
          .where(qResource.resourceId.eq(Long.valueOf(propResourceId)))
          .forUpdate();

    });
  }

  private Long selectResourceId(final String providerName, final String uniqueUserId) {
    return querydslSupport.execute((connection, configuration) -> {
      QOAuth2UserMapping qoauth2UserMapping = QOAuth2UserMapping.oAuth2UserMapping;
      return new SQLQuery(connection, configuration)
          .from(qoauth2UserMapping)
          .where(qoauth2UserMapping.providerName.eq(providerName)
              .and(qoauth2UserMapping.providerUniqueUserId.eq(uniqueUserId)))
          .uniqueResult(qoauth2UserMapping.resourceId);
    });
  }
}
