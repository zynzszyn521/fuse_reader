package com.cmcid.myreader.uhf.pkg

import com.cmcid.myreader.util.Common.htonl
import com.cmcid.myreader.util.Common.longToBytes
import com.cmcid.myreader.util.Common.shortToBytes

class HOST_REG_REQ {
    var access_flg: Short = 0
    var reg_addr: Short = 0
    var reg_data: Long = 0
    fun disposal(desc: ByteArray?) {
        shortToBytes(access_flg, desc!!, 0)
        shortToBytes(reg_addr, desc, 2)
        longToBytes(htonl(reg_data), desc, 4)
    }
}