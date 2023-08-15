package com.cmcid.myreader.uhf.pkg

import com.cmcid.myreader.util.Common.bytesToLong
import com.cmcid.myreader.util.Common.bytesToShort
import com.cmcid.myreader.util.Common.htonl
import com.cmcid.myreader.util.Common.htons
import com.cmcid.myreader.util.Common.memcpy

class RFID_PACKET_18K6C_INVENTORY {
    /* Common preamble - part of every packet!                                  */ //public RFID_PACKET_COMMON    cmn;
    /* current millisecond timer/counter                                        */
    var ms_ctr: Long = 0

    /* Receive Signal Strength Indicator - backscattered tab signal */ /* amplitude.                                                               */
    var nb_rssi: Byte = 0
    var wb_rssi: Byte = 0
    var ana_ctrl1: Short = 0

    /* Reserved                                                                 */
    var rssi: Short = 0
    var res0: Short = 0

    /* Variable length inventory data (i.e., PC, EPC, and CRC)                  */
    var inv_data = ByteArray(256)
    var epc_size = 0
    fun disposal(tmpBuf: ByteArray, offset: Int) {
        epc_size = 0
        ms_ctr = bytesToLong(tmpBuf, offset)
        ms_ctr = htonl(ms_ctr)
        nb_rssi = tmpBuf[offset + 4]
        wb_rssi = tmpBuf[offset + 5]
        ana_ctrl1 = bytesToShort(tmpBuf, offset + 6)
        ana_ctrl1 = htons(ana_ctrl1)
        rssi = bytesToShort(tmpBuf, offset + 8)
        rssi = htons(rssi)
        res0 = bytesToShort(tmpBuf, offset + 10)
        res0 = htons(res0)
        epc_size = (tmpBuf[offset + 12].toInt() shr 3) * 2
        memcpy(inv_data, tmpBuf, 0, offset + 12, epc_size + 2 + 2)
    }
}