package kalbe.corp.aichatbottutorial

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create

object NetworkModule {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.28:3000/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
    val aiService = retrofit.create<AiService>()
}