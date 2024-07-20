package com.test.transactionalkeyvaluestore.data

data class Storage(var store: MutableMap<String, String?> = mutableMapOf()) {

    fun set(key: String, value: String) {
        store[key] = value
    }

    fun get(key: String): String? {
        return store[key]
    }

    fun delete(key: String) {
        store[key] = null
    }

}
