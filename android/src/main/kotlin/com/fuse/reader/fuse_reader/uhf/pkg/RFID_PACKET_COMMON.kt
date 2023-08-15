package com.cmcid.myreader.uhf.pkg

import com.cmcid.myreader.util.Common.bytesToShort
import com.cmcid.myreader.util.Common.htons

class RFID_PACKET_COMMON {
    /* Packet specific version number                                         */
    var pkt_ver: Byte = 0

    /* Packet specific flags*/
    var flags: Byte = 0

    /* Packet type identifier                                                 */
    var pkt_type: Short = 0

    /* Packet length indicator - number of 32-bit words that follow the common*/ /* packet preamble (i.e., this struct)                                    */
    var pkt_len: Short = 0

    /* Reserved for future use                                                */
    var res0: Short = 0
    fun disposal(tmpBuf: ByteArray) {
        pkt_ver = tmpBuf[0]
        flags = tmpBuf[1]
        pkt_type = bytesToShort(tmpBuf, 2)
        pkt_type = htons(pkt_type)
        pkt_len = bytesToShort(tmpBuf, 4)
        pkt_len = htons(pkt_len)
        res0 = bytesToShort(tmpBuf, 6)
    }
}