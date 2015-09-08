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

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.everit.authentication.oauth2.OAuth2UserIdResolver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The Google specific {@link OAuth2UserIdResolver} implementation.
 */
public class GoogleOAuth2UserIdResolverImpl implements OAuth2UserIdResolver {

  private String userInformationRequestURI;

  /**
   * Constructor.
   *
   * @param userInformationRequestURI
   *          the request URI from which we obtain the unique user ID. Cannot be <code>null</code>!
   *
   * @throws NullPointerException
   *           if one of the parameters is <code>null</code>.
   */
  public GoogleOAuth2UserIdResolverImpl(final String userInformationRequestURI) {
    this.userInformationRequestURI = Objects.requireNonNull(userInformationRequestURI,
        "The userInformationRequestURI cannot be null.");
  }

  @Override
  public String getUniqueUserId(final String tokenType, final String accessToken,
      final Long accessTokenExpiresIn, final String refreshToken, final String scope) {
    OAuthResourceResponse resourceResponse;
    try {
      OAuthClientRequest resourceRequest =
          new OAuthBearerClientRequest(userInformationRequestURI)
              .setAccessToken(accessToken)
              .buildHeaderMessage();

      OAuthClient client = new OAuthClient(new URLConnectionClient());
      resourceResponse = client.resource(resourceRequest, "GET", OAuthResourceResponse.class);
    } catch (OAuthSystemException | OAuthProblemException e) {
      throw new RuntimeException(e);
    }
    JsonObject fromJson = new Gson().fromJson(resourceResponse.getBody(), JsonObject.class);
    // the id is already unique.
    return fromJson.get("id").toString();
  }

}
