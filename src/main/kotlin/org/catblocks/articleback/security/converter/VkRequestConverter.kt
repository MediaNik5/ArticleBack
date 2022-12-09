package org.catblocks.articleback.security.converter

import org.catblocks.articleback.model.Provider
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

private val vk = Provider.VK.toString().lowercase(Locale.getDefault())

@Component
class VkRequestConverter : OAuth2Converter {

    override fun canProceed(registrationId: String): Boolean = vk == registrationId

    override fun extract(userRequest: OAuth2UserRequest): RequestEntity<*> {
        val client = userRequest.clientRegistration
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = DEFAULT_CONTENT_TYPE

        val uri = UriComponentsBuilder
            .fromUriString(client.providerDetails.userInfoEndpoint.uri)
            .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.accessToken.tokenValue)
            .queryParam("user_ids", userRequest.additionalParameters["user_id"].toString())
            .queryParam("fields", DEFAULT_FIELDS)
            .queryParam("v", "5.131")
            .build().toUri()

        return RequestEntity<Any>(headers, HttpMethod.POST, uri)
    }

    companion object {
        private val DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON
        private val DEFAULT_FIELDS = listOf("photo_max_orig")
    }
}