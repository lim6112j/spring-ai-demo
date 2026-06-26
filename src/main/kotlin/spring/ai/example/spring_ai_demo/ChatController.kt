package spring.ai.example.spring_ai_demo

import org.springaicommunity.agent.tools.FileSystemTools
import org.springaicommunity.agent.tools.GrepTool
import org.springaicommunity.agent.tools.GlobTool
import org.springaicommunity.agent.tools.ShellTools
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.Scanner


@Component
class ChatController(chatClientBuilder: ChatClient.Builder) : CommandLineRunner {
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

    override fun run(vararg args: String) {
        val scanner = Scanner(System.`in`)
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
}
