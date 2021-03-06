package com.nyxcode.blinded.backend

import java.io.File
import java.time.Duration
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*
import kotlin.reflect.KProperty
import java.time.Duration.of as duration


class Config(path: String) {

    private val file = File(path)

    private val properties = Properties().apply {
        if (file.exists()) {
            file.reader().use(::load)
        }
    }

    val hostname: String = get(::hostname) { "localhost" }
    val apiPort: Int = get(::apiPort) { 8081 }.toInt()
    val serverPort: Int = get(::serverPort) { 8080 }.toInt()
    val gameKeyLen: Int = get(::gameKeyLen) { 4 }.toInt()
    val playerKeyLen: Int = get(::playerKeyLen) { 16 }.toInt()
    val timeout: Duration = get(::timeout) { duration(10, MINUTES) }.let(Duration::parse)
    val checkRate: Duration = get(::checkRate) { duration(10, MINUTES) }.let(Duration::parse)

    val frontendDirectory: File = getDirectory(::frontendDirectory) { "../blinded-frontend/dist/" }

    fun save() = file.writer().use { properties.store(it, "blinded-backend configuration") }

    private inline fun get(field: KProperty<*>, def: () -> Any?): String =
            properties.getProperty(field.name) ?: def().toString().also {
                properties.setProperty(field.name, it)
            }

    private inline fun getDirectory(field: KProperty<*>, def: () -> Any?) = File(get(field, def))
}
