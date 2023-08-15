package com.cmcid.myreader.uhf.pkg

class HOST_18K6C_INV {
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
    var inv_data = ByteArray(256) //    public void disposal(byte[] tmpBuf,int size)
    //    {
    //        ms_ctr = Common.bytesToLong(tmpBuf,0);           ms_ctr = htonl(ms_ctr);
    //        nb_rssi = tmpBuf[4];
    //        wb_rssi = tmpBuf[5];
    //        ana_ctrl1 = Common.bytesToShort(tmpBuf, 6);    ana_ctrl1 = htons(ana_ctrl1);
    //        rssi = Common.bytesToShort(tmpBuf, 8);          rssi = htons(rssi);
    //        res0 = Common.bytesToShort(tmpBuf,10);          res0 = htons(res0);
    //        //PC = Common.bytesToShort(tmpBuf,12);          PC = htons(PC);
    ////        offset = 0;
    ////        inv_size = tmpSize-UHF_DEF.COMMAND_INV_SIZE;
    //        Common.memcpy(inv_data,tmpBuf,0, UHF_DEF.COMMAND_INV_SIZE,size - UHF_DEF.COMMAND_INV_SIZE);
    ////        offset = inv_size;
    //    }
}