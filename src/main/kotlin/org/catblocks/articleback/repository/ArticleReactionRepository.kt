package org.catblocks.articleback.repository

import org.catblocks.articleback.model.Article
import org.catblocks.articleback.model.ArticleReaction
import org.catblocks.articleback.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ArticleReactionRepository : JpaRepository<ArticleReaction, Long> {

    @Modifying
    @Query(
        value = """
        insert into article_reactions (article_id, user_id, reaction_type)
        values (:articleId, :userId, :reactionType) on conflict (article_id, user_id) do update
        set reaction_type = :reactionType
    """, nativeQuery = true
    )
    fun set(userId: String, articleId: Long, reactionType: Int): ArticleReaction?

    fun findByUserAndArticle(user: User, article: Article): ArticleReaction?

    fun deleteByUserAndArticle(user: User, article: Article): Int

    @Query(
        """
        select
            reaction.reactionType as reactionType,
            count(reaction) as count
        from ArticleReaction reaction
        where reaction.article = :article
        group by reaction.reactionType
    """
    )
    fun findByArticle(article: Article): List<ReactionCount>
}

data class ReactionCount(
    val reactionType: Int,
    val count: Long,
)