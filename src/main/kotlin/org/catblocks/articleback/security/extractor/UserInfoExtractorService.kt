package org.catblocks.articleback.security.extractor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service

@Service
class UserInfoExtractorService(
    @param:Autowired private val userInfoExtractors: List<Extractor<Map<String, Any>, Map<String, Any>>>,
) {
    fun extract(
        response: ResponseEntity<Map<String, Any>>,
        userRequest: OAuth2UserRequest
    ): Map<String, Any>{
        val registrationId = userRequest.clientRegistration.registrationId
        return userInfoExtractors
            .first { userInfoExtractor: Extractor<Map<String, Any>, Map<String, Any>> ->
                userInfoExtractor.canProceed(registrationId)
            }.extract(response.body!!, userRequest)
    }
}