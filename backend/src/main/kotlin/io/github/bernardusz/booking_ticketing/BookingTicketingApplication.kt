package io.github.bernardusz.booking_ticketing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BookingTicketingApplication

fun main(args: Array<String>) {
	runApplication<BookingTicketingApplication>(*args)
}
