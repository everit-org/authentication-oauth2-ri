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

import org.everit.authentication.oauth2.OAuth2SessionAttributeNames;

/**
 * DTO for holding session attribute names used by the
 * {@link org.everit.authentication.oauth2.ri.OAuth2AuthenticationServlet}.
 */
public class OAuth2SessionAttributeNameDTO implements OAuth2SessionAttributeNames {

  private final String sessionAttrNameAccessToken;

  private final String sessionAttrNameAccessTokenType;

  private final String sessionAttrNameExpiresIn;

  private final String sessionAttrNameProviderName;

  private final String sessionAttrNameRefreshToken;

  private final String sessionAttrNameScope;

  /**
   * Constructor.
   */
  public OAuth2SessionAttributeNameDTO(
      final String sessionAttrNameProviderName,
      final String sessionAttrNameAccessToken,
      final String sessionAttrNameAccessTokenType,
      final String sessionAttrNameExpiresIn,
      final String sessionAttrNameRefreshToken,
      final String sessionAttrNameScope) {
    super();
    this.sessionAttrNameProviderName = sessionAttrNameProviderName;
    this.sessionAttrNameAccessToken = sessionAttrNameAccessToken;
    this.sessionAttrNameAccessTokenType = sessionAttrNameAccessTokenType;
    this.sessionAttrNameExpiresIn = sessionAttrNameExpiresIn;
    this.sessionAttrNameRefreshToken = sessionAttrNameRefreshToken;
    this.sessionAttrNameScope = sessionAttrNameScope;
  }

  @Override
  public String accessToken() {
    return sessionAttrNameAccessToken;
  }

  @Override
  public String accessTokenType() {
    return sessionAttrNameAccessTokenType;
  }

  @Override
  public String expiresIn() {
    return sessionAttrNameExpiresIn;
  }

  @Override
  public String providerName() {
    return sessionAttrNameProviderName;
  }

  @Override
  public String refreshToken() {
    return sessionAttrNameRefreshToken;
  }

  @Override
  public String scope() {
    return sessionAttrNameScope;
  }

}
