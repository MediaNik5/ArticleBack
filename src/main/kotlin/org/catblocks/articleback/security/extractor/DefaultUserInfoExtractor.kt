package org.catblocks.articleback.security.extractor

import org.catblocks.articleback.model.Provider
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class DefaultUserInfoExtractor : UserInfoExtractor {
    override fun canProceed(registrationId: String): Boolean {
        return AUTH_PROVIDERS.contains(registrationId)
    }

    override fun extract(o: Map<String, Any?>, userRequest: OAuth2UserRequest): Map<String, Any?> {
        return o
    }

    companion object {
        private val AUTH_PROVIDERS = Arrays
            .stream(Provider.values())
            .map { obj: Provider -> obj.toString().lowercase(Locale.getDefault()) }
            .filter { it != "vk" }
            .collect(Collectors.toUnmodifiableSet())
    }
}