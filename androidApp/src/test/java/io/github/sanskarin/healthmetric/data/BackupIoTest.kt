package io.github.sanskarin.healthmetric.data

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupIoTest {
    @Test
    fun utf8BackupRoundTripPreservesContent() {
        val original = "{\"schemaVersion\":1,\"note\":\"HealthMetric ✓\"}"
        val output = ByteArrayOutputStream()

        BackupIo.writeUtf8(output, original)
        val restored = BackupIo.readUtf8(ByteArrayInputStream(output.toByteArray()))

        assertEquals(original, restored)
    }

    @Test(expected = IllegalArgumentException::class)
    fun oversizedBackupReadIsRejected() {
        val oversized = ByteArray(BackupIo.MAX_BACKUP_BYTES + 1) { 'a'.code.toByte() }

        BackupIo.readUtf8(ByteArrayInputStream(oversized))
    }

    @Test(expected = IllegalArgumentException::class)
    fun oversizedBackupWriteIsRejected() {
        val oversized = "a".repeat(BackupIo.MAX_BACKUP_BYTES + 1)

        BackupIo.writeUtf8(ByteArrayOutputStream(), oversized)
    }
}
