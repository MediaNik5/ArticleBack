package org.catblocks.articleback.security.token

import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter
import org.springframework.web.util.UriComponentsBuilder

class VkAccessTokenRequestConverter : OAuth2AuthorizationCodeGrantRequestEntityConverter() {
    override fun convert(request: OAuth2AuthorizationCodeGrantRequest): RequestEntity<*> {
        val client = request.clientRegistration
        val uri = UriComponentsBuilder
            .fromUriString(client.providerDetails.tokenUri)
            .queryParam("client_id", client.clientId)
            .queryParam("client_secret", client.clientSecret)
            .queryParam("redirect_uri", request.authorizationExchange.authorizationResponse.redirectUri)
            .queryParam("code", request.authorizationExchange.authorizationResponse.code)
            .build().toUri()
        return RequestEntity<Any>(HttpMethod.GET, uri)
    }
}