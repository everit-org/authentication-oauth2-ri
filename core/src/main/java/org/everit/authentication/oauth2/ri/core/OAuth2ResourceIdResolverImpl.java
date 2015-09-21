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
package org.everit.authentication.oauth2.ri.core;

import java.util.Objects;
import java.util.Optional;

import org.everit.authentication.oauth2.ri.schema.qdsl.QOAuth2Provider;
import org.everit.authentication.oauth2.ri.schema.qdsl.QOAuth2ResourceMapping;
import org.everit.osgi.props.PropertyManager;
import org.everit.osgi.querydsl.support.QuerydslSupport;
import org.everit.osgi.resource.ResourceService;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.everit.osgi.resource.ri.schema.qdsl.QResource;
import org.everit.transaction.propagator.TransactionPropagator;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;

/**
 * OAuth2 specific {@link ResourceIdResolver} configured to a dedicated provider. This
 * implementation manages the {@link QOAuth2Provider} and {@link QOAuth2ResourceMapping} tables. It
 * lazily initializes:
 * <ul>
 * <li>the {@link QOAuth2Provider} by the configured provider name and</li>
 * <li>the {@link QOAuth2ResourceMapping} if the given provider and the unique user ID is not mapped
 * to a {@link QResource}.</li>
 * </ul>
 */
public class OAuth2ResourceIdResolverImpl implements ResourceIdResolver {

  private static final String PROP_LOCK_RESOURCE_ID =
      "org.everit.authentication.oauth2.ri.resource.mapping.lock.resource.id";

  private final PropertyManager propertyManager;

  private final long providerId;

  private final String providerName;

  private final QuerydslSupport querydslSupport;

  private final ResourceService resourceService;

  private final TransactionPropagator transactionPropagator;

  /**
   * Constructor.
   *
   * @param providerName
   *          the name of the provider used by this instance
   * @param propertyManager
   *          a {@link PropertyManager} used to store the resource ID used for locking the creation
   *          of the resource mappings
   * @param resourceService
   *          a {@link ResourceService} used to create a new resource ID if the resource mapping
   *          does not exist
   * @param transactionPropagator
   *          a {@link TransactionPropagator} instance
   * @param querydslSupport
   *          a {@link QuerydslSupport} instance
   *
   * @throws NullPointerException
   *           if one of the parameter is <code>null</code>.
   */
  public OAuth2ResourceIdResolverImpl(final String providerName,
      final PropertyManager propertyManager, final ResourceService resourceService,
      final TransactionPropagator transactionPropagator, final QuerydslSupport querydslSupport) {
    this.propertyManager = Objects.requireNonNull(propertyManager,
        "propertyManager cannot be null");
    this.querydslSupport = Objects.requireNonNull(querydslSupport,
        "querydslSupport cannot be null");
    this.resourceService = Objects.requireNonNull(resourceService,
        "resourceService cannot be null");
    this.transactionPropagator = Objects.requireNonNull(transactionPropagator,
        "transactionPropagator cannot be null");
    this.providerName = Objects.requireNonNull(providerName,
        "providerName cannot be null");

    providerId = transactionPropagator.required(() -> {

      String propResourceId = propertyManager.getProperty(PROP_LOCK_RESOURCE_ID);
      if (propResourceId == null) {
        long resourceId = resourceService.createResource();
        propertyManager.addProperty(PROP_LOCK_RESOURCE_ID, String.valueOf(resourceId));
      }

      return getOrCreateProviderId();
    });
  }

  private long getOrCreateProviderId() {

    Long oauth2ProviderId = selectProviderId();
    if (oauth2ProviderId != null) {
      return oauth2ProviderId;
    }

    return insertProvider();
  }

  private long getOrCreateResourceId(final String uniqueUserId) {

    Long resourceId = selectResourceId(uniqueUserId);
    if (resourceId != null) {
      return resourceId;
    }

    return transactionPropagator.required(() -> {

      lockMappingRegistration();

      Long checkResourceId = selectResourceId(uniqueUserId);
      if (checkResourceId != null) {
        return checkResourceId;
      }

      return insertResourceMapping(uniqueUserId);
    });
  }

  @Override
  public Optional<Long> getResourceId(final String uniqueUserId) {
    long resourceId = getOrCreateResourceId(uniqueUserId);
    return Optional.ofNullable(resourceId);
  }

  private long insertProvider() {
    return querydslSupport.execute((connection, configuration) -> {

      QOAuth2Provider qoAuth2Provider = QOAuth2Provider.oAuth2Provider;

      return new SQLInsertClause(connection, configuration, qoAuth2Provider)
          .set(qoAuth2Provider.providerName, providerName)
          .executeWithKey(qoAuth2Provider.oauth2ProviderId);
    });
  }

  private Long insertResourceMapping(final String uniqueUserId) {

    return querydslSupport.execute((connection, configuration) -> {

      long resourceId = resourceService.createResource();

      QOAuth2ResourceMapping qoAuth2ResourceMapping = QOAuth2ResourceMapping.oAuth2ResourceMapping;

      new SQLInsertClause(connection, configuration, qoAuth2ResourceMapping)
          .set(qoAuth2ResourceMapping.resourceId, resourceId)
          .set(qoAuth2ResourceMapping.oauth2ProviderId, providerId)
          .set(qoAuth2ResourceMapping.providerUniqueUserId, uniqueUserId)
          .execute();

      return resourceId;
    });

  }

  private void lockMappingRegistration() {

    long resourceId = Long.parseLong(propertyManager
        .getProperty(PROP_LOCK_RESOURCE_ID));

    querydslSupport.execute((connection, configuration) -> {

      QResource qResource = QResource.resource;

      return new SQLQuery(connection, configuration)
          .from(qResource)
          .where(qResource.resourceId.eq(resourceId))
          .forUpdate();
    });
  }

  private Long selectProviderId() {

    return querydslSupport.execute((connection, configuration) -> {

      QOAuth2Provider qoAuth2Provider = QOAuth2Provider.oAuth2Provider;

      return new SQLQuery(connection, configuration)
          .from(qoAuth2Provider)
          .where(qoAuth2Provider.providerName.eq(providerName))
          .uniqueResult(qoAuth2Provider.oauth2ProviderId);
    });
  }

  private Long selectResourceId(final String uniqueUserId) {

    return querydslSupport.execute((connection, configuration) -> {

      QOAuth2ResourceMapping qoAuth2ResourceMapping = QOAuth2ResourceMapping.oAuth2ResourceMapping;

      return new SQLQuery(connection, configuration)
          .from(qoAuth2ResourceMapping)
          .where(qoAuth2ResourceMapping.oauth2ProviderId.eq(providerId)
              .and(qoAuth2ResourceMapping.providerUniqueUserId.eq(uniqueUserId)))
          .uniqueResult(qoAuth2ResourceMapping.resourceId);
    });
  }
}
