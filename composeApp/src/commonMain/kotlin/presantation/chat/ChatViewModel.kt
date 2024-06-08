package presantation.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.remote.ChatSocketService
import data.remote.MessageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import util.Resource

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
) : ScreenModel {


    private val _state = mutableStateOf(ChatState())
    val state = _state

    private val _messageText = mutableStateOf("")
    val messageText = _messageText

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    var errorText by mutableStateOf(
        ""
    )

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    var active = false

    fun connect(username: String) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = chatSocketService.initSession(username)
            if (result is Resource.Success) {
                active = true
            }

            if (result.message?.isNotBlank() == true) {
                errorText = result.message
                delay(1500)
                errorText = ""
            }

        }
    }

    fun disconnect() {
        screenModelScope.launch(Dispatchers.IO) {
            chatSocketService.closeSession()
            active = false
        }
    }


    fun getAllMessages() {
        screenModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            val results = messageService.getAllMessages()
            _state.value = _state.value.copy(
                messages = results,
                isLoading = false
            )

        }
    }

    fun sendMessage() {
        screenModelScope.launch(Dispatchers.IO) {
            if (messageText.value.isNotBlank()) {
                val result = chatSocketService.sendMessage(messageText.value)
                if (result.message?.isNotBlank() == true) {
                    errorText = result.message
                    delay(1000)
                    errorText = ""
                }
            }
            _messageText.value = ""
            messageText.value = ""
        }
    }

    override fun onDispose() {
        super.onDispose()
        disconnect()
    }
}