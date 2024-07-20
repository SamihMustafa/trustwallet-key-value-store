package com.test.transactionalkeyvaluestore.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.transactionalkeyvaluestore.data.Commands
import com.test.transactionalkeyvaluestore.KeyValueStoreViewModel
import com.test.transactionalkeyvaluestore.KeyValueStoreContract
import com.test.transactionalkeyvaluestore.android.ui.commandsview.ParamsView
import com.test.transactionalkeyvaluestore.data.CommandHistory
import com.test.transactionalkeyvaluestore.data.MessageType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClientScreen()
                }
            }
        }
    }
}

@Composable
fun ClientScreen() {
    val viewModel = remember { KeyValueStoreViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Client(
        uiState = uiState,
        setIntent = {
            viewModel.setIntent(it)
        }
    )
}

@Composable
fun Client(
    uiState: KeyValueStoreContract.State,
    setIntent: (KeyValueStoreContract.Intent) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        CommandHistoryView(modifier = Modifier.weight(1f), outputList = uiState.commandHistory)
        CommandInput(
            selectedCommand = uiState.selectedCommand,
            paramsValueList = uiState.paramValueList,
            changeCommand = {
                setIntent(KeyValueStoreContract.Intent.UpdateCommand(it))
            },
            changeParams = { index, newValue ->
                setIntent(KeyValueStoreContract.Intent.UpdateParams(index, newValue))
            },
            acceptCommand = {
                setIntent(KeyValueStoreContract.Intent.AcceptCommand)
            }
        )
    }
}

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


@Composable
fun CommandInput(
    modifier: Modifier = Modifier,
    selectedCommand: Commands,
    paramsValueList: List<String>,
    changeCommand: (Commands) -> Unit,
    changeParams: (Int, String) -> Unit,
    acceptCommand: () -> Unit
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            CommandView(
                Modifier.fillMaxWidth(),
                selectedCommand = selectedCommand.action
            ) {
                changeCommand(it)
            }
            Spacer(modifier = Modifier.height(8.dp))
            ParamsView(
                modifier = Modifier.fillMaxWidth(),
                selectedCommand = selectedCommand,
                paramsValueList = paramsValueList
            ) { index, newValue ->
                changeParams(index, newValue)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    acceptCommand()
                }) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Done"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandView(
    modifier: Modifier = Modifier,
    selectedCommand: String = Commands.SET.action,
    changeCommand: (Commands) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCommandText by remember(selectedCommand) { mutableStateOf(selectedCommand) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = selectedCommand,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Action") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Commands.entries.forEach {
                DropdownMenuItem(
                    text = { Text(it.label, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        changeCommand(it)
                        selectedCommandText = it.action
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
                HorizontalDivider()
            }
        }
    }
}
