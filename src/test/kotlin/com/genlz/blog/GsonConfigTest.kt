package com.genlz.blog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test


@SpringBootTest
internal class GsonConfigTest(
    @Autowired
    @Qualifier("gsonAdapters")
    private val gsonAdapters: Collection<*>
) {

    @Test
    fun provideGsonTypeAdapters() {
        println(gsonAdapters)
        assert(gsonAdapters.isNotEmpty())
    }
}