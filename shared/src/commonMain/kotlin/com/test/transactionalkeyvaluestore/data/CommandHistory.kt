package com.test.transactionalkeyvaluestore.data

data class CommandHistory(
    val messageType: MessageType,
    val message: String
)
