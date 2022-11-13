package org.catblocks.articleback.service

import org.catblocks.articleback.controller.NewArticleRequest
import org.catblocks.articleback.controller.SortBy
import org.catblocks.articleback.controller.UpdateArticleAccessRequest
import org.catblocks.articleback.controller.UpdateArticleRequest
import org.catblocks.articleback.model.AccessType
import org.catblocks.articleback.model.Article
import org.catblocks.articleback.model.ArticleAccess
import org.catblocks.articleback.repository.ArticleRepository
import org.catblocks.articleback.repository.UserRepository
import org.catblocks.articleback.security.UserPrincipal
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
    fun createArticle(user: UserPrincipal, article: NewArticleRequest): Article {
        return articleRepository.save(
            Article(
                id = 0L,
                title = article.title,
                content = article.content,
                previewImage = article.previewImage,
                author = userRepository.getReferenceById(user.id),
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
        user: UserPrincipal,
        sortBy: SortBy,
        sortDirection: Sort.Direction,
        page: Int,
        size: Int,
    ): List<Article> {
        return articleRepository.findAll(
            userRepository.getReferenceById(user.id),
            sortBy,
            sortDirection,
            page,
            size,
        )
    }

    fun getArticle(user: UserPrincipal?, id: Long): Article {
        val article = articleRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        }
        if (article.author.id == user?.id) {
            return article
        }
        return when (article.access.accessType) {
            AccessType.PUBLIC -> article
            AccessType.CUSTOM -> if (user !== null && article.access.users.any { it.id == user.id }) {
                article
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
            }
            AccessType.PRIVATE -> throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        }
    }

    fun updateArticle(user: UserPrincipal, id: Long, updatedArticle: UpdateArticleRequest) {
        val article = articleRepository.findByIdAndAuthor(id, userRepository.getReferenceById(user.id))
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

    fun deleteArticle(user: UserPrincipal, id: Long) {
        val article = articleRepository.findByIdAndAuthor(id, userRepository.getReferenceById(user.id))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        articleRepository.delete(article)
    }

    fun updateAccess(user: UserPrincipal, id: Long, newAccess: UpdateArticleAccessRequest) {
        val article = articleRepository.findByIdAndAuthor(id, userRepository.getReferenceById(user.id))
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id $id not found")
        article.access.accessType = newAccess.access
        article.access.users = newAccess.users.map { userRepository.getReferenceById(it) }
        articleRepository.save(article)
    }
}