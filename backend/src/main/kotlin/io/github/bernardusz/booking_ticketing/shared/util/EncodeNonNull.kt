package io.github.bernardusz.booking_ticketing.shared.util

import org.springframework.security.crypto.password.PasswordEncoder

fun PasswordEncoder.encodeNonNull(rawPassword: CharSequence): String{
    return requireNotNull(this.encode(rawPassword)) {
        "PasswordEncoder returned null for the provided password"
    }
}