package com.example.duralap.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.duralap"])
class SearchServiceApplication

fun main(args: Array<String>) {
    runApplication<SearchServiceApplication>(*args)
}
