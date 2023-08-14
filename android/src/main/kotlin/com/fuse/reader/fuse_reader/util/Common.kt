package com.cmcid.myreader.util

object Common {
    fun memcpy(desBuf: ByteArray, srcBuf: ByteArray, desOffset: Int, srcOffset: Int, count: Int) {
        for (i in 0 until count) {
            desBuf[desOffset + i] = srcBuf[srcOffset + i]
        }
    }

    fun memcmp(desBuf: ByteArray, srcBuf: ByteArray, desOffset: Int, srcOffset: Int, count: Int): Int {
        for (i in 0 until count) {
            if (desBuf[desOffset + i] > srcBuf[srcOffset + i]) {
                return 1
            } else if (desBuf[desOffset + i] < srcBuf[srcOffset + i]) {
                return -1
            }
        }
        return 0
    }

    fun memset(desBuf: ByteArray, desOffset: Int, value: Byte, count: Int) {
        for (i in 0 until count) {
            desBuf[desOffset + i] = value
        }
    }

    fun arrByteIndexof(
        desBuf: ByteArray, desOffset: Int, descLength: Int,
        srcBuf: ByteArray, srcOffset: Int, size: Int
    ): Boolean {
        for (i in desOffset until descLength - size) {
            var b = true
            for (j in 0 until size) {
                if (srcBuf[srcOffset + j] != desBuf[i + j]) {
                    b = false
                    break
                }
            }
            if (b) return b
        }
        return false
    }

    private fun hex2Word(b: Byte): String {
        return "" + "0123456789ABCDEF"[0x0f and (b.toInt() shr 4)] +
                "0123456789ABCDEF"[b.toInt() and 0x0f]
    }

    fun arrByte2String(buf: ByteArray, offset: Int, size: Int): String {
        val sb = StringBuilder()
        for (i in 0 until size) {
            sb.append(hex2Word(buf[offset + i]))
            if (i < buf.size - 1) sb.append(' ')
        }
        return sb.toString()
    }

    fun arrByte2String(buf: ByteArray, size: Int): String {
        return arrByte2String(buf, 0, size)
    }

    fun hexStr2Bytes(src: String, desc: ByteArray, offset: Int, max: Int) {
        var m: Int
        var n: Int
        var l: Int
        val src = src.replace(" ", "")
        l = src.length / 2
        if (l > max) l = max
        var str1: String
        for (i in 0 until l) {
            m = i * 2 + 1
            n = m + 1
            str1 = "0x" + src.substring(i * 2, m) + src.substring(m, n)
            try {
                desc[offset + i] = str1.toInt(16).toByte()
            } catch (ex: Exception) {
            }
        }
    }

    fun hexStr2Bytes(src: String, desc: ByteArray, max: Int) {
        hexStr2Bytes(src, desc, 0, max)
    }

    fun longToBytes(num: Long, desc: ByteArray, offset: Int) {
        for (i in 0 until 4) {
            desc[i + offset] = (num ushr (24 - i * 8)).toByte()
        }
    }

    fun intH2L(desc: ByteArray, offset: Int) {
        val b = desc[offset + 0]
        desc[offset + 0] = desc[offset + 3]
        desc[offset + 3] = b
        val b2 = desc[offset + 2]
        desc[offset + 2] = desc[offset + 1]
        desc[offset + 1] = b2
    }

    fun shortH2L(desc: ByteArray, offset: Int) {
        val b = desc[offset + 0]
        desc[offset + 0] = desc[offset + 1]
        desc[offset + 1] = b
    }

    fun shortToBytes(num: Short, desc: ByteArray, offset: Int) {
        for (i in 0 until 2) {
            desc[i + offset] = (num.toInt() ushr (i * 8)).toByte()
        }
    }

    fun htons(inValue: Short): Short {
        val a = ((inValue.toInt() and 0xff) shl 8).toShort()
        val b = ((inValue.toInt() and 0xff00) ushr 8).toShort()
        return (a or b).toShort()
    }

    fun htonl(inValue: Long): Long {
        val a = ((inValue and 0xff).toInt() shl 24).toLong()
        val b = ((inValue and 0xff00).toInt() shl 8).toLong()
        val c = ((inValue and 0xff0000) ushr 8).toLong()
        val d = ((inValue and 0xff000000) ushr 24).toLong()
        return a or b or c or d
    }

    fun bytesToLong(data: ByteArray, offset: Int): Long {
        var num: Long = 0
        for (i in offset until offset + 4) {
            num = num shl 8
            num = num or (data[i].toInt() and 0xff).toLong()
        }
        return num
    }

    fun bytesToShort(data: ByteArray, offset: Int): Short {
        var num: Short = 0
        for (i in offset until offset + 2) {
            num = (num.toInt() shl 8).toShort()
            num = (num.toInt() or (data[i].toInt() and 0xff)).toShort()
        }
        return num
    }

    fun bcc(data: ByteArray, offset: Int, size: Int
