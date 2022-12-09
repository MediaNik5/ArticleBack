package org.catblocks.articleback.security.extractor

import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service

@Service
class UserInfoExtractorService(
    private val userInfoExtractors: List<UserInfoExtractor>,
) {

    fun extract(
        response: ResponseEntity<Map<String, Any?>>,
        userRequest: OAuth2UserRequest,
    ): Map<String, Any?> {
        val registrationId = userRequest.clientRegistration.registrationId
        return userInfoExtractors
            .first { userInfoExtractor: UserInfoExtractor ->
                userInfoExtractor.canProceed(registrationId)
            }.extract(response.body!!, userRequest)
    }
}