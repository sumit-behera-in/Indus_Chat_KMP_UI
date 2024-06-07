package presantation.username

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserNameViewModel : ScreenModel {

    private val _userName = mutableStateOf("")
    val userName = _userName

    private val _onJoinChat = MutableSharedFlow<String>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    fun onUserNameChanged(userName: String) {
        _userName.value = userName
    }


    fun onJoinClick() {
        screenModelScope.launch(Dispatchers.IO) {
            if (userName.value.isNotBlank()) {
                _onJoinChat.emit(userName.value)
            }
        }
    }
}