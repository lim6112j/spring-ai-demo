package spring.ai.example.spring_ai_demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["spring.ai.example.spring_ai_demo"])
class SpringAiDemoApplication

fun main(args: Array<String>) {
	runApplication<SpringAiDemoApplication>(*args)
}
