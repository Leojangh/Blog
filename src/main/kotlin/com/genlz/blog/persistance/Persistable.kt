package com.genlz.blog.persistance

import java.io.Serializable
import java.time.Instant

interface Persistable<Key : Serializable, Time : Serializable> : TimeAware<Time> {
    val id: Key
}

interface TimeAware<Time : Serializable> {
    val createTime: Time
    val modifyTime: MutableList<Time>
}

data class TimeFieldDelegate(
    override val createTime: Instant,
    override val modifyTime: MutableList<Instant>,
) : TimeAware<Instant>
