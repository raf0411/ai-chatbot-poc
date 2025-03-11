package kalbe.corp.aichatbottutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.launch
import kotlin.random.Random
import android.util.Log

class MainViewModel : ViewModel() {
    private val chatClient by lazy { ChatClient.instance() }

    fun createChannel() {
        viewModelScope.launch {
            val number = Random.nextInt(10000)
            chatClient.createChannel(
                channelType = "messaging",
                channelId = "channel$number",
                memberIds = listOf(chatClient.getCurrentUser()?.id.orEmpty()),
                extraData = mapOf()
            ).await().onSuccess {
                Log.d("StreamChat", "Created a new channel")
            }.onError {
                Log.e("StreamChat", "Error: $it")
            }
        }
    }
}
