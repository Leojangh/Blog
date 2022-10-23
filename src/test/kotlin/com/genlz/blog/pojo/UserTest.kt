package com.genlz.blog.pojo

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class UserTest {

    @Autowired
    private lateinit var gson: Gson

    private val administrator = Administrator(2, "xuxian", "lz@genlz.com")
    private val guest = Guest("zhangsan", "zhangsan@genlz.com")
    private val au = AuthorizedUser(1, "xuxian", "lz@genlz.com")

    @Test
    fun testGsonSerialize() {
        administrator.modifyTime.add(Instant.now())
        println(gson.toJson(administrator))
    }

    @Test
    fun testSerializeAndDeserialize() {
        val json = gson.toJson(administrator)
        val de = gson.fromJson(json, Administrator::class.java)
        val auJson = gson.toJson(au)
        val guestJson = gson.toJson(guest)
        assertEquals(guest, gson.fromJson(guestJson, Guest::class.java))
        assertEquals(au, gson.fromJson(auJson, AuthorizedUser::class.java))
        assertEquals(administrator, de)
    }
}
