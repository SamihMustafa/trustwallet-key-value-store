package com.test.transactionalkeyvaluestore.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual abstract class ViewModel actual constructor() {

    actual val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    protected actual open fun onCleared() {
    }

    fun clear() {
        onCleared()
    }

}