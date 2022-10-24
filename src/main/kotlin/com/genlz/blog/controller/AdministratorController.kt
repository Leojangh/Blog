package com.genlz.blog.controller

import com.genlz.blog.service.AdministratorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdministratorController(
    private val service: AdministratorService
) {

    @GetMapping("/admin")
    suspend fun test(): String {
        return "Admin"
    }
}