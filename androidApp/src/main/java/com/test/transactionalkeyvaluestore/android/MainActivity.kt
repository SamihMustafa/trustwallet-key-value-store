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
import com.test.transactionalkeyvaluestore.android.ui.CommandHistoryView
import com.test.transactionalkeyvaluestore.android.ui.CommandInput
import com.test.transactionalkeyvaluestore.android.ui.ParamsView
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