package kalbe.corp.aichatbottutorial
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable

interface AiService {
    @POST("start-ai-agent")
    suspend fun startAiAgent(@Body request: AiAgentRequest): AiAgentResponse

    @POST("stop-ai-agent")
    suspend fun stopAiAgent(@Body request: AiAgentRequest): AiAgentResponse
}

@Serializable
data class AiAgentRequest(
    val channel_id: String,
    val channel_type: String = "messaging"
)

@Serializable
data class AiAgentResponse(
    val message: String,
    val data: List<String>
)