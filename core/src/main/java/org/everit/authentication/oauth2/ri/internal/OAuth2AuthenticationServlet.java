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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.everit.authentication.oauth2.OAuth2UserIdResolver;
import org.everit.authentication.oauth2.ri.AccessTokenResponse;
import org.everit.authentication.oauth2.ri.OAuth2AuthenticationServletParameter;
import org.everit.authentication.oauth2.ri.OAuth2Communicator;
import org.everit.authentication.oauth2.ri.OAuth2SessionAttributeNames;
import org.everit.osgi.authentication.http.session.AuthenticationSessionAttributeNames;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.everit.web.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet that manage OAuth2 authentication and implementation of
 * {@link OAuth2SessionAttributeNames}.
 */
public class OAuth2AuthenticationServlet extends HttpServlet
    implements OAuth2SessionAttributeNames {

  private static final String PARAM_PROVIDER_NAME = "providerName";

  private AuthenticationSessionAttributeNames authenticationSessionAttributeNames;

  private String failedUrl;

  private Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationServlet.class);

  private String loginEndpointPath;

  private OAuth2Communicator oauth2Communicator;

  private OAuth2UserIdResolver oauth2UserIdResolver;

  private String redirectEndpointPath;

  private ResourceIdResolver resourceIdResolver;

  private String successUrl;

  /**
   * Constructor.
   *
   * @param parameters
   *          the {@link OAuth2AuthenticationServletParameter} class with contains all requeired
   *          parameters.
   *
   * @throws NullPointerException
   *           if one of the parameters is <code>null</code>.
   */
  public OAuth2AuthenticationServlet(final OAuth2AuthenticationServletParameter parameters) {
    Objects.requireNonNull(parameters, "The parameters cannot be null.");

    authenticationSessionAttributeNames =
        Objects.requireNonNull(parameters.authenticationSessionAttributeNames,
            "The authenticationSessionAttributeNames cannot be null.");
    failedUrl = Objects.requireNonNull(parameters.failedUrl, "The failedUrl cannot be null.");
    loginEndpointPath = Objects.requireNonNull(parameters.loginEndpointPath,
        "The loginEndpointPath cannot be null.");
    redirectEndpointPath = Objects.requireNonNull(parameters.redirectEndpointPath,
        "The redirectEndpointPath cannot be null.");
    oauth2Communicator = Objects.requireNonNull(parameters.oauth2Communicator,
        "The oauth2Communicator cannot be null.");
    oauth2UserIdResolver = Objects.requireNonNull(parameters.oauth2UserIdResolver,
        "The oauth2UserIdResolver cannot be null.");
    resourceIdResolver = Objects.requireNonNull(parameters.resourceIdResolver,
        "The resourceIdResolver cannot be null.");
    successUrl = Objects.requireNonNull(parameters.successUrl, "The successUrl cannot be null.");
  }

  private void continueOAuth2Authenticate(final HttpServletRequest req,
      final HttpServletResponse resp) throws IOException {
    try {
      // Authentication response from oauth server
      AccessTokenResponse oauthAccessTokenResponse =
          oauth2Communicator.getAccessToken(req);

      // Store the access token response in the session
      HttpSession httpSession = req.getSession();
      storeAccessTokenResponseInSession(httpSession, oauthAccessTokenResponse);

      // Resource ID mapping
      String uniqueUserId = oauth2UserIdResolver.getUniqueUserId(
          oauthAccessTokenResponse.getAccessTokenType(),
          oauthAccessTokenResponse.getAccessToken(),
          oauthAccessTokenResponse.getExpiresIn(),
          oauthAccessTokenResponse.getRefreshToken(),
          oauthAccessTokenResponse.getScope());

      Optional<Long> optionalAuthenticatedResourceId = resourceIdResolver
          .getResourceId(uniqueUserId);
      if (!optionalAuthenticatedResourceId.isPresent()) {
        logger.info("Unique user ID '" + uniqueUserId + "' cannot be mapped to Resource ID");
        redirectToFailedUrl(resp);
        return;
      }

      // Store the resource ID in the session
      Long authenticatedResourceId = optionalAuthenticatedResourceId.get();
      httpSession.setAttribute(
          authenticationSessionAttributeNames.authenticatedResourceId(), authenticatedResourceId);

      String providerName = req.getParameter(PARAM_PROVIDER_NAME);
      String successUrlWithParams = successUrl;
      if (providerName != null) {
        String encodedProviderName = encodeUrlParameter(providerName);
        if (successUrlWithParams.contains("?")) {
          successUrlWithParams += "&" + PARAM_PROVIDER_NAME + "=" + encodedProviderName;
        } else {
          successUrlWithParams += "?" + PARAM_PROVIDER_NAME + "=" + encodedProviderName;
        }
      }
      resp.sendRedirect(successUrlWithParams);

    } catch (Exception e) {
      logger.info("Problem in authenticate process.", e);
      redirectToFailedUrl(resp);
      return;
    }
  }

  /**
   * Encode parameter.
   *
   * @param param
   *          the parameter.
   * @return the encoded parameter.
   */
  public String encodeUrlParameter(final String param) {
    try {
      return URLEncoder.encode(param, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(StandardCharsets.UTF_8.name() + " is unknown");
    }
  }

  @Override
  public String oauth2AccessToken() {
    return "oauth2.access.token";
  }

  @Override
  public String oauth2AccessTokenExpiresIn() {
    return "oauth2.access.token.expires.in";
  }

  @Override
  public String oauth2RefreshToken() {
    return "oauth2.refresh.token";
  }

  @Override
  public String oauth2Scope() {
    return "oauth2.scope";
  }

  @Override
  public String oauth2TokenType() {
    return "oauth2.token.type";
  }

  private void redirectToFailedUrl(final HttpServletResponse resp) throws IOException {
    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    resp.sendRedirect(failedUrl);
  }

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    String servletPath = req.getServletPath();
    if (loginEndpointPath.equals(servletPath)) {
      startOAuth2Authentication(resp);
    } else if (redirectEndpointPath.equals(servletPath)) {
      continueOAuth2Authenticate(req, resp);
    }
  }

  private void startOAuth2Authentication(final HttpServletResponse resp) throws IOException {
    // Authentication (to redirect oauth server).
    try {
      String url = oauth2Communicator.buildAuthorizationUri();
      resp.sendRedirect(url);
    } catch (Exception e) {
      logger.info("Problem when start authentication process.", e);
      redirectToFailedUrl(resp);
      return;
    }
  }

  private void storeAccessTokenResponseInSession(final HttpSession httpSession,
      final AccessTokenResponse oauthAccessTokenResponse) {
    String accessToken = oauthAccessTokenResponse.getAccessToken();
    httpSession.setAttribute(oauth2AccessToken(), accessToken);

    Long expiresIn = oauthAccessTokenResponse.getExpiresIn();
    httpSession.setAttribute(oauth2AccessTokenExpiresIn(),
        expiresIn);

    String refreshToken = oauthAccessTokenResponse.getRefreshToken();
    httpSession.setAttribute(oauth2RefreshToken(), refreshToken);

    String scope = oauthAccessTokenResponse.getScope();
    httpSession.setAttribute(oauth2Scope(), scope);

    String tokenType = oauthAccessTokenResponse.getAccessTokenType();
    httpSession.setAttribute(oauth2TokenType(), tokenType);
  }

}
