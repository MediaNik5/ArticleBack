package org.catblocks.articleback.security.extractor

import org.catblocks.articleback.security.Provider
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class DefaultUserInfoExtractor : Extractor<Map<String, Any?>?, Map<String, Any?>?> {
    override fun canProceed(registrationId: String): Boolean {
        return AUTH_PROVIDERS.contains(registrationId.uppercase())
    }

    override fun extract(o: Map<String, Any?>?, userRequest: OAuth2UserRequest?): Map<String, Any?>? {
        TODO("Not yet implemented")
    }

    companion object {
        private val AUTH_PROVIDERS = Arrays
            .stream(Provider.values())
            .map { obj: Provider -> obj.toString() }
            .collect(Collectors.toUnmodifiableSet())
    }
}