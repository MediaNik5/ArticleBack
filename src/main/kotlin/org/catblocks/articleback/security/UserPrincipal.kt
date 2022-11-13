package org.catblocks.articleback.security

import org.catblocks.articleback.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class UserPrincipal : OAuth2User, UserDetails {
    val id: String
    private val username: String
    private val email: String
    private val authorities: Collection<GrantedAuthority>
    private val attributes: Map<String, Any>

    constructor(
        id: String,
        username: String,
        email: String,
        authorities: Collection<GrantedAuthority>,
        attributes: Map<String, Any>,
    ) {
        this.id = id
        this.username = username
        this.email = email
        this.authorities = authorities
        this.attributes = attributes
    }

    constructor(
        id: String,
        username: String,
        email: String,
        authorities: Collection<GrantedAuthority>,
    ) {
        this.id = id
        this.username = username
        this.email = email
        this.authorities = authorities
        attributes = java.util.Map.of()
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return id
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getName(): String {
        return id
    }

    companion object {
        private val roleUser = SimpleGrantedAuthority("ROLE_USER")
        fun create(user: User): UserPrincipal {
            return UserPrincipal(
                user.id,
                user.username,
                user.email,
                requireRoleUser(null)
            )
        }

        fun create(user: User, attributes: Map<String, Any>): UserPrincipal {
            return UserPrincipal(
                user.id,
                user.username,
                user.email,
                requireRoleUser(null),
                attributes
            )
        }

        private fun requireRoleUser(authorities: MutableList<GrantedAuthority>?): List<GrantedAuthority> {
            var authorities = authorities
            if (authorities == null) authorities = ArrayList(1)
            if (!authorities.contains(roleUser)) authorities.add(roleUser)
            return authorities
        }
    }
}