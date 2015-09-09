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
package org.everit.authentication.oauth2.ri.dto;

import org.everit.authentication.oauth2.OAuth2UserIdResolver;
import org.everit.authentication.oauth2.ri.OAuth2Communicator;
import org.everit.osgi.authentication.http.session.AuthenticationSessionAttributeNames;
import org.everit.osgi.resource.resolver.ResourceIdResolver;

/**
 * OAuth2 Authenticiation Servlet parameter container.
 */
public class OAuth2AuthenticationServletParameter {

  public AuthenticationSessionAttributeNames authenticationSessionAttributeNames;

  public String failedUrl;

  public String loginEndpointPath;

  public OAuth2Communicator oauth2Communicator;

  public OAuth2UserIdResolver oauth2UserIdResolver;

  public String redirectEndpointPath;

  public ResourceIdResolver resourceIdResolver;

  public String successUrl;

  public OAuth2AuthenticationServletParameter authenticationSessionAttributeNames(
      final AuthenticationSessionAttributeNames authenticationSessionAttributeNames) {
    this.authenticationSessionAttributeNames = authenticationSessionAttributeNames;
    return this;
  }

  public OAuth2AuthenticationServletParameter failedUrl(final String failedUrl) {
    this.failedUrl = failedUrl;
    return this;
  }

  public OAuth2AuthenticationServletParameter loginEndpointPath(final String loginEndpointPath) {
    this.loginEndpointPath = loginEndpointPath;
    return this;
  }

  public OAuth2AuthenticationServletParameter oauth2Communicator(
      final OAuth2Communicator oauth2Communicator) {
    this.oauth2Communicator = oauth2Communicator;
    return this;
  }

  public OAuth2AuthenticationServletParameter oauth2UserIdResolver(
      final OAuth2UserIdResolver oauth2UserIdResolver) {
    this.oauth2UserIdResolver = oauth2UserIdResolver;
    return this;
  }

  public OAuth2AuthenticationServletParameter redirectEndpointPath(
      final String redirectEndpointPath) {
    this.redirectEndpointPath = redirectEndpointPath;
    return this;
  }

  public OAuth2AuthenticationServletParameter resourceIdResolver(
      final ResourceIdResolver resourceIdResolver) {
    this.resourceIdResolver = resourceIdResolver;
    return this;
  }

  public OAuth2AuthenticationServletParameter successUrl(final String successUrl) {
    this.successUrl = successUrl;
    return this;
  }

}
