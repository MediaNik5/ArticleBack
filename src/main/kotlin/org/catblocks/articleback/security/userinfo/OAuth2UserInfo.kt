package org.catblocks.articleback.security.userinfo

abstract class OAuth2UserInfo {
    abstract val id: String
    abstract val name: String
    abstract val email: String
    abstract val imageUrl: String
}