package org.catblocks.articleback.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "article_reactions")
class ArticleReaction(
    @Id
    var id: Long,
    @ManyToOne
    var article: Article,
    @ManyToOne
    var user: User,
    var reactionType: Int,
)