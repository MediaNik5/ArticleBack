package org.catblocks.articleback.security.converter

import org.catblocks.articleback.model.Provider
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

private val AUTH_PROVIDERS = Arrays
    .stream(Provider.values())
    .map { obj: Provider -> obj.toString().lowercase(Locale.getDefault()) }
    .filter { s: String -> s != "vk" }
    .collect(Collectors.toUnmodifiableSet())

@Service
class DefaultRequestConverter : OAuth2Converter {
    private val defaultConverter = OAuth2UserRequestEntityConverter()

    override fun canProceed(registrationId: String): Boolean {
        return AUTH_PROVIDERS.contains(registrationId)
    }

    override fun extract(userRequest: OAuth2UserRequest): RequestEntity<*> =
        defaultConverter.convert(userRequest)!!
}