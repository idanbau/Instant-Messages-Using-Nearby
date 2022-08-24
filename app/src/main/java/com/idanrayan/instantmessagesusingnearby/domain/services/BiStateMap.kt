package com.idanrayan.instantmessagesusingnearby.domain.services

import androidx.compose.runtime.snapshots.SnapshotStateMap

class BiStateMap<K : Any, V : Any>(
    private val _direct: SnapshotStateMap<K, V> = SnapshotStateMap(),
    private val reverse: LinkedHashMap<V, K> =
        _direct.entries.associate { (key, value) -> value to key } as LinkedHashMap<V, K>
) {
    val direct: Map<K, V> = _direct
    fun get(key: K): V? = _direct[key]
    fun getKey(value: V): K? = reverse[value]

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    private fun put(key: K, value: V): V? {
        require(value !in reverse) { "BiMap already contains value $value" }
        return forcePut(key, value)
    }

    private fun forcePut(key: K, value: V): V? {
        val oldValue = _direct.put(key, value)
        oldValue?.let { reverse.remove(it) }
        val oldKey = reverse.put(value, key)
        oldKey?.let { _direct.remove(it) }
        return oldValue
    }

    fun containsKey(key: K): Boolean = key in _direct

    fun containsValue(value: V): Boolean = value in reverse

    fun remove(key: K): V? {
        val oldValue = _direct.remove(key)
        oldValue?.let { reverse.remove(it) }
        return oldValue
    }

    fun removeValue(value: V): K? {
        val oldValue = reverse.remove(value)
        oldValue?.let { _direct.remove(it) }
        return oldValue
    }

    fun clear(initialKey: K, initialValue: V) {
        _direct.clear()
        reverse.clear()
        put(initialKey, initialValue)
    }

    fun isEmpty(): Boolean = _direct.isEmpty()
}