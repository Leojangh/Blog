package com.genlz.blog.persistance

import java.time.Instant

interface Persistent<Key, Time> : TimeAware<Time> {
    val id: Key
}

interface TimeAware<Time> {
    val createTime: Time
    val modifyTime: MutableList<Time>
}

data class TimeFieldDelegate(
    override val createTime: Instant,
    override val modifyTime: MutableList<Instant>,
) : TimeAware<Instant>
