package org.catblocks.articleback.model

import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "articles")
@Entity
data class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var title: String,
    var previewImage: String,
    @ManyToOne
    var author: User,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, optional = false)
    var access: ArticleAccess,
    @Column(columnDefinition = "TEXT")
    var content: String,
    var created: LocalDateTime,
    var updated: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Article

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "Article(id=$id, title='$title', access=$access, content='$content')"
    }
}