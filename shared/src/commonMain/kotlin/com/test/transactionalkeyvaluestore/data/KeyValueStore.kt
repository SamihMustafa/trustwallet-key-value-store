package com.test.transactionalkeyvaluestore.data

class KeyValueStore {

    private var commitedDb: Storage = Storage()
    private var transactions: MutableList<Storage> = mutableListOf()

    /**
     * Set the value for the given key in the most recent transaction
     */
    fun set(key: String, value: String) {
        (getCurrentTransaction() ?: commitedDb).set(key, value)
    }

    /**
     * Get the value for the given key in the most recent transaction
     */
    fun get(key: String): String? {
        // traverse the transactions list backwards from most recent transaction
        for (transaction in transactions.reversed()) {
            if (key in transaction.store) {
                return transaction.get(key)
            }
        }
        return commitedDb.get(key)
    }

    /**
     * Delete the value for the given key in the most recent transaction or remove from the committed db
     */
    fun delete(key: String) {
        getCurrentTransaction()?.delete(key) ?: remove(key)
    }

    /**
     * Remove the value for the given key from the committed db
     */
    private fun remove(key: String) {
        commitedDb.store.remove(key)
    }

    /**
     * Count the number of times the value appears in the storage
     */
    fun count(value: String): Int {
        val allDbValues = transactions.flatMap { it.store.values } + commitedDb.store.values
        return allDbValues.count { it == value }
    }

    /**
     * Begin a new transaction
     */
    fun beginTransaction() {
        transactions.add(Storage())
    }

    /**
     * Commit the last transaction
     */
    fun commitTransaction(): Boolean {
        if (transactions.isEmpty()) {
            return false
        }
        val lastTransaction = transactions.removeLast()
        lastTransaction.store.forEach { (key, value) ->
            if (value == null) {
                remove(key)
            } else {
                commitedDb.store[key] = value
            }
        }
        return true
    }

    /**
     * Rollback the last transaction
     */
    fun rollbackTransaction(): Boolean {
        if (transactions.isEmpty()) {
            return false
        }
        transactions.removeLast()
        return true
    }

    /**
     * Return the current (last) transaction or the commited db
     */
    private fun getCurrentTransaction(): Storage? {
        return transactions.lastOrNull()
    }



}