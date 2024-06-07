package presantation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import presantation.username.UserNameScreen

class ChatScreen(private val username: String) : Screen {
    @Composable
    override fun Content() {

        val viewModel: ChatViewModel = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        val state = viewModel.state.value

        viewModel.connect(username)
        viewModel.getAllMessages()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.weight(0.1f)
                            .clickable {
                                viewModel.disconnect()
                                navigator.replace(UserNameScreen())
                            }
                    )

                    Text(
                        text = viewModel.errorText.ifBlank { username },
                        modifier = Modifier.weight(0.9f),
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "isActive",
                        tint = if (viewModel.active) Color.Green else Color.Red
                    )

                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextField(
                        value = viewModel.messageText.value,
                        onValueChange = { viewModel.onMessageChange(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter Your Message") }
                    )

                    IconButton(
                        onClick = {
                            viewModel.sendMessage()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "send",
                        )
                    }

                }
            }
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {

                items(state.messages.size) { index ->
                    val isMe = state.messages[index].user == username
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {

                        Column(
                            modifier = Modifier
                                .background(if (isMe) Color.LightGray else Color.White)
                                .width(200.dp)
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = state.messages[index].user,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) Color.Blue else Color.Black,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 30.sp
                            )

                            Text(
                                text = state.messages[index].text,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) Color.Blue else Color.Black,
                                fontSize = 20.sp
                            )

                            Text(
                                text = state.messages[index].timeStamp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) Color.Blue else Color.Black,
                                textAlign = TextAlign.End,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }

            }


        }

    }

}