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

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible to provide information from OAuth2 server.
 */
public interface OAuth2Communicator {

  /**
   * Build full authorization uri with parameters to OAuth2 server.
   *
   * @return the full authorization uri.
   */
  String buildAuthorizationUri();

  /**
   * Gets (obtain) access token from OAuth2 server.
   *
   * @param req
   *          the {@link HttpServletRequest} which contains Auth2 server response when redirect to
   *          own server after authorize user.
   * @return the {@link AccessTokenResponse}.
   */
  AccessTokenResponse getAccessToken(HttpServletRequest req);
}
