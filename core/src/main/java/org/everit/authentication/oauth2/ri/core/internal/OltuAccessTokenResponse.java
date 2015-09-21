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
package org.everit.authentication.oauth2.ri.core.internal;

import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.token.BasicOAuthToken;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.everit.authentication.oauth2.AccessTokenResponse;

/**
 * Data holder of the Access Token Response.
 */
public class OltuAccessTokenResponse
    extends OAuthAccessTokenResponse
    implements AccessTokenResponse {

  private static final String PARAM_TOKEN_TYPE = "token_type";

  @Override
  public String getAccessToken() {
    return getParam(OAuth.OAUTH_ACCESS_TOKEN);
  }

  @Override
  public String getAccessTokenType() {
    return getParam(PARAM_TOKEN_TYPE);
  }

  @Override
  public Long getExpiresIn() {
    String value = getParam(OAuth.OAUTH_EXPIRES_IN);
    return value == null ? null : Long.valueOf(value);
  }

  @Override
  public OAuthToken getOAuthToken() {
    return new BasicOAuthToken(getAccessToken(), getExpiresIn(), getRefreshToken(), getScope());
  }

  @Override
  public String getRefreshToken() {
    return getParam(OAuth.OAUTH_REFRESH_TOKEN);
  }

  @Override
  public String getScope() {
    return getParam(OAuth.OAUTH_SCOPE);
  }

  @Override
  protected void init(final String body, final String contentType, final int responseCode)
      throws OAuthProblemException {
    setParameters(contentType, body);
    super.init(body, contentType, responseCode);
  }

  @Override
  protected void setBody(final String body) throws OAuthProblemException {
    this.body = body;
  }

  @Override
  protected void setContentType(final String contentType) {
    this.contentType = contentType;
  }

  private void setParameters(final String contentType, final String body)
      throws OAuthProblemException {
    if (contentType.contains("application/json")) {
      try {
        parameters = JSONUtils.parseJSON(body);
      } catch (Throwable e) {
        throw OAuthProblemException.error(OAuthError.CodeResponse.UNSUPPORTED_RESPONSE_TYPE,
            "Invalid response! Response body is not " + OAuth.ContentType.JSON + " encoded");
      }
    } else {
      parameters = OAuthUtils.decodeForm(body);
    }
  }

  @Override
  protected void setResponseCode(final int responseCode) {
    this.responseCode = responseCode;
  }

}
