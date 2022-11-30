package org.catblocks.articleback.controller

import org.catblocks.articleback.repository.ReactionCount
import org.catblocks.articleback.security.UserPrincipal
import org.catblocks.articleback.service.StatisticsService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article-statistics")
class ArticleStatisticsController(
    private val statisticsService: StatisticsService,
) {
    @PostMapping("/{articleId}/read-progress")
    fun setReadProgress(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
        @RequestBody readProgressRequest: ReadProgressRequest,
    ) {
        return statisticsService.setReadProgress(
            user.id,
            articleId,
            readProgressRequest,
        )
    }

    @GetMapping("/{articleId}/read-progress-self")
    fun getReadProgress(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
    ): ReadProgressRequest {
        return statisticsService.getReadProgress(
            user.id,
            articleId,
        ).toDto()
    }

    @GetMapping("/{articleId}/read-progress")
    fun getReadProgresses(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
        @RequestParam includeEach: Boolean,
    ): ReadProgressResponse {
        return statisticsService.getReadProgresses(
            user.id,
            articleId,
            includeEach
        )
    }

    @PostMapping("/{articleId}/reaction")
    fun setReaction(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
        @RequestBody reactionRequest: ReactionRequest,
    ) {
        return statisticsService.setReaction(
            user.id,
            articleId,
            reactionRequest,
        )
    }

    @GetMapping("/{articleId}/reaction")
    fun getReaction(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
    ): ReactionRequest {
        return statisticsService.getReaction(
            user.id,
            articleId,
        )
    }

    @DeleteMapping("/{articleId}/reaction")
    fun deleteReaction(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
    ) {
        return statisticsService.deleteReaction(
            user.id,
            articleId,
        )
    }

    @GetMapping("/{articleId}/reactions")
    fun getReactions(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable articleId: Long,
    ): List<ReactionCount> {
        return statisticsService.getReactions(
            user.id,
            articleId,
        )
    }
}