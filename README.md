# authentication-oauth2-ri

The reference implementation of the [authentication-oauth2-api][1] based on 
[OAuth2][2] ([RFC6749][5]) integrated with [Everit Authentication][4] concept. 
The implementation is independent from OAuth2 servers and was tested with 
Google and Facebook.

# Modules
* api: implementation specific API.
* schema: the database schema (based on [Modularized Persistence][3]) to 
be able to map the users of OAuth2 providers with [resources][6].
* core: the implementation of the API and the *OAuth2AuthenticationServlet* 
that controls the communication.

# Sample

The sample is available in the *sample* module of [authentication-oauth2-ri-ecm][7].

[1]: https://github.com/everit-org/authentication-oauth2-api
[2]: http://oauth.net/2/
[3]: https://everitorg.wordpress.com/2014/06/18/modularized-persistence/
[4]: http://everitorg.wordpress.com/2014/07/31/everit-authentication/
[5]: http://tools.ietf.org/html/rfc6749
[6]: https://github.com/everit-org/resource-api
[7]: https://github.com/everit-org/authentication-oauth2-ri-ecm
