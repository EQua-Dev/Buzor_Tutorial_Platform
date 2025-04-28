package awesomenessstudios.schoolprojects.buzortutorialplatform.utils

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay
import javax.inject.Inject

class OpenAIService(private val apiKey: String) {

    @OptIn(BetaOpenAI::class)
    suspend fun generateCourseDescription(
        subject: String,
        targetGrades: List<String>,
        title: String
    ): String {
        Log.d(this.javaClass.name.toString(), "generateCourseDescription: $apiKey")
        val openAI = OpenAI(apiKey)


        val prompt = """
            Generate a short and engaging course description for a course titled "$title".
            The course covers $subject and is designed for students in ${targetGrades.joinToString(", ")}.
            The description should be concise, professional, and highlight the key benefits of the course.
        """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"), // Use GPT-3.5 Turbo
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        var attempt = 0
        val maxAttempts = 3

        while (attempt < maxAttempts) {
            try {
                val completion = openAI.chatCompletion(chatCompletionRequest)
                return completion.choices.firstOrNull()?.message?.content ?: "Failed to generate description."
            } catch (e: OpenAIAPIException) { // Make sure you import or catch the right OpenAI exception
                if (e.statusCode == 429) {
                    attempt++
                    Log.w(this.javaClass.name, "Rate limited (429), retrying attempt $attempt...")
                    delay(2000L * attempt) // wait longer each retry (e.g., 2s, 4s, 6s)
                } else {
                    throw e // If it's another error (not 429), rethrow
                }
            }
        }

        return "Failed to generate description after retries."

    }
}
