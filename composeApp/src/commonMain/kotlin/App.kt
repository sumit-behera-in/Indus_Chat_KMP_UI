import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
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

//        // UI
//        MaterialTheme(colorScheme = colors) {
//            Navigator(HomeScreen()) {
//                SlideTransition(it)
//            }
//        }
    }
}

val realmModule = module {
    // singleton

//    single { RealmDb() }
//    // Viewmodel factory
//    single { HomeScreenViewModel(get()) }
//    single { TaskViewModel(get()) }

}

fun initKoin() {
    stopKoin()
    GlobalContext.startKoin {
        modules(realmModule)
    }

}