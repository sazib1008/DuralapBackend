package com.example.duralap.presence

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.duralap"])
class PresenceServiceApplication

fun main(args: Array<String>) {
    runApplication<PresenceServiceApplication>(*args)
}
