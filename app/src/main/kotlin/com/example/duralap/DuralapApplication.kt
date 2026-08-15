package com.example.duralap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = ["com.example.duralap"])
class DuralapApplication

fun main(args: Array<String>) {
    runApplication<DuralapApplication>(*args)
}
