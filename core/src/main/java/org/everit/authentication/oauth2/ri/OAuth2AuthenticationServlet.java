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

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.everit.authentication.oauth2.OAuth2Configuration;
import org.everit.authentication.oauth2.OAuth2RequestURIResolver;
import org.everit.authentication.oauth2.ri.api.OAuth2SessionAttributeNames;
import org.everit.osgi.authentication.http.session.AuthenticationSessionAttributeNames;
import org.everit.osgi.resource.resolver.ResourceIdResolver;
import org.everit.web.servlet.HttpServlet;
import org.slf4j.Logger;

/**
 * Servlet that manage OAuth2 authentication and implementation of
 * {@link OAuth2SessionAttributeNames}.
 */
public class OAuth2AuthenticationServlet extends HttpServlet
    implements OAuth2SessionAttributeNames {

  // TODO nyilvanos api Oauth2Configuration, RequestUriResolver
  // TODO ri/api, ri-core, ri-schema
  // TODO tests jetty bekonfigurálva féliaz oauth leírva hogy kell konfigurálni.
  // sample-google,sample-facebook
  // publikus login oldal, google,fb login, sikeres login. Teljes nev megjelentés irjuk ki a db-be
  // mentet rekordokat azon az oldalon még.
  // TODO loguot (token invalnidálás oauth serveren) + redirect a sessionauthenticationcomponentre
  // (logouturl-jére)
  // sessionauthenticationcompoenent a servletcontextfactorybe konfirurálni + filter.

  private static final String PARAM_TOKEN_TYPE = "token_type";

  private AuthenticationSessionAttributeNames authenticationSessionAttributeNames;

  private String failedUrl;

  private Logger logger;

  private String loginEndpointPath;

  private OAuth2Configuration oauth2Configuration;

  private String redirectEndpointPath;

  private OAuth2RequestURIResolver requestURIResolver;

  private ResourceIdResolver resourceIdResolver;

  private String successUrl;

  /**
   * Constructor.
   *
   * @param authenticationSessionAttributeNames
   *          the {@link AuthenticationSessionAttributeNames} instance. Cannot be <code>null</code>!
   * @param failedUrl
   *          the URL where the user will be redirected by default in case of a failed
   *          authentication. Cannot be <code>null</code>!
   * @param loginEndpointPath
   *          the servlet path where the user start authentication process.
   * @param oauth2Configuration
   *          the {@link OAuth2Configuration} instance. Cannot be <code>null</code>!
   * @param redirectEndpointPath
   *          the servlet path where the OAuth2 server redirect.
   * @param requestURIResolver
   *          the {@link OAuth2RequestURIResolver} instance. Cannot be <code>null</code>!
   * @param resourceIdResolver
   *          the {@link ResourceIdResolver} instance. Cannot be <code>null</code>!
   * @param successUrl
   *          the URL where the user will be redirected by default in case of a successful
   *          authentication. Cannot be <code>null</code>!
   * @param logger
   *          the {@link Logger} instance. Cannot be <code>null</code>!
   *
   * @throws NullPointerException
   *           if one of the parameters is <code>null</code>.
   */
  public OAuth2AuthenticationServlet(
      final AuthenticationSessionAttributeNames authenticationSessionAttributeNames,
      final String failedUrl, final String loginEndpointPath,
      final OAuth2Configuration oauth2Configuration, final String redirectEndpointPath,
      final OAuth2RequestURIResolver requestURIResolver,
      final ResourceIdResolver resourceIdResolver, final String successUrl, final Logger logger) {
    Objects.requireNonNull(authenticationSessionAttributeNames,
        "The authenticationSessionAttributeNames cannot be null.");
    Objects.requireNonNull(failedUrl, "The failedUrl cannot be null.");
    Objects.requireNonNull(loginEndpointPath, "The loginEndpointPath cannot be null.");
    Objects.requireNonNull(oauth2Configuration, "The oauth2Configuration cannot be null.");
    Objects.requireNonNull(redirectEndpointPath, "The redirectEndpointPath cannot be null.");
    Objects.requireNonNull(requestURIResolver, "The requestURIResolver cannot be null.");
    Objects.requireNonNull(resourceIdResolver, "The resourceIdResolver cannot be null.");
    Objects.requireNonNull(successUrl, "The successUrl cannot be null.");
    Objects.requireNonNull(logger, "The logger cannot be null.");

    this.authenticationSessionAttributeNames = authenticationSessionAttributeNames;
    this.failedUrl = failedUrl;
    this.loginEndpointPath = loginEndpointPath;
    this.oauth2Configuration = oauth2Configuration;
    this.redirectEndpointPath = redirectEndpointPath;
    this.requestURIResolver = requestURIResolver;
    this.resourceIdResolver = resourceIdResolver;
    this.successUrl = successUrl;
    this.logger = logger;
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

  private OAuthJSONAccessTokenResponse obtainAccessToken(final String authorizationCode)
      throws OAuthSystemException, OAuthProblemException {
    OAuthClientRequest request = OAuthClientRequest
        .tokenLocation(oauth2Configuration.tokenEndpoint())
        .setClientId(oauth2Configuration.clientId())
        .setClientSecret(oauth2Configuration.clientSecret())
        .setRedirectURI(oauth2Configuration.redirectEndpoint())
        .setGrantType(GrantType.AUTHORIZATION_CODE)
        .setCode(authorizationCode)
        .buildBodyMessage();

    OAuthClient client = new OAuthClient(new URLConnectionClient());
    OAuthJSONAccessTokenResponse oauthResponse =
        client.accessToken(request, OAuthJSONAccessTokenResponse.class);
    return oauthResponse;
  }

  private void processOAuth2Response(final HttpServletRequest req,
      final HttpServletResponse resp) throws IOException {
    try {
      // Authentication response from oauth server
      OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(req);

      OAuthJSONAccessTokenResponse oauthAccessTokenResponse = obtainAccessToken(oar.getCode());

      // Store the access token response in the session
      HttpSession httpSession = req.getSession();

      String accessToken = oauthAccessTokenResponse.getAccessToken();
      httpSession.setAttribute(oauth2AccessToken(), accessToken);

      Long expiresIn = oauthAccessTokenResponse.getExpiresIn();
      httpSession.setAttribute(oauth2AccessTokenExpiresIn(),
          expiresIn);

      String refreshToken = oauthAccessTokenResponse.getRefreshToken();
      httpSession.setAttribute(oauth2RefreshToken(), refreshToken);

      String scope = oauthAccessTokenResponse.getScope();
      httpSession.setAttribute(oauth2Scope(), scope);

      String tokenType = oauthAccessTokenResponse.getParam(PARAM_TOKEN_TYPE);
      httpSession.setAttribute(oauth2TokenType(), tokenType);

      // Resource ID mapping
      String uniqueUserId = requestURIResolver.getUniqueUserId(
          tokenType,
          accessToken,
          expiresIn,
          refreshToken,
          scope);

      Optional<Long> optionalAuthenticatedResourceId = resourceIdResolver
          .getResourceId(oauth2Configuration.providerName() + ";" + uniqueUserId);
      if (!optionalAuthenticatedResourceId.isPresent()) {
        logger.info("Unique user ID '" + uniqueUserId + "' cannot be mapped to Resource ID");
        redirectToFailedUrl(resp);
        return;
      }

      // Store the resource ID in the session
      Long authenticatedResourceId = optionalAuthenticatedResourceId.get();
      httpSession.setAttribute(
          authenticationSessionAttributeNames.authenticatedResourceId(), authenticatedResourceId);

      resp.sendRedirect(successUrl);

    } catch (OAuthProblemException | OAuthSystemException e) {
      logger.info("Problem in authenticate process.", e);
      redirectToFailedUrl(resp);
      return;
    }
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
      processOAuth2Response(req, resp);
    }
    // if oauthLogout
    // 1. oauth logout
    // 2. resp.sendRedirect(http session logout url)
  }

  private void startOAuth2Authentication(final HttpServletResponse resp) throws IOException {
    try {
      // Authentication (to redirect oauth server).
      OAuthClientRequest request = OAuthClientRequest
          .authorizationLocation(oauth2Configuration.authorizationEndpoint())
          .setClientId(oauth2Configuration.clientId())
          .setRedirectURI(oauth2Configuration.redirectEndpoint())
          .setResponseType(ResponseType.CODE.toString())
          .setScope(oauth2Configuration.scope())
          .buildQueryMessage();

      resp.sendRedirect(request.getLocationUri());
    } catch (OAuthSystemException e) {
      // not throw in implementation
      // (org.apache.oltu.oauth2.common.parameters.QueryParameterApplier)
    }
  }

}
