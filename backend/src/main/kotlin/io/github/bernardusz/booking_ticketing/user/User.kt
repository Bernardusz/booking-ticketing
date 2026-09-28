package io.github.bernardusz.booking_ticketing.user

import io.github.bernardusz.booking_ticketing.user.dto.UserInformation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy

enum class Role {
    ROLE_USER,
    ROLE_ADMIN
}

@Entity
@Table(
    name = "users"
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.ROLE_USER,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as User

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, username='$username')"
    }

    fun toUserInformation(): UserInformation = UserInformation(
        id = this.id,
        username = this.username,
        email = this.email,
        role = this.role
    )
}