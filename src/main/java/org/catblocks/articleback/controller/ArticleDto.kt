package org.catblocks.articleback.controller

import org.catblocks.articleback.model.AccessType
import org.catblocks.articleback.model.Article
import java.time.ZoneOffset


fun List<Article>.toDto(page: Int, size: Int): ArticlesResponse {
    return ArticlesResponse(
        map {
            ShortArticleResponse(
                id = it.id,
                title = it.title,
                previewImage = it.previewImage,
                creatorId = it.author.username,
                created = it.created.toInstant(ZoneOffset.UTC).toEpochMilli(),
                updated = it.updated.toInstant(ZoneOffset.UTC).toEpochMilli(),
            )
        },
        page,
        size,
    )
}

fun Article.toDto(): ArticleResponse {
    return ArticleResponse(
        id = id,
        title = title,
        content = content,
        previewImage = previewImage,
        creatorId = author.username,
        created = created.toInstant(ZoneOffset.UTC).toEpochMilli(),
        updated = updated.toInstant(ZoneOffset.UTC).toEpochMilli(),
    )
}

data class ArticlesResponse(
    val articles: List<ShortArticleResponse>,
    val page: Int,
    val size: Int,
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
