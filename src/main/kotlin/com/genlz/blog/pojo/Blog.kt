package com.genlz.blog.pojo

import com.genlz.blog.persistance.TimeAware
import java.time.Instant

data class Blog(
    val id: Long,
    val times: TimeAware<Instant>,

    val author: User,
    val title: String,
    val abstract: String,
    val content: String,
    val tags: Set<String>,
    val category: Set<Category>,
    val splash: String
) : TimeAware<Instant> by times

interface Category


