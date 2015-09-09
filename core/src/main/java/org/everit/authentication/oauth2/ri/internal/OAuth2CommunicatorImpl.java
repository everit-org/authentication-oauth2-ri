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

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.everit.authentication.oauth2.ri.AccessTokenResponse;
import org.everit.authentication.oauth2.ri.OAuth2Communicator;
import org.everit.authentication.oauth2.ri.OAuth2ConfigurationDTO;

/**
 * Basic implementation of {@link OAuth2Communicator}.
 */
public class OAuth2CommunicatorImpl implements OAuth2Communicator {

  private OAuth2ConfigurationDTO oauth2Configuration;

  public OAuth2CommunicatorImpl(final OAuth2ConfigurationDTO oauth2Configuration) {
    this.oauth2Configuration = Objects.requireNonNull(oauth2Configuration,
        "The oauth2Configuration cannot be null.");
  }

  @Override
  public AccessTokenResponse getAccessToken(final HttpServletRequest req) {
    AccessTokenResponse oauthResponse = null;
    try {
      OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(req);

      OAuthClientRequest request = OAuthClientRequest
          .tokenLocation(oauth2Configuration.tokenEndpoint)
          .setClientId(oauth2Configuration.clientId)
          .setClientSecret(oauth2Configuration.clientSecret)
          .setRedirectURI(oauth2Configuration.redirectEndpoint)
          .setGrantType(GrantType.AUTHORIZATION_CODE)
          .setCode(oar.getCode())
          .buildBodyMessage();
      OAuthClient client = new OAuthClient(new URLConnectionClient());
      oauthResponse = client.accessToken(request, AccessTokenResponse.class);
    } catch (OAuthSystemException | OAuthProblemException e) {
      throw new RuntimeException("Problem with obtain access token from OAuth2 server", e);
    }
    return oauthResponse;
  }

  @Override
  public String getAuthorizationUriWithParams() {
    OAuthClientRequest request;
    try {
      request = OAuthClientRequest
          .authorizationLocation(oauth2Configuration.authorizationEndpoint)
          .setClientId(oauth2Configuration.clientId)
          .setRedirectURI(oauth2Configuration.redirectEndpoint)
          .setResponseType(ResponseType.CODE.toString())
          .setScope(oauth2Configuration.scope)
          .buildQueryMessage();
    } catch (OAuthSystemException e) {
      // not throw in implementation
      // (org.apache.oltu.oauth2.common.parameters.QueryParameterApplier)
      throw new RuntimeException("Problem with authorization uri create.", e);
    }
    return request.getLocationUri();
  }

}
