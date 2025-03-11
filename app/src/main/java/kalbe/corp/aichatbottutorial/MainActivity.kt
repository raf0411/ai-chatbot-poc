package kalbe.corp.aichatbottutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val chatClient = ChatClient.instance()

            val clientInitialisationState by chatClient.clientState.initializationState.collectAsStateWithLifecycle()

            when (clientInitialisationState) {
                InitializationState.COMPLETE -> {
                    ChatTheme {
                        ChannelsScreen(
                            title = stringResource(id = R.string.app_name),
                            isShowingHeader = true,
                            onHeaderActionClick = { mainViewModel.createChannel() },
                            onChannelClick = { channel ->
                                startActivity(MessageActivity.getIntent(this, channel.cid))
                            },
                            onBackPressed = { finish() }
                        )
                    }
                }

                InitializationState.INITIALIZING -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                InitializationState.NOT_INITIALIZED -> {
                    Text(text = "Not initialized...")
                }
            }
        }
    }
}