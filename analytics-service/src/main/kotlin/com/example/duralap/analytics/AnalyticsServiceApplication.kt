package com.example.duralap.analytics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.duralap"])
class AnalyticsServiceApplication

fun main(args: Array<String>) {
    runApplication<AnalyticsServiceApplication>(*args)
}
