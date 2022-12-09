package org.catblocks.articleback.security.extractor

import org.catblocks.articleback.model.Provider
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
class VkUserInfoExtractor : UserInfoExtractor {
    private val vk: String = Provider.VK.toString().lowercase(Locale.getDefault())

    override fun canProceed(registrationId: String): Boolean {
        return vk == registrationId
    }

    override fun extract(map: Map<String, Any?>, userRequest: OAuth2UserRequest): Map<String, Any?> {
        val attributes = (map["response"] as List<MutableMap<String, Any?>>)[0]
        attributes["email"] = userRequest.additionalParameters["email"]
        return attributes
    }
}