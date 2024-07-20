package com.test.transactionalkeyvaluestore.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.transactionalkeyvaluestore.data.Commands


@Composable
fun CommandInput(
    modifier: Modifier = Modifier,
    selectedCommand: Commands,
    paramsValueList: List<String>,
    changeCommand: (Commands) -> Unit,
    changeParams: (Int, String) -> Unit,
    acceptCommand: () -> Unit
) {
    var showErrorDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to proceed with this action?") },
            confirmButton = {
                TextButton(onClick = {
                    acceptCommand()
                    showConfirmationDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text("Please fill in all the parameters.") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

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
                    if (paramsValueList.any { it.trim().isEmpty() }) {
                        showErrorDialog = true
                    } else {
                        when (selectedCommand) {
                            Commands.DELETE, Commands.ROLLBACK, Commands.COMMIT -> {
                                showConfirmationDialog = true
                            }
                            else -> acceptCommand()
                        }
                    }
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
    selectedCommand: String,
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
