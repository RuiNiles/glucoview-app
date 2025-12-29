package com.ruiniles.glucoview.core.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.System.currentTimeMillis


data class LogLine(
    val msg: String,
    val ts: Long = currentTimeMillis()
)

object LogCollector {
    private const val MAX_LINES = 50
    private val _lines = MutableStateFlow<List<LogLine>>(emptyList())
    val lines: StateFlow<List<LogLine>> = _lines

    @Synchronized
    fun log(msg: String) {
        val updated = (_lines.value + LogLine(msg))

        _lines.value = when {
            updated.size > MAX_LINES -> updated.drop(updated.size - MAX_LINES)
            else -> updated
        }
    }

    @Synchronized
    fun clear() {
        _lines.value = emptyList()
    }
}
