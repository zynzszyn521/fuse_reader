package com.cmcid.myreader.uhf.pkg

import com.cmcid.myreader.util.Common.bytesToLong
import com.cmcid.myreader.util.Common.bytesToShort

class HOST_REG_RESP {
    var rfu0: Short = 0
    var reg_addr: Short = 0
    var reg_data: Long = 0
    fun disposal(tmpBuf: ByteArray?) {
        rfu0 = bytesToShort(tmpBuf!!, 0)
        reg_addr = bytesToShort(tmpBuf, 2)
        reg_data = bytesToLong(tmpBuf, 4)
    }
}