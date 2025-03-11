package kalbe.corp.aichatbottutorial

import android.app.Application
import android.util.Log
import io.getstream.chat.android.client.BuildConfig
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.ConnectionData
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.result.call.Call
import java.util.Random

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true
            ),
            appContext = applicationContext
        )

        val chatClient = ChatClient.Builder("yjy7tzwdummj", applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING)
            .build()

        val user = User(
            id = "AIStreamUser1",
            name = "AI Android Stream",
            image = "https://picsum.photos/id/${Random().nextInt(1000)}/300/300"
        )
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQUlTdHJlYW1Vc2VyMSJ9.mmU8nzXeewhn4CSgUveqYFrCdleziY2evdSlc_eYUbc"
        chatClient.connectUser(user, token).enqueue { result ->
            if (result.isSuccess) {
                Log.d("App", "Successfully connect user")
            } else {
                Log.e("App", "Failed to connect user: ${result.errorOrNull()}")
            }
        }
    }
}