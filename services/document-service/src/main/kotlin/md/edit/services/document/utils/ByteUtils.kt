package md.edit.services.document.utils

fun ULong.toByteArray(): ByteArray {
    val result = ByteArray(ULong.SIZE_BYTES)
    (0 until ULong.SIZE_BYTES).forEach {
        result[it] = this.shr(Byte.SIZE_BITS * it).toByte()
    }
    return result
}