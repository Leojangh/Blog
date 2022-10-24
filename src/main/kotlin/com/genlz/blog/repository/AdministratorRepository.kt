package com.genlz.blog.repository

import com.genlz.blog.pojo.Administrator
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AdministratorRepository : CoroutineCrudRepository<Administrator, Long>