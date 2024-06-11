package presantation.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.realm.RealmDB
import data.remote.ChatSocketService
import data.remote.MessageService
import domain.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import util.Resource

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val realmDB: RealmDB,
) : ScreenModel {

    private var messageJob: Job? = null

    private val _state = mutableStateOf(ChatState())
    val state = _state

    private val _messageText = mutableStateOf("")
    val messageText = _messageText

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    var errorText by mutableStateOf("")
    var messages: List<Message> by mutableStateOf(emptyList())
    var active = false

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun connect(username: String) {
        screenModelScope.launch(Dispatchers.IO) {
            val result = chatSocketService.initSession(username)
            if (result is Resource.Success) {
                active = true
                messageJob = launch(Dispatchers.IO) {
                    chatSocketService.observeMessages()
                        .collect { message ->
                            withContext(Dispatchers.IO) {
                                val update = messages + message
                                messages = update
                                _state.value = _state.value.copy(messages = update)
                            }
                        }
                }
            } else {
                if (result.message?.isNotBlank() == true) {
                    withContext(Dispatchers.IO) {
                        errorText = result.message
                    }
                    delay(1500)
                    withContext(Dispatchers.IO) {
                        errorText = ""
                    }
                }
            }
        }
    }

    fun disconnect() {
        screenModelScope.launch(Dispatchers.IO) {
            chatSocketService.closeSession()
            active = false
            messageJob?.cancel()
        }
    }

    fun getAllMessages() {
        screenModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            var results = messageService.getAllMessages()
            if (results.isEmpty()) {
                results = realmDB.getAllMessages()
            } else {
                realmDB.updateMessage(results)
            }
            _state.value = _state.value.copy(isLoading = false, messages = results)
            messages = results
        }
    }

    fun getActiveUsers() {
        screenModelScope.launch(Dispatchers.IO) {
            val activeUsers = messageService.getActiveUsers().toMutableList()
            withContext(Dispatchers.IO) {
                _state.value = _state.value.copy(activeChatId = activeUsers)
            }
        }
    }

    fun sendMessage() {
        screenModelScope.launch(Dispatchers.IO) {
            if (messageText.value.isNotBlank()) {
                val result = chatSocketService.sendMessage(messageText.value)
                if (result.message?.isNotBlank() == true) {
                    withContext(Dispatchers.IO) {
                        errorText = result.message
                    }
                    delay(1000)
                    withContext(Dispatchers.IO) {
                        errorText = ""
                    }
                }
            }
            withContext(Dispatchers.IO) {
                _messageText.value = ""
                messageText.value = ""
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        disconnect()
    }
}
