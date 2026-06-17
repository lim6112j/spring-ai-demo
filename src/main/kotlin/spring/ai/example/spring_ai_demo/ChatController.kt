package spring.ai.example.spring_ai_demo

import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController(chatClientBuilder: ChatClient.Builder) {
  private val chatClient: ChatClient = chatClientBuilder.build()

  @PostMapping("/chat")
  fun chat(@RequestParam message: String): String {
    return chatClient.prompt().user(message).call().content() ?: ""
  }
}
