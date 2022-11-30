package org.catblocks.articleback.service

import org.catblocks.articleback.controller.ReactionRequest
import org.catblocks.articleback.controller.ReadProgressRequest
import org.catblocks.articleback.controller.ReadProgressResponse
import org.catblocks.articleback.controller.toDto
import org.catblocks.articleback.model.ArticleReadProgress
import org.catblocks.articleback.repository.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Duration


@Service
class StatisticsService(
    private val readProgressRepository: ArticleReadProgressRepository,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val articleReactionRepository: ArticleReactionRepository,
    private val articleService: ArticleService,
) {
    fun setReadProgress(
        userId: String,
        articleId: Long,
        readProgressRequest: ReadProgressRequest,
    ) {
        val user = userRepository.getReferenceById(userId)
        val article = articleRepository.getReferenceById(articleId)
        val readProgress = readProgressRepository.findByUserAndArticle(user, article)
            ?: ArticleReadProgress(
                0,
                article,
                user
            )
        readProgress.progress = readProgressRequest.readProgress
        readProgress.duration = Duration.ofMillis(readProgressRequest.duration)
        readProgressRepository.save(readProgress)
    }

    fun getReadProgress(userId: String, articleId: Long): ArticleReadProgress {
        val user = userRepository.getReferenceById(userId)
        val article = articleRepository.getReferenceById(articleId)
        return readProgressRepository.findByUserAndArticle(user, article)
            ?: ArticleReadProgress(
                0,
                article,
                user
            )
    }

    fun getReadProgresses(userId: String, articleId: Long, includeEach: Boolean): ReadProgressResponse {
        val user = userRepository.getReferenceById(userId)

        val article = articleRepository.findByIdAndAuthor(articleId, user)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found")

        val commonReadProgress = readProgressRepository.findProgressByArticle(article)

        return if (includeEach) {
            ReadProgressResponse(
                commonReadProgress.averageProgress,
                commonReadProgress.count,
                commonReadProgress.averageDuration,
                listOf()
            )
        } else {
            ReadProgressResponse(
                commonReadProgress.averageProgress,
                commonReadProgress.count,
                commonReadProgress.averageDuration,
                readProgressRepository.findAllByArticle(article).toDto()
            )
        }
    }

    fun setReaction(userId: String, articleId: Long, reactionRequest: ReactionRequest) {
        articleService.getArticle(userId, articleId)
        articleRepository.findById(articleId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found") }
        articleReactionRepository.set(userId, articleId, reactionRequest.reaction)
    }

    fun getReaction(userId: String, articleId: Long): ReactionRequest {
        val user = userRepository.getReferenceById(userId)
        val article = articleService.getArticle(userId, articleId)
        val reaction = articleReactionRepository.findByUserAndArticle(user, article)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found")
        return ReactionRequest(reaction.reactionType)
    }

    fun deleteReaction(userId: String, articleId: Long) {
        val user = userRepository.getReferenceById(userId)
        val article = articleService.getArticle(userId, articleId)
        articleReactionRepository.deleteByUserAndArticle(user, article)
    }

    fun getReactions(userId: String, articleId: Long): List<ReactionCount> {
        val article = articleService.getArticle(userId, articleId)
        return articleReactionRepository.findByArticle(article)
    }
}
