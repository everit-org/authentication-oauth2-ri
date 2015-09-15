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
package org.everit.authentication.oauth2.ri;

/**
 * Provides OAuth2 server configurations.
 */
public class OAuth2ConfigurationParam {

  public final String authorizationEndpoint;

  public final String clientId;

  public final String clientSecret;

  public final String providerName;

  public final String scope;

  public final String tokenEndpoint;

  /**
   * Constructor.
   */
  public OAuth2ConfigurationParam(final String providerName, final String clientId,
      final String clientSecret, final String authorizationEndpoint, final String tokenEndpoint,
      final String scope) {
    super();
    this.providerName = providerName;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authorizationEndpoint = authorizationEndpoint;
    this.tokenEndpoint = tokenEndpoint;
    this.scope = scope;
  }

}
