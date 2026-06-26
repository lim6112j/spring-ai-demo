package spring.ai.example.spring_ai_demo

import org.springaicommunity.agent.tools.FileSystemTools
import org.springaicommunity.agent.tools.GrepTool
import org.springaicommunity.agent.tools.GlobTool
import org.springaicommunity.agent.tools.ShellTools
import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import java.util.Scanner


@RestController
class ChatController(chatClientBuilder: ChatClient.Builder) {
  private val chatClient: ChatClient =
      chatClientBuilder
          .defaultSystem { """
              You are a helpful assistant. you have access to tools,
              for reading files, searching code, running shell commands
              and editting files. use them to help the user with their codebase.
               Current directory is %s
          """.format(System.getProperty("user.dir")) }
          .defaultTools(
              FileSystemTools.builder().build(),
              GrepTool.builder().build(),
              GlobTool.builder().build(),
              ShellTools.builder().build()
          )
          .defaultAdvisors(
              MessageChatMemoryAdvisor
                  .builder(MessageWindowChatMemory.builder().build())
                  .build()
          )
          .build()
    var scanner = Scanner(System.`in`)
    init {
        println("🤖 Sprout coding Agent at your service. Ask me anything")
        while (true) {
            print("\n> ")
            val input: String = scanner.nextLine()
            if ("exit".equals(input.trim(), ignoreCase = true)) break
            try {
                val response = chatClient
                    .prompt()
                    .advisors { it.param(ChatMemory.CONVERSATION_ID, "cli-session") }
                    .user(input)
                    .call()
                println(response.content())
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    @PostMapping("/chat")
    fun chat(
        @RequestParam message: String,
        @RequestParam(defaultValue = "default") conversationId: String
    ): String {
        return chatClient
            .prompt()
            .advisors { it.param(ChatMemory.CONVERSATION_ID, conversationId) }
            .user(message)
            .call()
            .content() ?: ""
    }
}
