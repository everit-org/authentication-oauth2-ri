# authentication-oauth2-ri
**DRAFT**

Authentication Servlet based on [OAuth2][1] ([RFC6749][5]). 
The implementation is independent from OAuth2 servers (as far as possible). The OAuth2 server dependent interfaces is found [authentication-oauth2-api][2], but has default implementation in project that tested with Google and Facebook.

# Modules
* api: interfaces and classes.
* core: the implementation of interfaces and servlet.
* schema: the used database schema (read [Modularized Persistence][3]).

# Usage

## OAuth2Communicator

First create OAuth2ConfigurationDTO that contains OAuth2 server configurations.

### OAuth2Configuration

```java
OAuth2ConfigurationDTO oauth2Configuration = new OAuth2ConfigurationDTO()
  .authorizationEndpoint(authorizationEndpoint)
  .clientId(clientId)
  .clientSecret(clientSecret)
  .providerName(providerName)
  .redirectEndpoint(redirectEndpoint)
  .scope(scope)
  .tokenEndpoint(tokenEndpoint);
```
##### Configuration
* **providerName**: the OAuth2 provider name. Example: google.
* **clientId**: the client ID of the registered client (application) in OAuth2 server.
* **clientSecret**: the client secret of the registered client (application) in OAuth2 server.
* **redirectEndpoint**: the redirect endpoint which registered in OAuth2 server and our Servlet is listen. Example: /oauth2-redirect
* **authorizationEndpoint**: the authorization endpoint of OAuth2 server. Example: https://accounts.google.com/o/oauth2/auth
* **tokenEndpoint**: the token endpoint of OAuth2 server. Example: https://accounts.google.com/o/oauth2/token
* **scope**: the scope of the access request. OAuth2 server specific. Example: https://www.googleapis.com/auth/userinfo.profile

Then create the OAuth2Communicator that communicate OAuth2 server.

```java
OAuth2Communicator oAuth2Communicator = new OAuth2CommunicatorImpl(oauth2Configuration);
```

## ResourceIdResolver
Create an ResourceIdResolver that help to mapping OAuth2 user to resource ID. Read [concepts][4].

```java
ResourceIdResolver resourceIdResolver = new OAuth2ResourceIdResolverImpl(propertyManager, querydslSupport, resourceService, transactionHelper, providerName);
```

##### Configuration
* **propertyManager**: see [property-manager-api][6].
* **querydslSupport**: see [osgi-querydsl-support][8]. 
* **resourceService**: see [resource-api][9].
* **transactionHelper**: see [transaction-helper][10].
* **providerName**: the OAuth2 provider name. Example: google

## OAuth2UserIdResolver

Create an OAuth2UserIdResolver that help to obtain userID from OAuth2 server which already unique in client.

```java
OAuth2UserIdResolver oauth2UserIdResolver = new DefaultOAuth2UserIdResolverImpl(userInformationRequestURI);
```

##### Configuration
* **userInformationRequestURI**: the request URI from we obtain the unique user ID. Example: https://www.googleapis.com/userinfo/v2/me.

## OAuth2AuthenticationServlet

Finally create OAuth2AuthenticationServlet that manage OAuth2 authentication. 

```java
OAuth2AuthenticationServletParameter oauth2AuthenticationServletParameter = new OAuth2AuthenticationServletParameter()
  .authenticationSessionAttributeNames(authenticationSessionAttributeNames)
  .failedUrl(failedUrl)
  .loginEndpointPath(loginEndpointPath)
  .oauth2Communicator(oauth2Communicator)
  .oauth2UserIdResolver(oauth2UserIdResolver)
  .redirectEndpointPath(redirectEndpointPath)
  .resourceIdResolver(resourceIdResolver)
  .successUrl(successUrl);

OAuth2AuthenticationServlet oauth2AuthenticationServlet = new OAuth2AuthenticationServlet(oauth2AuthenticationServletParameter);
```

##### Configuration
* **successUrl**: the URL where the user will be redirected by default in case of a successful authentication.
* **failedUrl**: the URL where the user will be redirected by default in case of a failed authentication. 
* **loginEndpointPath**: the servlet path where the user start authentication process.
* **redirectEndpointPath**: the servlet path where the OAuth2 server redirect.
* **authenticationSessionAttributeNames**: see [authentication-http-session][11]
* **oauth2Communicator**: the OAuth2Communicator instance. 
* **resourceIdResolver**: the ResourceIdResolver instance
* **oauth2UserIdResolver**: the OAuth2UserIdResolver instance.

You can try solution in the [authentication-oauth2-ecm][12], sample module.

# Concept
Full authentication concept is available on blog post [Everit Authentication][4].

# Dependencies
* [property-manager-ri][18]
* [osgi-querydsl-support][8]
* [resource-ri][19]
* [transaction-helper][10]
* [authentication-http-session][13]
* [web-servlet][14]
* [resource-resolver-api][15]
* [oltu][16] (client)
* [gson][17] 

[1]: https://github.com/everit-org/authentication-oauth2-api
[2]: http://oauth.net/2/
[5]: http://tools.ietf.org/html/rfc6749
[6]: https://github.com/everit-org/property-manager-api
[8]: https://github.com/everit-org/osgi-querydsl-support
[9]: https://github.com/everit-org/resource-api
[10]: https://github.com/everit-org-archive/transaction-helper
[11]: https://github.com/everit-org/authentication-http-session
[12]: https://github.com/everit-org/authentication-oauth2-ecm
[3]: https://everitorg.wordpress.com/2014/06/18/modularized-persistence/
[4]: http://everitorg.wordpress.com/2014/07/31/everit-authentication/
[13]: https://github.com/everit-org/authentication-http-session
[14]: https://github.com/everit-org/web-servlet
[15]: https://github.com/everit-org/resource-resolver-api
[16]: https://oltu.apache.org/
[17]: https://github.com/google/gson
[18]: https://github.com/everit-org/property-manager-ri
[19]: https://github.com/everit-org/resource-ri