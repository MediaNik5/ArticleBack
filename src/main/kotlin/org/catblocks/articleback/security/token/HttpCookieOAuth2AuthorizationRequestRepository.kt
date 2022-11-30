package org.catblocks.articleback.security.token

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import org.springframework.util.SerializationUtils
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
const val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"
private const val cookieExpireSeconds = 180

@Component
class HttpCookieOAuth2AuthorizationRequestRepository :
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        println("localaddr: ${request.localAddr}")
        request.headerNames.toList().forEach { println("Header $it: ${request.getHeaders(it).toList()}") }
        println("Localname: ${request.localName}")
        return request
            ?.cookies
            ?.find { it.name == OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME }
            ?.deserialize()
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return loadAuthorizationRequest(request)
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        if (authorizationRequest == null) {
            deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }
        addCookie(
            response,
            OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
            serialize(authorizationRequest),
            cookieExpireSeconds,
        )
        val redirectAfterCompleteLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)
        if (!redirectAfterCompleteLogin.isNullOrBlank()) {
            addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectAfterCompleteLogin, cookieExpireSeconds)
        }
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): OAuth2AuthorizationRequest? {
        return loadAuthorizationRequest(request)
    }

    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}

private fun Cookie?.deserialize(): OAuth2AuthorizationRequest? {
    if (this === null)
        return null
    return deserialize(this)
}

fun <T> deserialize(cookie: Cookie): T {
    return SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.value)) as T
}

fun serialize(`object`: Any?): String {
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(`object`))
}

fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
    val cookies = request.cookies
    cookies
        ?.filter { it.name == name }
        ?.forEach { cookie ->
            cookie.value = ""
            cookie.path = "/"
            cookie.maxAge = 0
            response.addCookie(cookie)
        }
}

fun addCookie(response: HttpServletResponse, name: String?, value: String?, maxAge: Int) {
    val cookie = Cookie(name, value)
    cookie.path = "/"
    cookie.isHttpOnly = true
    cookie.maxAge = maxAge
    response.addCookie(cookie)
}