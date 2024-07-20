package com.test.transactionalkeyvaluestore.android.ui.commandsview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.transactionalkeyvaluestore.data.Commands


@Composable
fun ParamsView(
    modifier: Modifier = Modifier,
    selectedCommand: Commands = Commands.GET,
    paramsValueList: List<String>,
    onParamsChange: (Int, String) -> Unit
) {
    Row(modifier.padding(end = 12.dp)) {
        selectedCommand.paramsLabels.forEachIndexed() { index, paramLabel ->
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                singleLine = true,
                value = paramsValueList[index],
                onValueChange = {
                    onParamsChange(index, it)
                },
                label = { Text(paramLabel) },
            )
        }
    }

}