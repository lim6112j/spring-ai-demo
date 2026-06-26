package spring.ai.example.spring_ai_demo

import org.springaicommunity.agent.tools.FileSystemTools
import org.springaicommunity.agent.tools.GrepTool
import org.springaicommunity.agent.tools.GlobTool
import org.springaicommunity.agent.tools.ShellTools
import org.springaicommunity.agent.tools.SkillsTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.Scanner


@Component
class ChatController(chatClientBuilder: ChatClient.Builder) : CommandLineRunner {
  private fun compact(text: String?, max: Int = 120): String {
      val normalized = text?.replace(Regex("\\s+"), " ")?.trim().orEmpty()
      return if (normalized.length <= max) normalized else normalized.take(max) + "…"
  }
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
              ShellTools.builder().build(),
              SkillsTool.builder().addSkillsDirectory(".claude/skills").build()
          )
          .defaultAdvisors(
              SimpleLoggerAdvisor
                  .builder()
                  .requestToString { req ->
                      val conversationId = req?.context()?.get(ChatMemory.CONVERSATION_ID) ?: "-"
                      val userPreview = compact(req?.prompt()?.getUserMessage()?.text)
                      "AI_REQ conversationId=$conversationId user=\"$userPreview\""
                  }
                  .responseToString { res ->
                      val generation = runCatching { res?.result }.getOrNull()
                      val assistantMessage = generation?.output
                      val toolCalls = assistantMessage?.toolCalls?.size ?: 0
                      val textPreview = compact(assistantMessage?.text)
                      "AI_RES hasToolCalls=${res?.hasToolCalls() ?: false} toolCalls=$toolCalls text=\"$textPreview\""
                  }
                  .build(),
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
