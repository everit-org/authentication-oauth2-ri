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

import org.everit.authentication.oauth2.OAuth2Configuration;

/**
 * Implementation of {@link OAuth2Configuration} and responsible to collect OAuth2 configuration.
 */
public class OAuth2ConfigurationImpl implements OAuth2Configuration {

  private String authorizationEndpoint;

  private String clientId;

  private String clientSecret;

  private String providerName;

  private String redirectEndpoint;

  private String scope;

  private String tokenEndpoint;

  /**
   * Constructor.
   *
   * @param authorizationEndpoint
   *          the authorization endpoint of OAuth2 server. Cannot be <code>null</code>!
   * @param clientId
   *          the client ID of the registered client (application) in OAuth2 server. Cannot be
   *          <code>null</code>!
   * @param clientSecret
   *          the client secret of the registered client (application) in OAuth2 server. Cannot be
   *          <code>null</code>!
   * @param providerName
   *          the OAuth2 provider name. Cannot be <code>null</code>!
   * @param redirectEndpoint
   *          the redirect endpoint which registered in OAuth2 server. Cannot be <code>null</code>!
   * @param scope
   *          the OAuth2 configuration values. Cannot be <code>null</code>!
   * @param tokenEndpoint
   *          the token endpoint of OAuth2 server. Cannot be <code>null</code>!
   *
   * @throws NullPointerException
   *           if one of the parameters is <code>null</code>.
   */
  public OAuth2ConfigurationImpl(final String authorizationEndpoint, final String clientId,
      final String clientSecret, final String providerName, final String redirectEndpoint,
      final String scope, final String tokenEndpoint) {
    this.authorizationEndpoint = Objects.requireNonNull(authorizationEndpoint,
        "The authorizationEndpoint cannot be null.");
    this.clientId = Objects.requireNonNull(clientId, "The clientId cannot be null.");
    this.clientSecret = Objects.requireNonNull(clientSecret, "The clientSecret cannot be null.");
    this.providerName = Objects.requireNonNull(providerName, "The providerName cannot be null.");
    this.redirectEndpoint = Objects.requireNonNull(redirectEndpoint,
        "The redirectEndpoint cannot be null.");
    this.scope = Objects.requireNonNull(scope, "The scope cannot be null.");
    this.tokenEndpoint = Objects.requireNonNull(tokenEndpoint, "The tokenEndpoint cannot be null.");
  }

  @Override
  public String authorizationEndpoint() {
    return authorizationEndpoint;
  }

  @Override
  public String clientId() {
    return clientId;
  }

  @Override
  public String clientSecret() {
    return clientSecret;
  }

  @Override
  public String providerName() {
    return providerName;
  }

  @Override
  public String redirectEndpoint() {
    return redirectEndpoint;
  }

  @Override
  public String scope() {
    return scope;
  }

  @Override
  public String tokenEndpoint() {
    return tokenEndpoint;
  }

}
