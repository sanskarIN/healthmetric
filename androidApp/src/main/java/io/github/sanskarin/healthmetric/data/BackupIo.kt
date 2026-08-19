package io.github.sanskarin.healthmetric.data

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

object BackupIo {
    const val MAX_BACKUP_BYTES: Int = 1_048_576

    fun readUtf8(input: InputStream): String {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var totalBytes = 0

        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            totalBytes += read
            require(totalBytes <= MAX_BACKUP_BYTES) { "Backup file is too large." }
            output.write(buffer, 0, read)
        }

        return Charsets.UTF_8
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(output.toByteArray()))
            .toString()
    }

    fun writeUtf8(output: OutputStream, content: String) {
        val bytes = content.toByteArray(Charsets.UTF_8)
        require(bytes.size <= MAX_BACKUP_BYTES) { "Backup data is too large." }
        output.write(bytes)
        output.flush()
    }
}
