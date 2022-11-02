package org.catblocks.articleback.service

import org.catblocks.articleback.model.User
import org.catblocks.articleback.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun getUser(id: String): User {
        return userRepository.findById(id).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User with id ${id} not found")
        }
    }
}