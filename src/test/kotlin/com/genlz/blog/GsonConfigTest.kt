package com.genlz.blog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
internal class GsonConfigTest {

    @Autowired
    private lateinit var gsonTypeAdapters: Collection<*>

    @Test
    fun provideGsonTypeAdapters() {
        println(gsonTypeAdapters)
        assert(gsonTypeAdapters.isNotEmpty())
    }
}