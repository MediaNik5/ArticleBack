package org.catblocks.articleback.service

import org.catblocks.articleback.controller.*
import org.catblocks.articleback.model.AccessType
import org.catblocks.articleback.model.Article
import org.catblocks.articleback.model.ArticleAccess
import org.catblocks.articleback.repository.ArticleRepository
import org.catblocks.articleback.repository.UserRepository
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) {
    fun createArticle(userId: String, article: NewArticleRequest): Article {
        return articleRepository.save(
            Article(
                id = 0L,
                title = article.title,
                content = article.content,
                previewImage = article.previewImage,
                author = userRepository.getReferenceById(userId),
                created = LocalDateTime.now(),
                updated = LocalDateTime.now(),
                access = ArticleAccess(
                    id = 0L,
                    accessType = AccessType.PRIVATE,
                    users = listOf(),
                )
            )
        )
    }

    fun getArticles(
        creatorId: String?,
        dateFrom: LocalDateTime,
        dateTo: LocalDateTime,
        currentUserId: String?,
        sortBy: SortBy,
        sortDirection: Sort.Direction,
        page: Int,
        size: Int,
    ): PageableResponse<List<Article>> {
        val articles = articleRepository.findAll(
            if (creatorId != null) userRepository.getReferenceById(creatorId) else null,
            dateFrom,
            dateTo,
            sortBy,
            sortDirection,
            page,
            size,
        )

        val maxSize = articleRepository.countAllByAuthor(
            if (creatorId != null) userRepository.getReferenceById(creatorId) else null,
            dateFrom,
            dateTo,
        )

        return PageableResponse(articles.filter { article ->
            when (article.access.accessType) {
                AccessType.PUBLIC -> true
                AccessType.PRIVATE -> article.author.id == currentUserId
                AccessType.CUSTOM -> currentUserId != null && article.access.users.any { it.id == currentUserId }
            }
        }, maxSize)
    }

    fun getArticle(id: Long, userId: String? = null): Article {
        val article = articleRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        }
        if (article.author.id == userId) {
            return article
        }
        return when (article.access.accessType) {
            AccessType.PUBLIC -> article
            AccessType.CUSTOM -> if (userId !== null && article.access.users.any { it.id == userId }) {
                article
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
            }

            AccessType.PRIVATE -> throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        }
    }

    fun updateArticle(userId: String, id: Long, updatedArticle: UpdateArticleRequest) {
        val article = articleRepository.findByIdAndAuthor(id, userRepository.getReferenceById(userId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        articleRepository.save(
            article.copy(
                title = updatedArticle.title,
                content = updatedArticle.content,
                previewImage = updatedArticle.previewImage,
                updated = LocalDateTime.now(),
            )
        )
    }

    fun deleteArticle(userId: String, id: Long) {
        val article = articleRepository.findByIdAndAuthor(id, userRepository.getReferenceById(userId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        articleRepository.delete(article)
    }

    fun updateAccess(userId: String, id: Long, newAccess: UpdateArticleAccessRequest) {
        val article = articleRepository.findByIdAndAuthor(id, userRepository.getReferenceById(userId))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        article.access.accessType = newAccess.access
        article.access.users = newAccess.users.map { userRepository.getReferenceById(it) }
        articleRepository.save(article)
    }
}