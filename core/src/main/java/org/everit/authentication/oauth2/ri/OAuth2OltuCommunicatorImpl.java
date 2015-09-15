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

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.everit.authentication.oauth2.AccessTokenResponse;
import org.everit.authentication.oauth2.OAuth2Communicator;
import org.everit.authentication.oauth2.ri.internal.OltuAccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Implementation of {@link OAuth2Communicator} based on Apache Oltu.
 */
public class OAuth2OltuCommunicatorImpl implements OAuth2Communicator {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2OltuCommunicatorImpl.class);

  private final OAuth2ConfigurationParam configuration;

  private final String userInformationRequestURI;

  /**
   * Constructor.
   *
   * @param configuration
   *          the configuration used for communication
   * @param userInformationRequestURI
   *          the request URI from which we obtain the unique user ID
   *
   * @throws NullPointerException
   *           if one of the parameters is <code>null</code>.
   */
  public OAuth2OltuCommunicatorImpl(final OAuth2ConfigurationParam configuration,
      final String userInformationRequestURI) {
    this.configuration = Objects.requireNonNull(configuration,
        "configuration cannot be null");
    this.userInformationRequestURI = Objects.requireNonNull(userInformationRequestURI,
        "userInformationRequestURI cannot be null");
  }

  @Override
  public String buildAuthorizationUri(final String redirectUri) {
    try {
      return OAuthClientRequest
          .authorizationLocation(configuration.authorizationEndpoint)
          .setClientId(configuration.clientId)
          .setRedirectURI(redirectUri)
          .setResponseType(ResponseType.CODE.toString())
          .setScope(configuration.scope)
          .buildQueryMessage()
          .getLocationUri();
    } catch (OAuthSystemException e) {
      throw new RuntimeException("Failed to build the authorization uri.", e);
    }
  }

  @Override
  public String getProviderName() {
    return configuration.providerName;
  }

  @Override
  public Optional<String> getUniqueUserId(final AccessTokenResponse accessTokenResponse) {

    String accessToken = accessTokenResponse.getAccessToken();

    OAuthResourceResponse resourceResponse;

    try {
      OAuthClientRequest resourceRequest =
          new OAuthBearerClientRequest(userInformationRequestURI)
              .setAccessToken(accessToken)
              .buildHeaderMessage();

      OAuthClient client = new OAuthClient(new URLConnectionClient());
      resourceResponse = client.resource(resourceRequest, "GET", OAuthResourceResponse.class);

    } catch (OAuthSystemException | OAuthProblemException e) {

      LOGGER.error("Authentication failed.", e);
      return Optional.empty();
    }

    JsonObject fromJson = new Gson().fromJson(resourceResponse.getBody(), JsonObject.class);
    return Optional.of(fromJson.get("id").toString());
  }

  @Override
  public Optional<AccessTokenResponse> readAccessToken(final HttpServletRequest req,
      final String redirectUri) {

    try {
      OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(req);

      OAuthClientRequest request = OAuthClientRequest
          .tokenLocation(configuration.tokenEndpoint)
          .setClientId(configuration.clientId)
          .setClientSecret(configuration.clientSecret)
          .setRedirectURI(redirectUri)
          .setGrantType(GrantType.AUTHORIZATION_CODE)
          .setCode(oar.getCode())
          .buildBodyMessage();

      OAuthClient client = new OAuthClient(new URLConnectionClient());
      return Optional.of(client.accessToken(request, OltuAccessTokenResponse.class));

    } catch (OAuthSystemException | OAuthProblemException e) {

      LOGGER.error("Authentication failed.", e);
      return Optional.empty();
    }
  }

}
