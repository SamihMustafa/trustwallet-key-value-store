package com.test.transactionalkeyvaluestore.model

import com.test.transactionalkeyvaluestore.base.UiIntent
import com.test.transactionalkeyvaluestore.base.UiState
import com.test.transactionalkeyvaluestore.data.CommandHistory
import com.test.transactionalkeyvaluestore.data.Commands

sealed interface KeyValueStoreIntent : UiIntent {
    data class UpdateParams(val index: Int, val newValue: String) : KeyValueStoreIntent
    data class UpdateCommand(val command: Commands) : KeyValueStoreIntent
    data object AcceptCommand : KeyValueStoreIntent
}

data class KeyValueStoreState(
    val selectedCommand: Commands,
    val paramValueList: List<String>,
    val commandHistory: List<CommandHistory>
) : UiState