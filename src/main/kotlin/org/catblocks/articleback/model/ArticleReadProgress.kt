package org.catblocks.articleback.model

import java.time.Duration
import javax.persistence.*

@Entity
@Table(name = "article_read_progresses")
class ArticleReadProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @ManyToOne
    var article: Article,
    @ManyToOne
    val user: User,
    var progress: Int = 0, // number from 1 to 100 (percent)
    var duration: Duration = Duration.ZERO,
)