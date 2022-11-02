package org.catblocks.articleback.security.converter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service


@Service
class RequestConverter(
    @param:Autowired private val requestConverters: List<Converter<RequestEntity<*>>>,
){
    fun convert(
        userRequest: OAuth2UserRequest
    ): RequestEntity<*> {
        val registrationId = userRequest.clientRegistration.registrationId
        return requestConverters
            .first { converter ->
                converter.canProceed(registrationId)
            }.extract(userRequest)
    }
}