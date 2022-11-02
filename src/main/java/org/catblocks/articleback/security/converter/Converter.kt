package org.catblocks.articleback.security.converter

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest

interface Converter<T> {
    fun canProceed(registrationId: String): Boolean
    fun extract(userRequest: OAuth2UserRequest): T
}