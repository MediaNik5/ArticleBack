package org.catblocks.articleback.model

import java.time.Duration
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "article_read_progresses")
class ArticleReadProgress(
    @Id
    var id: Long,
    @ManyToOne
    var article: Article,
    var progress: Int, // number from 1 to 100 (percent)
    var duration: Duration,
)