package org.catblocks.articleback.model

import org.catblocks.articleback.security.userinfo.GoogleOAuth2UserInfo
import org.catblocks.articleback.security.userinfo.OAuth2UserInfo
import org.catblocks.articleback.security.userinfo.VkOauth2UserInfo
import java.util.function.Function

enum class Provider(converter: Function<Map<String, Any?>, OAuth2UserInfo>) {
    GOOGLE({ GoogleOAuth2UserInfo(it) }),
    VK({ VkOauth2UserInfo(it) });

    private val converter: Function<Map<String, Any?>, OAuth2UserInfo>

    init {
        this.converter = converter
    }

    fun convert(map: Map<String, Any?>): OAuth2UserInfo {
        return converter.apply(map)
    }
}
