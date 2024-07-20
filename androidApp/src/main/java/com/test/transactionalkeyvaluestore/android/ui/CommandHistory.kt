package com.test.transactionalkeyvaluestore.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.transactionalkeyvaluestore.data.CommandHistory
import com.test.transactionalkeyvaluestore.data.MessageType


@Composable
fun CommandHistoryView(
    modifier: Modifier = Modifier,
    outputList: List<CommandHistory>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        items(
            outputList.size
        ) { index ->
            val output = outputList[index]
            val color = when (output.messageType) {
                MessageType.INPUT -> MaterialTheme.colorScheme.primary
                MessageType.RESULT -> MaterialTheme.colorScheme.onPrimary
                MessageType.ERROR -> MaterialTheme.colorScheme.error
            }
            Message(
                modifier = Modifier.padding(4.dp),
                messageType = output.messageType,
                message = output.message,
                color = color
            )
        }
    }
}

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: String,
    color: Color,
    messageType: MessageType
) {
    val prefix = when (messageType) {
        MessageType.INPUT -> "> "
        MessageType.RESULT -> ""
        MessageType.ERROR -> ""
    }
    Text(
        modifier = modifier,
        text = "$prefix$message",
        color = color
    )
}
