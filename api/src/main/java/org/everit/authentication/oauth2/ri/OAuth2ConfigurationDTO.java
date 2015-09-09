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
public class OAuth2ConfigurationDTO {

  public String authorizationEndpoint;

  public String clientId;

  public String clientSecret;

  public String providerName;

  public String redirectEndpoint;

  public String scope;

  public String tokenEndpoint;

  public OAuth2ConfigurationDTO authorizationEndpoint(final String authorizationEndpoint) {
    this.authorizationEndpoint = authorizationEndpoint;
    return this;
  }

  public OAuth2ConfigurationDTO clientId(final String clientId) {
    this.clientId = clientId;
    return this;
  }

  public OAuth2ConfigurationDTO clientSecret(final String clientSecret) {
    this.clientSecret = clientSecret;
    return this;
  }

  public OAuth2ConfigurationDTO providerName(final String providerName) {
    this.providerName = providerName;
    return this;
  }

  public OAuth2ConfigurationDTO redirectEndpoint(final String redirectEndpoint) {
    this.redirectEndpoint = redirectEndpoint;
    return this;
  }

  public OAuth2ConfigurationDTO scope(final String scope) {
    this.scope = scope;
    return this;
  }

  public OAuth2ConfigurationDTO tokenEndpoint(final String tokenEndpoint) {
    this.tokenEndpoint = tokenEndpoint;
    return this;
  }

}
