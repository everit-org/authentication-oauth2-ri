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

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.everit.authentication.oauth2.AccessTokenResponse;
import org.everit.authentication.oauth2.OAuth2Communicator;
import org.everit.authentication.oauth2.OAuth2SessionAttributeNames;
import org.everit.osgi.authentication.http.session.AuthenticationSessionAttributeNames;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.everit.web.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the OAuth2 authorization requests.
 */
public class OAuth2AuthenticationServlet
    extends HttpServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationServlet.class);

  private final AuthenticationSessionAttributeNames authenticationSessionAttributeNames;

  private final String failedUrl;

  private final OAuth2Communicator oauth2Communicator;

  private final OAuth2SessionAttributeNames oAuth2SessionAttributeNames;

  private final String processRequestTokenPathInfo;

  private final ResourceIdResolver resourceIdResolver;

  private final String successUrl;

  /**
   * Constructor.
   */
  public OAuth2AuthenticationServlet(
      final String successUrl,
      final String failedUrl,
      final String processRequestTokenPathInfo,
      final OAuth2Communicator oauth2Communicator,
      final ResourceIdResolver resourceIdResolver,
      final AuthenticationSessionAttributeNames authenticationSessionAttributeNames,
      final OAuth2SessionAttributeNames oAuth2SessionAttributeNames) {
    this.successUrl = Objects.requireNonNull(successUrl,
        "successUrl cannot be null");
    this.failedUrl = Objects.requireNonNull(failedUrl,
        "failedUrl cannot be null");
    this.processRequestTokenPathInfo = Objects.requireNonNull(processRequestTokenPathInfo,
        "processRequestTokenPathInfo cannot be null");
    this.oauth2Communicator = Objects.requireNonNull(oauth2Communicator,
        "oauth2Communicator cannot be null");
    this.resourceIdResolver = Objects.requireNonNull(resourceIdResolver,
        "resourceIdResolver cannot be null");
    this.authenticationSessionAttributeNames =
        Objects.requireNonNull(authenticationSessionAttributeNames,
            "authenticationSessionAttributeNames cannot be null");
    this.oAuth2SessionAttributeNames = Objects.requireNonNull(oAuth2SessionAttributeNames,
        "oAuth2SessionAttributeNames cannot be null");
  }

  private String buildRedirectUri(final HttpServletRequest req) {
    StringBuffer redirectUri = req.getRequestURL();
    String pathInfo = req.getPathInfo();
    if ((pathInfo == null) || !pathInfo.equals(processRequestTokenPathInfo)) {
      redirectUri.append(processRequestTokenPathInfo);
    }
    return redirectUri.toString();
  }

  private void processRequestToken(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {

    String redirectUri = buildRedirectUri(req);

    // Access token
    Optional<AccessTokenResponse> optionalAccessTokenResponse =
        oauth2Communicator.readAccessToken(req, redirectUri);
    if (!optionalAccessTokenResponse.isPresent()) {
      redirectToFailedUrl(resp, "Failed to retrieve access token.");
      return;
    }

    AccessTokenResponse accessTokenResponse = optionalAccessTokenResponse.get();

    // Store the access token response in the session
    HttpSession httpSession = req.getSession();
    storeAccessTokenResponseInSession(httpSession, accessTokenResponse);

    // Unique user ID
    Optional<String> optionalUniqueUserId =
        oauth2Communicator.getUniqueUserId(accessTokenResponse);
    if (!optionalUniqueUserId.isPresent()) {
      redirectToFailedUrl(resp, "Failed to retrieve unique user ID.");
      return;
    }

    String uniqueUserId = optionalUniqueUserId.get();

    // Resource ID mapping
    Optional<Long> optionalAuthenticatedResourceId =
        resourceIdResolver.getResourceId(uniqueUserId);
    if (!optionalAuthenticatedResourceId.isPresent()) {
      redirectToFailedUrl(resp,
          "Unique user ID '" + uniqueUserId + "' cannot be mapped to Resource ID");
      return;
    }

    // Store the resource ID in the session
    long authenticatedResourceId = optionalAuthenticatedResourceId.get();
    httpSession.setAttribute(
        authenticationSessionAttributeNames.authenticatedResourceId(), authenticatedResourceId);

    resp.sendRedirect(successUrl);
  }

  private void redirectToFailedUrl(final HttpServletResponse resp, final String message)
      throws IOException {
    LOGGER.info(message);
    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    resp.sendRedirect(failedUrl);
  }

  private void redirectToOAuthAuthorization(final HttpServletRequest req,
      final HttpServletResponse resp) throws IOException {
    String redirectUri = buildRedirectUri(req);
    String authorizationUrl = oauth2Communicator.buildAuthorizationUri(redirectUri);
    resp.sendRedirect(authorizationUrl);
  }

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

    String pathInfo = req.getPathInfo();

    if (pathInfo == null) {
      redirectToOAuthAuthorization(req, resp);
    } else if (pathInfo.equals(processRequestTokenPathInfo)) {
      processRequestToken(req, resp);
    }

  }

  private void storeAccessTokenResponseInSession(final HttpSession httpSession,
      final AccessTokenResponse oauthAccessTokenResponse) {
    httpSession.setAttribute(
        oAuth2SessionAttributeNames.providerName(), oauth2Communicator.getProviderName());
    httpSession.setAttribute(
        oAuth2SessionAttributeNames.accessToken(),
        oauthAccessTokenResponse.getAccessToken());
    httpSession.setAttribute(
        oAuth2SessionAttributeNames.expiresIn(),
        oauthAccessTokenResponse.getExpiresIn());
    httpSession.setAttribute(
        oAuth2SessionAttributeNames.refreshToken(),
        oauthAccessTokenResponse.getRefreshToken());
    httpSession.setAttribute(
        oAuth2SessionAttributeNames.scope(),
        oauthAccessTokenResponse.getScope());
    httpSession.setAttribute(
        oAuth2SessionAttributeNames.accessTokenType(),
        oauthAccessTokenResponse.getAccessTokenType());
  }

}
