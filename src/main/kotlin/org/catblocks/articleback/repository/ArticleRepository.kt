@file:Suppress("FunctionName")

package org.catblocks.articleback.repository

import org.catblocks.articleback.controller.SortBy
import org.catblocks.articleback.model.AccessType
import org.catblocks.articleback.model.Article
import org.catblocks.articleback.model.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface ArticleRepository : JpaRepository<Article, Long> {

    fun findByAccess_AccessType(accessType: AccessType): List<Article>

    fun findByIdAndAuthor(id: Long, author: User): Article?
    fun findAll(
        author: User?,
        dateFrom: LocalDateTime,
        dateTo: LocalDateTime,
        sortBy: SortBy,
        sortDirection: Sort.Direction,
        page: Int,
        size: Int,
    ): List<Article> {
        return findAllByAuthor(
            author,
            dateFrom,
            dateTo,
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

    @Query(
        """
        SELECT a FROM Article a 
            WHERE (:author is null or a.author = :author) and
            a.created >= :dateFrom and
            a.created <= :dateTo
        """
    )
    fun findAllByAuthor(
        author: User?,
        dateFrom: LocalDateTime,
        dateTo: LocalDateTime,
        pageable: Pageable,
    ): List<Article>

    @Query(
        """
        SELECT count(a) FROM Article a 
            WHERE (:author is null or a.author = :author) and
            a.created >= :dateFrom and
            a.created <= :dateTo
        """
    )
    fun countAllByAuthor(
        author: User?,
        dateFrom: LocalDateTime,
        dateTo: LocalDateTime,
    ): Int
}