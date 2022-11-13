package org.catblocks.articleback.security.userinfo

class GoogleOAuth2UserInfo(private val attributes: Map<String, Any?>) : OAuth2UserInfo() {

    override val id: String
        get() = attributes["sub"] as String
    override val name: String
        get() = attributes["name"] as String
    override val email: String
        get() = attributes["email"] as String
    override val imageUrl: String
        get() = attributes["picture"] as String

    override fun toString(): String {
        return "Id: $id, email: $email"
    }
}