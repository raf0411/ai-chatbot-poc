import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.chat.android.ai.assistant.TypingState
import io.getstream.chat.android.ai.assistant.TypingState.Companion.toTypingState
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.AIIndicatorClearEvent
import io.getstream.chat.android.client.events.AIIndicatorStopEvent
import io.getstream.chat.android.client.events.AIIndicatorUpdatedEvent
import io.getstream.chat.android.client.extensions.cidToTypeAndId
import kalbe.corp.aichatbottutorial.AiAgentRequest
import kalbe.corp.aichatbottutorial.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val chatClient by lazy { ChatClient.instance() }

    private val _isAiStarted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAiStarted: StateFlow<Boolean> = _isAiStarted

    private val _typingState: MutableStateFlow<TypingState> = MutableStateFlow(TypingState.Nothing)
    val typingState: StateFlow<TypingState> = _typingState

    fun subscribeEvents(cid: String) {
        chatClient.channel(cid).subscribeFor(
            AIIndicatorUpdatedEvent::class.java,
            AIIndicatorClearEvent::class.java,
            AIIndicatorStopEvent::class.java
        ) { event ->
            if (event is AIIndicatorUpdatedEvent) {
                _typingState.value = event.aiState.toTypingState(event.messageId)
            } else if (event is AIIndicatorClearEvent) {
                _typingState.value = TypingState.Clear
            }
        }
    }

    fun startAiAssistant(cid: String) {
        val (_, id) = cid.cidToTypeAndId()
        viewModelScope.launch {
            _isAiStarted.value = true
            NetworkModule.aiService.startAiAgent(
                request = AiAgentRequest(id)
            )
        }
    }

    fun stopAiAssistant(cid: String) {
        val (_, id) = cid.cidToTypeAndId()
        viewModelScope.launch {
            _isAiStarted.value = false
            NetworkModule.aiService.stopAiAgent(
                request = AiAgentRequest(id)
            )
        }
    }
}