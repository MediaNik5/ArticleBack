package org.catblocks.articleback.security.token

import org.springframework.core.convert.converter.Converter
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

private const val INVALID_TOKEN_RESPONSE_ERROR_CODE = "invalid_token_response"

@Component

class AccessTokenResponseClient : OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private val defaultConverter: Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<*>> =
        OAuth2AuthorizationCodeGrantRequestEntityConverter()
    private val restOperations: RestOperations

    init {
        val converter = OAuth2AccessTokenResponseHttpMessageConverter()
        converter.setAccessTokenResponseConverter(VkAccessTokenResponseConverter())
        val restTemplate = RestTemplate(listOf(FormHttpMessageConverter(), converter))
        restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()
        restOperations = restTemplate
    }

    override fun getTokenResponse(
        authorizationCodeGrantRequest: OAuth2AuthorizationCodeGrantRequest,
    ): OAuth2AccessTokenResponse {
        val request: RequestEntity<*> = defaultConverter.convert(authorizationCodeGrantRequest)!!
        val response = getResponse(request)
        var tokenResponse = response.body
        if (tokenResponse?.accessToken?.scopes.isNullOrEmpty()) {
            // As per spec, in Section 5.1 Successful Access Token Response
            // https://tools.ietf.org/html/rfc6749#section-5.1
            // If AccessTokenResponse.scope is empty, then default to the scope
            // originally requested by the client in the Token Request
            // @formatter:off
            tokenResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse)
                .scopes(authorizationCodeGrantRequest.clientRegistration.scopes)
                .build()
            // @formatter:on
        }
        return tokenResponse!!
    }

    private fun getResponse(request: RequestEntity<*>): ResponseEntity<OAuth2AccessTokenResponse> {
        return try {
            restOperations.exchange(request, OAuth2AccessTokenResponse::class.java)
        } catch (ex: RestClientException) {
            val oauth2Error = OAuth2Error(
                INVALID_TOKEN_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: "
                        + ex.message,
                null
            )
            throw OAuth2AuthorizationException(oauth2Error, ex)
        }
    }
}