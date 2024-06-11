import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import data.remote.ChatSocketService
import data.remote.ChatSocketServiceImpl
import data.remote.MessageService
import data.remote.MessageServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import presantation.chat.ChatViewModel
import presantation.username.UserNameScreen
import presantation.username.UserNameViewModel
import presentation.theme.darkScheme
import presentation.theme.lightScheme

@Composable
@Preview
fun App() {
    MaterialTheme {
        initKoin()

        // Theme
        val colors by mutableStateOf(
            if (isSystemInDarkTheme()) darkScheme else lightScheme
        )

        // UI
        MaterialTheme(colorScheme = colors) {
            Navigator(UserNameScreen()) {
                SlideTransition(it)
            }
        }
    }
}

val mainModule = module {
    // singleton

//    single { RealmDb() }
//    // Viewmodel factory


    single {
        HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.BODY
                logger = Logger.DEFAULT
            }
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
    }

    single<MessageService> { MessageServiceImpl(get()) }
    single<ChatSocketService> { ChatSocketServiceImpl(get()) }

    single { UserNameViewModel() }
    single { ChatViewModel(get(), get()) }
}

fun initKoin() {
    stopKoin()

    startKoin {
        modules(mainModule)
    }

}