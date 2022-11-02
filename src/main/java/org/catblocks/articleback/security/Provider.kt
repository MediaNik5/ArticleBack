package org.catblocks.articleback.security

import org.catblocks.articleback.security.userinfo.GoogleOAuth2UserInfo
import org.catblocks.articleback.security.userinfo.OAuth2UserInfo
import java.util.function.Function

enum class Provider(converter: Function<Map<String, Any?>, OAuth2UserInfo>) {
    GOOGLE({ GoogleOAuth2UserInfo(it) } );

    private val converter: Function<Map<String, Any?>, OAuth2UserInfo>

    init {
        this.converter = converter
    }

    fun convert(map: Map<String, Any?>): OAuth2UserInfo {
        return converter.apply(map)
    }
}
