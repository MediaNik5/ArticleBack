package org.catblocks.articleback.security.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.WebUtils.getCookie
import java.io.IOException
import java.net.URI
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationSuccessHandler(
    private val tokenProvider: TokenProvider,
    @Value("\${auth.redirectUris}") private val authorizedRedirectUris: List<String>,
) : SimpleUrlAuthenticationSuccessHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        super.onAuthenticationSuccess(request, response, authentication)
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ): String {
        val redirectUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value
        if (redirectUrl != null && !isAuthorizedRedirectUri(redirectUrl)) {
//            throw ResponseStatusException(
//                HttpStatus.BAD_REQUEST,
//                "Sorry! We've got an Unauthorized Redirect URI $redirectUrl and can't proceed with the authentication"
//            )
        }
        val targetUrl = redirectUrl ?: defaultTargetUrl
        val token = tokenProvider.createToken(authentication)
        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("token", token)
            .build().toUriString()
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)
        println(authorizedRedirectUris)
        return authorizedRedirectUris
            .any { authorizedRedirectUri: String ->
                // Only validate host and port. Let the clients use different paths if they want to
                val authorizedURI = URI.create(authorizedRedirectUri)
                (authorizedURI.host.equals(clientRedirectUri.host, ignoreCase = true)
                        && authorizedURI.port == clientRedirectUri.port)
            }
    }
}