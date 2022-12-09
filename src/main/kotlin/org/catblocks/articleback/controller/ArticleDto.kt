package org.catblocks.articleback.controller

import org.catblocks.articleback.model.AccessType
import org.catblocks.articleback.model.Article
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.contracts.ExperimentalContracts


@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
fun Long?.toLocalDateTime(ifNull: LocalDateTime): LocalDateTime {
    if (this == null) {
        return ifNull
    }
    val instant = Instant.ofEpochMilli(this)
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
}

fun List<Article>.toDto(page: Int, maxSize: Int): ArticlesResponse {
    return ArticlesResponse(
        map {
            ShortArticleResponse(
                id = it.id,
                title = it.title,
                previewImage = it.previewImage,
                creatorId = it.author.id,
                created = it.created.toInstant(ZoneOffset.UTC).toEpochMilli(),
                updated = it.updated.toInstant(ZoneOffset.UTC).toEpochMilli(),
            )
        },
        page,
        maxSize,
    )
}

data class PageableResponse<T>(
    val content: T,
    val maxSize: Int,
)

fun Article.toDto(): ArticleResponse {
    return ArticleResponse(
        id = id,
        title = title,
        content = content,
        previewImage = previewImage,
        creatorId = author.id,
        created = created.toInstant(ZoneOffset.UTC).toEpochMilli(),
        updated = updated.toInstant(ZoneOffset.UTC).toEpochMilli(),
    )
}

data class ArticlesResponse(
    val articles: List<ShortArticleResponse>,
    val page: Int,
    val maxSize: Int,
)

data class ShortArticleResponse(
    val id: Long,
    val title: String,
    val previewImage: String,
    val creatorId: String,
    val created: Long,
    val updated: Long,
)

data class ArticleResponse(
    val id: Long,
    val title: String,
    val content: String,
    val previewImage: String,
    val creatorId: String,
    val created: Long,
    val updated: Long,
)

data class UpdateArticleRequest(
    val title: String,
    val content: String,
    val previewImage: String,
)

data class UpdateArticleAccessRequest(
    val access: AccessType,
    val users: List<String>,
)

enum class SortBy {
    TITLE,
    CREATED,
    UPDATED,
}

data class NewArticleRequest(
    val title: String,
    val content: String,
    val previewImage: String,
)
