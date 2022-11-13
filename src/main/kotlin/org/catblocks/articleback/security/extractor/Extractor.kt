package org.catblocks.articleback.security.extractor

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest

interface Extractor<Result, E> {
    fun canProceed(registrationId: String): Boolean
    fun extract(o: E, userRequest: OAuth2UserRequest?): Result
}