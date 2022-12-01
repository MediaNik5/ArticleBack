package org.catblocks.articleback

import org.catblocks.articleback.model.User
import org.catblocks.articleback.repository.UserRepository
import org.catblocks.articleback.security.Provider
import org.catblocks.articleback.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
) {
    val userService = UserService(userRepository)

    val imaginaryUser = User(
        "first_id",
        "MediaNik",
        "email@email.com",
        "url1",
        Provider.GOOGLE,
    )

    @BeforeEach
    fun initDb() {
        userRepository.deleteAll()
        userRepository.save(imaginaryUser)
    }
    @Test
    fun `given user, when asked for some user by id, then return correct user`() {
        val user = userService.getUser(imaginaryUser.id)

        assertUserEqualByContent(user, imaginaryUser)
    }

    private fun assertUserEqualByContent(user1: User, user2: User) {
        if (user1.id != user2.id &&
            user1.email != user2.email &&
            user1.username != user2.username &&
            user1.provider != user2.provider &&
            user1.imageUrl != user2.imageUrl
        ) {
            throw AssertionError("Expected $user1, found $user2")
        }
    }

    @Test
    fun `given unexisting user with id, when asked get it by id, throw exception`() {
        Assertions.assertThrows(ResponseStatusException::class.java, {
            userService.getUser(imaginaryUser.id + 1)
        }, "User with id ${imaginaryUser.id} not found")
    }
}
