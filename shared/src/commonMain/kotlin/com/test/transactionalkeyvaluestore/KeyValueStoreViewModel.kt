package com.test.transactionalkeyvaluestore

import com.test.transactionalkeyvaluestore.base.BaseViewModel
import com.test.transactionalkeyvaluestore.base.UiIntent
import com.test.transactionalkeyvaluestore.base.UiState
import com.test.transactionalkeyvaluestore.data.CommandHistory
import com.test.transactionalkeyvaluestore.data.Commands
import com.test.transactionalkeyvaluestore.data.KeyValueStore
import com.test.transactionalkeyvaluestore.data.MessageType

class KeyValueStoreViewModel(
    val keyValueStore: KeyValueStore = KeyValueStore()
) : BaseViewModel<KeyValueStoreContract.Intent, KeyValueStoreContract.State>() {

    override fun createInitialState(): KeyValueStoreContract.State {
        return KeyValueStoreContract.State(
            selectedCommand = Commands.SET,
            commandHistory = emptyList(),
            paramValueList = List(Commands.SET.paramsLabels.size) { "" }
        )
    }

    override fun handleIntent(intent: KeyValueStoreContract.Intent) {
        when (intent) {
            is KeyValueStoreContract.Intent.UpdateCommand -> updateCommand(intent.command)
            is KeyValueStoreContract.Intent.UpdateParams -> updateParams(
                intent.index,
                intent.newValue
            )
            is KeyValueStoreContract.Intent.AcceptCommand -> acceptCommand()
        }
    }

    private fun updateCommand(command: Commands) {
        setState {
            copy(
                selectedCommand = command,
                paramValueList = List(command.paramsLabels.size) { "" }
            )
        }
    }

    private fun updateParams(index: Int, newValue: String) {
        setState {
            copy(
                paramValueList = paramValueList.toMutableList().apply {
                    this[index] = newValue
                }
            )
        }
    }

    private fun acceptCommand() {
        when (val selectedCommand = currentState.selectedCommand) {
            Commands.SET -> set(
                currentState.paramValueList[0],
                currentState.paramValueList[1]
            )

            Commands.GET -> get(
                currentState.paramValueList[0]
            )

            Commands.DELETE -> delete(
                currentState.paramValueList[0]
            )

            Commands.COUNT -> count(
                currentState.paramValueList[0]
            )

            Commands.BEGIN -> beginTransaction()
            Commands.COMMIT -> commitTransaction()
            Commands.ROLLBACK -> rollbackTransaction()
        }
    }

    private fun set(key: String, value: String) {
        keyValueStore.set(key, value)
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, "${selectedCommand.action} $key $value"))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

    private fun count(value: String) {
        val count = keyValueStore.count(value)
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, "${selectedCommand.action} $value"))
                    add(CommandHistory(MessageType.RESULT, count.toString()))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

    private fun get(key: String) {
        val value = keyValueStore.get(key)
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, "${selectedCommand.action} $value"))
                    if (value != null) add(CommandHistory(MessageType.RESULT, value.toString()))
                    else add(CommandHistory(MessageType.ERROR, "Key not set"))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

    private fun delete(key: String) {
        keyValueStore.delete(key)
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, "${selectedCommand.action} $key"))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

    private fun beginTransaction() {
        keyValueStore.beginTransaction()
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, selectedCommand.action))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

    private fun commitTransaction() {
        val completed = keyValueStore.commitTransaction()
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, selectedCommand.action))
                    if (!completed) add(CommandHistory(MessageType.ERROR, "No transaction"))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

    private fun rollbackTransaction() {
        val completed = keyValueStore.rollbackTransaction()
        setState {
            copy(
                commandHistory = commandHistory.toMutableList().apply {
                    add(CommandHistory(MessageType.INPUT, selectedCommand.action))
                    if (!completed) add(CommandHistory(MessageType.ERROR, "No transaction"))
                },
                paramValueList = List(selectedCommand.paramsLabels.size) { "" }
            )
        }
    }

}

class KeyValueStoreContract {

    sealed interface Intent : UiIntent {
        data class UpdateParams(val index: Int, val newValue: String) : Intent
        data class UpdateCommand(val command: Commands) : Intent
        data object AcceptCommand : Intent
    }

    data class State(
        val selectedCommand: Commands,
        val paramValueList: List<String>,
        val commandHistory: List<CommandHistory>
    ) : UiState
}