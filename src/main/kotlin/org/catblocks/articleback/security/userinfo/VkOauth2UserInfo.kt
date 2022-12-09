package org.catblocks.articleback.security.userinfo

class VkOauth2UserInfo(private val attributes: Map<String, Any?>) : OAuth2UserInfo() {
    override val id: String
        get() = (attributes["id"] as Int).toString()
    override val name: String
        get() = attributes["first_name"].toString() + " " + attributes["last_name"].toString()
    override val email: String
        get() = attributes["email"].toString()
    override val imageUrl: String
        get() = attributes["photo_max_orig"].toString()
}