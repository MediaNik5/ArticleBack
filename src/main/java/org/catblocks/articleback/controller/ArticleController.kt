package org.catblocks.articleback.controller

import org.catblocks.articleback.security.UserPrincipal
import org.catblocks.articleback.service.ArticleService
import org.springframework.data.domain.Sort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService,
) {
    @PostMapping
    fun createArticle(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody article: NewArticleRequest,
    ) : ArticleResponse {
        return articleService.createArticle(user, article).toDto()
    }

    @GetMapping
    fun getArticles(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestParam(required = false) sortBy: SortBy?,
        @RequestParam(required = false) sortDirection: Sort.Direction?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
    ): ArticlesResponse {
        val articles = articleService.getArticles(
            user,
            sortBy ?: SortBy.CREATED,
            sortDirection ?: Sort.Direction.DESC,
            page ?: 0,
            size ?: 10,
        )

        return articles.toDto(page ?: 0, size ?: 10)
    }

    @GetMapping("/{id}")
    fun getArticle(
        @AuthenticationPrincipal user: UserPrincipal?,
        @PathVariable id: Long,
    ): ArticleResponse {
        return articleService.getArticle(user, id).toDto()
    }

    @PutMapping("/{id}")
    fun updateArticle(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody article: UpdateArticleRequest,
    ) {
        articleService.updateArticle(user, id, article)
    }

    @DeleteMapping("/{id}")
    fun deleteArticle(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
    ) {
        articleService.deleteArticle(user, id)
    }

    @PutMapping("/{id}/access")
    fun changeAccess(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody newAccess: UpdateArticleAccessRequest,
    ) {
        articleService.updateAccess(user, id, newAccess)
    }
}