package com.example.duralap.media

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.duralap"])
class MediaServiceApplication

fun main(args: Array<String>) {
    runApplication<MediaServiceApplication>(*args)
}
