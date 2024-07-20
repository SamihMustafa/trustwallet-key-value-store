package com.test.transactionalkeyvaluestore

import com.test.transactionalkeyvaluestore.data.KeyValueStore
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyValueStoreTest {

    private lateinit var storage: KeyValueStore

    @BeforeTest
    fun setup() {
        storage = KeyValueStore()
    }

    @Test
    fun getAndSet() {
        storage.set("foo", "100")
        storage.get("foo")
        assertEquals("100", storage.get("foo"))
    }

    @Test
    fun delete() {
        storage.set("foo", "100");
        storage.delete("foo")
        assertEquals(null, storage.get("foo"))
    }

    @Test
    fun count() {
        storage.set("foo", "123")
        storage.set("bar", "456")
        storage.set("baz", "123")
        assertEquals(2, storage.count("123"))
        assertEquals(1, storage.count("456"))
    }

    @Test
    fun commitTransaction() {
        storage.set("bar", "123")
        assertEquals("123", storage.get("bar"))
        storage.beginTransaction()
        storage.set("foo", "456")
        assertEquals("123", storage.get("bar"))
        storage.delete("bar")
        storage.commitTransaction()
        assertEquals(null, storage.get("bar"))
        assertEquals(false, storage.rollbackTransaction())
        assertEquals("456", storage.get("foo"))
    }

    @Test
    fun rollBackTransaction() {
        storage.set("foo", "123")
        storage.set("bar", "abc")
        storage.beginTransaction()
        storage.set("foo", "456")
        assertEquals("456", storage.get("foo"))
        storage.set("bar", "def")
        assertEquals("def", storage.get("bar"))
        storage.rollbackTransaction()
        assertEquals("123", storage.get("foo"))
        assertEquals("abc", storage.get("bar"))
        assertEquals(false, storage.commitTransaction())
    }

    @Test
    fun nestedTransaction() {
        storage.set("foo", "123")
        storage.set("bar", "456")
        storage.beginTransaction()
        storage.set("foo", "456")
        storage.beginTransaction()
        assertEquals(2, storage.count("456"))
        assertEquals("456", storage.get("foo"))
        storage.set("foo", "789")
        assertEquals("789", storage.get("foo"))
        storage.rollbackTransaction()
        assertEquals("456", storage.get("foo"))
        storage.delete("foo")
        assertEquals(null, storage.get("foo"))
        storage.rollbackTransaction()
        assertEquals("123", storage.get("foo"))
    }


}