package org.catblocks.articleback.repository

import org.catblocks.articleback.model.Article
import org.catblocks.articleback.model.ArticleReadProgress
import org.catblocks.articleback.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ArticleReadProgressRepository : JpaRepository<ArticleReadProgress, Long> {
    fun findByUserAndArticle(user: User, article: Article): ArticleReadProgress?

    @Query(
        """
        select
            avg(progress.progress) as averageProgress, 
            count(progress) as count, 
            avg(progress.duration) as averageDuration
        from ArticleReadProgress progress
        where progress.article = :article
    """
    )
    fun findProgressByArticle(article: Article): ArticleProgress

    fun findAllByArticle(article: Article): List<ArticleReadProgress>
}

data class ArticleProgress(
    val averageProgress: Int,
    val count: Long,
    val averageDuration: Long,
)