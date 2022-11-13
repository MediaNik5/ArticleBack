package org.catblocks.articleback.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.catblocks.articleback.security.UserPrincipal
import org.catblocks.articleback.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    fun getUser(@AuthenticationPrincipal user: UserPrincipal): UserResponse {
        return userService.getUser(user.id).toDto()
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ShortUserResponse {
        return userService.getUser(id).toShortDto()
    }
}