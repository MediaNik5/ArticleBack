package org.catblocks.articleback.security.converter

import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest

interface OAuth2Converter {
    fun canProceed(registrationId: String): Boolean
    fun extract(userRequest: OAuth2UserRequest): RequestEntity<*>
}