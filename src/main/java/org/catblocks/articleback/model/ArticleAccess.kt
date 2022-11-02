package org.catblocks.articleback.model

import org.hibernate.Hibernate
import javax.persistence.*

@Table(name = "article_accesses")
@Entity
data class ArticleAccess(
    @Id
    var id: Long,
    @OneToOne(mappedBy = "access")
    var article: Article,
    var accessType: AccessType,
    @ManyToMany
    var users: List<User>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ArticleAccess

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , article = $article , accessType = $accessType )"
    }
}