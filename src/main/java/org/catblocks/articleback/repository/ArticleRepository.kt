package org.catblocks.articleback.repository

import org.catblocks.articleback.controller.SortBy
import org.catblocks.articleback.model.Article
import org.catblocks.articleback.model.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByIdAndAuthor(id: Long, author: User): Article?
    fun findAll(
        author: User,
        sortBy: SortBy,
        sortDirection: Sort.Direction,
        page: Int,
        size: Int,
    ): List<Article> {
        return findAllByAuthor(
            author,
            PageRequest.of(
                page,
                size,
                Sort.by(
                    sortDirection,
                    sortBy.name.lowercase(Locale.getDefault())
                )
            ),
        )
    }

    @Query("SELECT a FROM Article a WHERE a.author = :author")
    fun findAllByAuthor(
        author: User,
        pageable: Pageable,
    ): List<Article>
}