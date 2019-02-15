package no.westerdals.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication(scanBasePackages = ["no.westerdals.gateway"])
@EnableEurekaServer
class EurekaApplication

fun main(args: Array<String>) {
    SpringApplication.run(EurekaApplication::class.java, *args)
}