package org.catblocks.articleback.controller

import org.catblocks.articleback.model.ArticleReadProgress

data class ReadProgressRequest(
    val readProgress: Int,
    val duration: Long,
)

data class ReadProgressResponse(
    val readProgress: Int,
    val readCount: Long,
    val duration: Long,
    val readProgresses: List<ReadProgressRequest>,
)

data class ReactionRequest(
    val reaction: Int,
)

fun ArticleReadProgress.toDto() = ReadProgressRequest(
    readProgress = progress,
    duration = duration.toMillis(),
)


fun List<ArticleReadProgress>.toDto(): List<ReadProgressRequest> {
    return this.map { it.toDto() }
}