package com.test.transactionalkeyvaluestore.data

enum class Commands(val action: String, val label: String, val paramsLabels: List<String>) {
    SET("SET", "SET <key> <value> // store the value for key", listOf("Key", "Value")),
    GET("GET", "GET <key> // return the current value for key", listOf("Key")),
    DELETE("DELETE", "DELETE <key> // remove the entry for key", listOf("Key")),
    COUNT(
        "COUNT",
        "COUNT <value> // return the number of keys that have the given value",
        listOf("Value")
    ),
    BEGIN("BEGIN", "BEGIN // start a new transaction", emptyList()),
    COMMIT("COMMIT", "COMMIT // complete the current transaction", emptyList()),
    ROLLBACK("ROLLBACK", "ROLLBACK // revert to state prior to BEGIN call", emptyList())
}