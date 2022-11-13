package org.catblocks.articleback.security.userinfo

import org.catblocks.articleback.repository.UserRepository
import org.catblocks.articleback.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class PersonDetailsServiceImpl(
    @param:Autowired private val userRepository: UserRepository,
) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository
            .findById(username)
            .map { person -> UserPrincipal.create(person) }
            .orElseThrow { UsernameNotFoundException(username) }
    }
}