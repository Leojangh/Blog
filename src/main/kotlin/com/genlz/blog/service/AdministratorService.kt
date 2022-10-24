package com.genlz.blog.service

import com.genlz.blog.pojo.Administrator
import com.genlz.blog.pojo.User
import com.genlz.blog.repository.AdministratorRepository
import org.springframework.stereotype.Service

interface AdministratorService : BaseService<User, Long, AdministratorRepository> {

    suspend fun findAdminById(id: Long): Administrator?
}

@Service
internal class AdministratorServiceImpl(
    override val repository: AdministratorRepository
) : AdministratorService {

    override suspend fun findAdminById(id: Long): Administrator? {
        return repository.findById(id)
    }
}

