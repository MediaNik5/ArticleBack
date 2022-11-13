package org.catblocks.articleback

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArticleBackApplication

fun main(args: Array<String>) {
    runApplication<ArticleBackApplication>(*args);
}