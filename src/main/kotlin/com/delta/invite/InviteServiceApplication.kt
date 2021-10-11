	package com.delta.invite

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Invite Service", version = "1.2.32", description = "Invite new Partners with different roles"))
class InviteServiceApplication

fun main(args: Array<String>) {
	runApplication<InviteServiceApplication>(*args)
}
