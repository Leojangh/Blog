package com.genlz.blog.service

import org.springframework.data.repository.Repository

interface BaseService<T, ID, Repo : Repository<out T, in ID>> {
    val repository: Repo
}