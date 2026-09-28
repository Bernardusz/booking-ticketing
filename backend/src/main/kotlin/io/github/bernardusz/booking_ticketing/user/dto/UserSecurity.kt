package io.github.bernardusz.booking_ticketing.user.dto
import io.github.bernardusz.booking_ticketing.user.Role
import io.github.bernardusz.booking_ticketing.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


data class UserSecurity(
    val user: User,
) : UserDetails {

    fun getId(): Long{
        return user.id
    }

    fun getEmail(): String{
        return user.email
    }

    override fun getUsername(): String{
        return user.username
    }

    override fun getPassword(): String{
        return user.password
    }

    fun getRole(): Role{
        return user.role
    }

    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(getRole().name))
    }

    override fun isEnabled(): Boolean { return true }
    override fun isCredentialsNonExpired(): Boolean { return true }
    override fun isAccountNonExpired(): Boolean { return true }
    override fun isAccountNonLocked(): Boolean { return true }
}