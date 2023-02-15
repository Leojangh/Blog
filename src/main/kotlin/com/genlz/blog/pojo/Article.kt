package com.genlz.blog.pojo

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.Instant

@Entity
data class Article(
    @Id @GeneratedValue
    override val id: Long,
    @CreatedDate
    override val createTime: Instant,
    @Transient
    override val modifyTime: MutableList<Instant>,
    override val lastModify: Instant = modifyTime.last(),
    val title: String,
    val abstract: String,
    val content: String,
    @ElementCollection
    @CollectionTable(joinColumns = [JoinColumn(name = "", referencedColumnName = "id")])
    @AttributeOverrides(AttributeOverride(name = "name", column = Column()))
    val tags: Set<Tag>,
    val category: Set<String>,
    val splash: String,
) : Persistable<Long>, TimeAware<Instant>

@Entity
data class Tag(
    @Id @GeneratedValue
    override val id: Long,
    val name: String,
    val count: Long
) : Persistable<Long>

interface Category


