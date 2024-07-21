package com.test.transactionalkeyvaluestore.model

import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.viewModelScope as androidScope

actual abstract class ViewModel actual constructor() : AndroidXViewModel() {

    actual override fun onCleared() {
        super.onCleared()
    }

    actual val viewModelScope: CoroutineScope = androidScope

}