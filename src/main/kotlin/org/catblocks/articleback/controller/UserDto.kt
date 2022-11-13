package org.catblocks.articleback.controller

import org.catblocks.articleback.model.User

fun User.toDto(): UserResponse {
    return UserResponse(
        id = id,
        username = username,
        email = email,
        avatar = imageUrl
    )
}

fun User.toShortDto(): ShortUserResponse {
    return ShortUserResponse(
        id = id,
        username = username,
        avatar = imageUrl
    )
}

data class UserResponse(
    var id: String,
    var username: String,
    var email: String,
    var avatar: String,
)

data class ShortUserResponse(
    var id: String,
    var username: String,
    var avatar: String,
)