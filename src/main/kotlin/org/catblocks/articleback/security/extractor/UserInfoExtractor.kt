package org.catblocks.articleback.security.extractor

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest

interface UserInfoExtractor {
    fun canProceed(registrationId: String): Boolean
    fun extract(o: Map<String, Any?>, userRequest: OAuth2UserRequest): Map<String, Any?>
}