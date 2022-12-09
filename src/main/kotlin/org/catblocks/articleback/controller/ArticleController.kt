package org.catblocks.articleback.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.catblocks.articleback.security.UserPrincipal
import org.catblocks.articleback.service.ArticleService
import org.springframework.data.domain.Sort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService,
) {
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    fun createArticle(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody article: NewArticleRequest,
    ) : ArticleResponse {
        return articleService.createArticle(user.id, article).toDto()
    }

    @Suppress("NAME_SHADOWING")
    @GetMapping
    fun getArticles(
        @AuthenticationPrincipal user: UserPrincipal?,
        @RequestParam(required = false) creatorId: String?,
        @RequestParam(required = false) dateFromMillis: Long?,
        @RequestParam(required = false) dateToMillis: Long?,
        @RequestParam(required = false) sortBy: SortBy?,
        @RequestParam(required = false) sortDirection: Sort.Direction?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
    ): ArticlesResponse {
        val page = page ?: 0
        val size = size ?: 10
        val dateFrom = dateFromMillis.toLocalDateTime(ifNull = LocalDateTime.MIN)
        val dateTo = dateToMillis.toLocalDateTime(ifNull = LocalDateTime.MAX)
        val articles = articleService.getArticles(
            creatorId,
            dateFrom,
            dateTo,
            user?.id,
            sortBy ?: SortBy.CREATED,
            sortDirection ?: Sort.Direction.DESC,
            page,
            size,
        )

        return articles.toDto(page, size)
    }

    @GetMapping("/{id}")
    fun getArticle(
        @AuthenticationPrincipal user: UserPrincipal?,
        @PathVariable id: Long,
    ): ArticleResponse {
        return articleService.getArticle(id, user?.id).toDto()
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    fun updateArticle(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody article: UpdateArticleRequest,
    ) {
        articleService.updateArticle(user.id, id, article)
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    fun deleteArticle(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
    ) {
        articleService.deleteArticle(user.id, id)
    }

    @PutMapping("/{id}/access")
    @SecurityRequirement(name = "Bearer Authentication")
    fun changeAccess(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody newAccess: UpdateArticleAccessRequest,
    ) {
        articleService.updateAccess(user.id, id, newAccess)
    }
}