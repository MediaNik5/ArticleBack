package org.catblocks.articleback

import org.catblocks.articleback.controller.NewArticleRequest
import org.catblocks.articleback.controller.SortBy
import org.catblocks.articleback.controller.UpdateArticleAccessRequest
import org.catblocks.articleback.controller.UpdateArticleRequest
import org.catblocks.articleback.model.*
import org.catblocks.articleback.repository.ArticleRepository
import org.catblocks.articleback.repository.UserRepository
import org.catblocks.articleback.service.ArticleService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import javax.transaction.Transactional
import kotlin.test.assertTrue

@SpringBootTest
class ArticleServiceTest @Autowired constructor(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) {
    private val articleService = ArticleService(articleRepository, userRepository)
    private val imaginaryUsers = listOf(
        User(
            "first_id",
            "MediaNik",
            "email@email.com",
            "url1",
            Provider.GOOGLE,
        ),
        User(
            "second_id",
            "AcediaExe",
            "acedia@email.com",
            "url2",
            Provider.GOOGLE,
        ),
    )
    private val imaginaryArticles = listOf(
        Article(
            1L,
            "Hello, title",
            "url3",
            imaginaryUsers[0],
            ArticleAccess(1L, AccessType.PRIVATE, listOf()),
            "Content of first article",
            LocalDateTime.now(),
            LocalDateTime.now(),
        ),
        Article(
            2L,
            "Hello, title 2",
            "url4",
            imaginaryUsers[0],
            ArticleAccess(2L, AccessType.PUBLIC, listOf()),
            "Content of second article",
            LocalDateTime.now(),
            LocalDateTime.now(),
        ),
        Article(
            3L,
            "Hello, title 3",
            "url5",
            imaginaryUsers[0],
            ArticleAccess(3L, AccessType.CUSTOM, listOf()),
            "Content of third article",
            LocalDateTime.now(),
            LocalDateTime.now(),
        ),
        Article(
            4L,
            "Hello, title 4",
            "url6",
            imaginaryUsers[0],
            ArticleAccess(4L, AccessType.CUSTOM, listOf(imaginaryUsers[1])),
            "Content of fourth article",
            LocalDateTime.now(),
            LocalDateTime.now(),
        ),
    )

    @BeforeEach
    fun initDb() {
        articleRepository.deleteAll()
        userRepository.deleteAll()
        userRepository.saveAll(imaginaryUsers)
        articleRepository.saveAll(imaginaryArticles)
    }

    @Test
    fun `given articles by user, when asked for all articles, then return complete articles list`() {
        val articles = articleService.getArticles(
            imaginaryUsers[0].id,
            LocalDateTime.MIN,
            LocalDateTime.MAX,
            imaginaryUsers[0].id,
            SortBy.CREATED,
            Sort.Direction.DESC,
            0,
            10,
        )
        assertArticlesEqualByContent(articles.content, imaginaryArticles)
    }

    private fun assertArticlesEqualByContent(articles: List<Article>, articles2: List<Article>) {
        for (article in articles) {
            if (articles2.none {
                    it.title == article.title &&
                            it.content == article.content &&
                            it.previewImage == article.previewImage &&
                            it.access.accessType == article.access.accessType
                }) {
                throw AssertionError("Expected $articles2, found $articles")
            }
        }
    }

    @Test
    fun `given created article, when asked for it by id, return created article`() {
        val expectedArticle = NewArticleRequest(
            "Title of newly created article",
            "Content of newly created article",
            "url4",
        )
        val (id) = articleService.createArticle(
            imaginaryUsers[0].id,
            expectedArticle
        )

        val actualArticle = articleService.getArticle(id, imaginaryUsers[0].id)

        assertEquals(actualArticle.title, expectedArticle.title)
        assertEquals(actualArticle.content, expectedArticle.content)
        assertEquals(actualArticle.previewImage, expectedArticle.previewImage)
    }

    @Test
    fun `given article, update should be more or equal to created`() {
        val all = articleRepository.findAll()
        for (article in all) {
            assert(article.updated >= article.created)
        }
    }

    @Test
    fun `given own article, when asked for it, return article`() {
        val expectedArticle = articleRepository.findAll().first()
        assertDoesNotThrow {
            articleService.getArticle(expectedArticle.id, imaginaryUsers[0].id)
        }
    }

    @Test
    fun `given unexisting article, when asked for it, throw exception`() {
        assertThrows(ResponseStatusException::class.java) {
            articleService.getArticle(-1L, null)
        }
    }

    @Test
    fun `given public article, when asked for article by id by unauthorized user, return it`() {
        val expectedArticle = articleRepository.findByAccess_AccessType(AccessType.PUBLIC).first()
        assertDoesNotThrow {
            articleService.getArticle(expectedArticle.id, null)
        }
    }

    @Test
    fun `given private article, when asked for article by id by unauthorized user, throw exception`() {
        val expectedArticle = articleRepository.findByAccess_AccessType(AccessType.PRIVATE).first()
        assertThrows(ResponseStatusException::class.java, {
            articleService.getArticle(expectedArticle.id, null)
        }, { "Article with id $id not found" })
    }

    @Test
    @Transactional
    fun `given custom article with one user allowed, when asked for article by id by unauthorized user, throw exception`() {
        val expectedArticle = articleRepository
            .findByAccess_AccessType(AccessType.CUSTOM).first { it.access.users.isEmpty() }
        assertThrows(ResponseStatusException::class.java, {
            articleService.getArticle(expectedArticle.id, null)
        }, { "Article with id $id not found" })
    }

    @Test
    @Transactional
    fun `given custom article with one user allowed, when asked for article by id by that user, return it`() {
        val expectedArticle = articleRepository
            .findByAccess_AccessType(AccessType.CUSTOM)
            .first { it.access.users.isNotEmpty() }
        assertDoesNotThrow {
            articleService.getArticle(expectedArticle.id, imaginaryUsers[1].id)
        }
    }

    @Test
    fun `given article, when update it, then changes have to be saved`() {
        val expectedArticle = articleRepository.findAll().first().copy(
            title = "New title for article",
            content = "new content for article",
            previewImage = "new image for article",
        )

        articleService.updateArticle(
            imaginaryUsers[0].id,
            expectedArticle.id,
            UpdateArticleRequest(expectedArticle.title, expectedArticle.content, expectedArticle.previewImage)
        )

        val actualArticle = articleService.getArticle(expectedArticle.id, imaginaryUsers[0].id)
        assertEquals(expectedArticle.title, actualArticle.title)
        assertEquals(expectedArticle.content, actualArticle.content)
        assertEquals(expectedArticle.previewImage, actualArticle.previewImage)

        assertTrue(actualArticle.updated > actualArticle.created)
    }

    @Test
    fun `given unexisting article, when asked to delete it, throw exception`() {
        assertThrows(ResponseStatusException::class.java, {
            articleService.deleteArticle(imaginaryUsers[0].id, -1L)
        }, "Article with id $id not found")
    }

    @Test
    fun `given article, when delete it and asked for it, throw exception`() {
        val article = articleRepository.findAll().first()
        articleService.deleteArticle(imaginaryUsers[0].id, article.id)
        assertThrows(ResponseStatusException::class.java, {
            articleService.deleteArticle(imaginaryUsers[0].id, article.id)
        }, { "Article with id $id not found" })
    }

    @Test
    @Transactional
    fun `given article, when changed its access, then changes save`() {
        val article = articleRepository.findByAccess_AccessType(AccessType.PUBLIC).first()
        articleService.updateAccess(
            imaginaryUsers[0].id,
            article.id,
            UpdateArticleAccessRequest(
                AccessType.PRIVATE,
                listOf(),
            )
        )

        val actualArticle = articleService.getArticle(article.id, imaginaryUsers[0].id)
        assertEquals(actualArticle.access.accessType, AccessType.PRIVATE)
        assertEquals(actualArticle.access.users, listOf<User>())
    }

    @Test
    fun `given nonexisting article, when change its access, then throw exception`() {
        assertThrows(ResponseStatusException::class.java) {
            articleService.updateAccess(
                imaginaryUsers[0].id,
                -1L,
                UpdateArticleAccessRequest(
                    AccessType.PRIVATE,
                    listOf(),
                )
            )
        }
    }
}