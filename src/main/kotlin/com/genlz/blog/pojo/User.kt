package com.genlz.blog.pojo

import com.genlz.blog.persistance.Persistable
import com.genlz.blog.persistance.TimeAware
import com.genlz.blog.persistance.TimeFieldDelegate
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import java.time.Instant


sealed interface User {
    val username: Username
    val email: Email
}

sealed interface Guest : User {
    companion object {
        operator fun invoke(
            username: String, email: String
        ): Guest = GuestImpl(Username(username), Email(email))
    }
}

sealed interface AuthorizedUser : User, Persistable<Long, Instant> {
    override val id: Long
    val registerTime: Instant

    companion object {
        operator fun invoke(
            id: Long,
            username: String,
            email: String,
            createTime: Instant = Instant.now(),
            modifyTime: MutableList<Instant> = mutableListOf(createTime),
        ): AuthorizedUser = AuthorizedUserImpl(
            id, Email(email), Username(username), TimeFieldDelegate(createTime, modifyTime)
        )
    }
}

sealed interface Administrator : AuthorizedUser {

    companion object {
        operator fun invoke(
            id: Long,
            username: String,
            email: String,
            createTime: Instant = Instant.now(),
            modifyTime: MutableList<Instant> = mutableListOf(createTime),
        ): Administrator = AdministratorImpl(
            id, Username(username), Email(email), TimeFieldDelegate(createTime, modifyTime)
        )
    }
}

@Serializable
@JvmInline
value class Email(val address: String) {

    companion object {
        val REGEX = Regex("""^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$""")
    }

    init {
        require(REGEX.matches(address)) {
            "Can't recognize the E-mail address:$address"
        }
    }
}

@Serializable
@JvmInline
value class Username(val value: String) {

    init {
        require(illegalNameFilter(value)) {
            "Illegal username."
        }
    }

    companion object {
        val illegalNameFilter: (String) -> Boolean = { it.isNotBlank() }
    }
}

private data class GuestImpl(
    override val username: Username,
    override val email: Email,
) : Guest

private data class AuthorizedUserImpl(
    override val id: Long,
    override val email: Email,
    override val username: Username,
    val times: TimeFieldDelegate,
) : AuthorizedUser, TimeAware<Instant> by times {
    override val registerTime by ::createTime
}

//@Entity
private data class AdministratorImpl(
//    @Id @GeneratedValue
    override val id: Long,
    override val username: Username,
    override val email: Email,
    val times: TimeFieldDelegate,
) : Administrator, TimeAware<Instant> by times {
    override val registerTime by ::createTime
}