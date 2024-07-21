package com.test.transactionalkeyvaluestore.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.transactionalkeyvaluestore.model.KeyValueStoreViewModel
import com.test.transactionalkeyvaluestore.android.ui.CommandHistoryView
import com.test.transactionalkeyvaluestore.android.ui.CommandInput
import com.test.transactionalkeyvaluestore.model.KeyValueStoreIntent
import com.test.transactionalkeyvaluestore.model.KeyValueStoreState
import androidx.lifecycle.viewmodel.compose.viewModel

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
fun ClientScreen(viewModel: KeyValueStoreViewModel = viewModel()) {
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
    uiState: KeyValueStoreState,
    setIntent: (KeyValueStoreIntent) -> Unit
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
                setIntent(KeyValueStoreIntent.UpdateCommand(it))
            },
            changeParams = { index, newValue ->
                setIntent(KeyValueStoreIntent.UpdateParams(index, newValue))
            },
            acceptCommand = {
                setIntent(KeyValueStoreIntent.AcceptCommand)
            }
        )
    }
}