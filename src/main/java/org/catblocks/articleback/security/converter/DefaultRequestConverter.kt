package org.catblocks.articleback.security.converter

import org.catblocks.articleback.security.Provider
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

private val AUTH_PROVIDERS = Arrays
    .stream(Provider.values())
    .map { obj: Provider -> obj.toString() }
    .collect(Collectors.toUnmodifiableSet())

@Service
class DefaultRequestConverter :
    Converter<RequestEntity<*>> {
    private val defaultConverter = OAuth2UserRequestEntityConverter()

    override fun canProceed(registrationId: String): Boolean {
        return AUTH_PROVIDERS.contains(registrationId.uppercase(Locale.getDefault()))
    }

    override fun extract(userRequest: OAuth2UserRequest): RequestEntity<*> =
        defaultConverter.convert(userRequest)!!
}