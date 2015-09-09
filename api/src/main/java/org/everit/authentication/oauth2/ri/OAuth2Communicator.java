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

import org.everit.authentication.oauth2.ri.dto.AccessTokenResponse;
import org.everit.authentication.oauth2.ri.exception.OAuth2Exception;

public interface OAuth2Communicator {

  AccessTokenResponse getAccessToken(HttpServletRequest req) throws OAuth2Exception;

  String getAuthorizationUriWithParams() throws OAuth2Exception;
}
