package org.catblocks.articleback.model

import javax.persistence.*

@Entity
@Table(name = "article_reactions")
class ArticleReaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @ManyToOne
    var article: Article,
    @ManyToOne
    var user: User,
    var reactionType: Int,
)