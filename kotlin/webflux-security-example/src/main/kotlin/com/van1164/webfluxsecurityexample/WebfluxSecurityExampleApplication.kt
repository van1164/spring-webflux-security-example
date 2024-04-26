package com.van1164.webfluxsecurityexample

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxSecurityExampleApplication

val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
	runApplication<WebfluxSecurityExampleApplication>(*args)
}
