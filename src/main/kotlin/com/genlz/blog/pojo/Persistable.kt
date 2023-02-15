package com.genlz.blog.pojo

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import java.io.Serializable
import java.time.Instant

@MappedSuperclass
interface Persistable<Key : Serializable> {
    @get:Id
    @get:GeneratedValue
    val id: Key
}

@MappedSuperclass
interface TimeAware<Time : Serializable> {
    val createTime: Time
    val lastModify: Time

    @get:Transient
    val modifyTime: MutableList<Time>
}

data class TimeFieldDelegate(
    override val createTime: Instant,
    override val modifyTime: MutableList<Instant>,
    override val lastModify: Instant = modifyTime.last(),
) : TimeAware<Instant>
