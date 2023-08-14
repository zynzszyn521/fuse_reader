package com.cmcid.myreader.uhf

import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.os.Handler
import com.cmcid.myreader.uhf.UHF_DEF
import com.cmcid.myreader.uhf.pkg.HOST_REG_REQ
import com.cmcid.myreader.uhf.pkg.RFID_PACKET_18K6C_INVENTORY
import com.cmcid.myreader.uhf.pkg.RFID_PACKET_COMMON
import com.cmcid.myreader.util.Common

object Reader {
    private const val WRITE_TIMEOUT = 1000
    private lateinit var mUsbDeviceConnection: UsbDeviceConnection
    private lateinit var mUsbEndpointIn: UsbEndpoint
    private lateinit var mUsbEndpointOut: UsbEndpoint

    fun setConnection(
        usbDeviceConnection: UsbDeviceConnection,
        usbEndpointOut: UsbEndpoint,
        usbEndpointIn: UsbEndpoint
    ) {
        mUsbDeviceConnection = usbDeviceConnection
        mUsbEndpointOut = usbEndpointOut
        mUsbEndpointIn = usbEndpointIn
    }

    private fun writeMACRegister(
        accessFlag: Short,
        registerAddress: Long,
        value: Long
    ): Int {
        val request = HOST_REG_REQ()
        val buf = ByteArray(64)
        request.access_flg = accessFlag
        request.reg_addr = registerAddress.toShort()
        request.reg_data = value
        request.disposal(buf)
        return mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, buf, UHF_DEF.PACKAGE_SIZE, 1000)
    }

    private fun writeMACRegister(registerAddress: Long, value: Long): Int {
        return writeMACRegister(UHF_DEF.HOST_REG_REQ_ACCESS_WRITE, registerAddress, value)
    }

    fun getVersion(): Int {
        val buf = ByteArray(64)
        Common.memset(buf, 0, 0.toByte(), UHF_DEF.PACKAGE_SIZE)
        return mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, buf, UHF_DEF.PACKAGE_SIZE, 1000)
    }

    fun InvOnce(): Int {
        var ret = -1
        ret = writeMACRegister(UHF_DEF.HST_ANT_CYCLES, 1)

        ret = writeMACRegister(UHF_DEF.HST_CMD, UHF_DEF.CMD_18K6CINV)
        return ret
    }

    fun beginInv(): Int {
        var ret = -1
        ret = writeMACRegister(UHF_DEF.HST_ANT_CYCLES, 0xFFFF)

        ret = writeMACRegister(UHF_DEF.HST_CMD, UHF_DEF.CMD_18K6CINV)
        return ret
    }

    fun stopInv2(): Int {
        val mStopThread = Thread {
            synchronized(this) {
                var ret = 0
                ret = mUsbDeviceConnection.controlTransfer(
                    0x40, 0x01, 0, 0,
                    null, 0,
                    WRITE_TIMEOUT
                )
                ret = writeMACRegister(
                    UHF_DEF.HOST_REG_REQ_ACCESS_READ, UHF_DEF.MAC_ERROR, 0
                )
                ret = writeMACRegister(
                    UHF_DEF.HOST_REG_REQ_ACCESS_READ, UHF_DEF.MAC_LAST_ERROR, 0
                )
            }
        }
        mStopThread.start()
        return 0
    }

    fun stopInv(): Int {
        var ret = -1
        val buf = ByteArray(64)
        Common.memset(buf, 0, 0.toByte(), UHF_DEF.PACKAGE_SIZE)
        buf[0] = 0x40
        buf[1] = 0x01
        ret = mUsbDeviceConnection.controlTransfer(
            0x40, 0x01, 0, 0,
            null, 0,
            WRITE_TIMEOUT
        )
        ret = writeMACRegister(
            UHF_DEF.HOST_REG_REQ_ACCESS_READ, UHF_DEF.MAC_ERROR, 0
        )
        ret = writeMACRegister(
            UHF_DEF.HOST_REG_REQ_ACCESS_READ, UHF_DEF.MAC_LAST_ERROR, 0
        )
        return ret
    }

    fun TransData(buf: ByteArray, size: Int, handler: Handler): Int {
        val cmn = RFID_PACKET_COMMON()
        cmn.disposal(buf)
        when (cmn.pkt_type) {
            UHF_DEF.RFID_PACKET_TYPE_COMMAND_BEGIN -> Unit
            UHF_DEF.RFID_PACKET_TYPE_COMMAND_END -> Unit
            UHF_DEF.RFID_PACKET_TYPE_INVENTORY_CYCLE_BEGIN ->
                handler.obtainMessage(
                    UHF_DEF.MSG_MSG,
                    0, 0,
                    "CYCLE_BEGIN"
                ).sendToTarget()
            UHF_DEF.RFID_PACKET_TYPE_INVENTORY_CYCLE_END ->
                handler.obtainMessage(
                    UHF_DEF.MSG_MSG,
                    0, 0,
                    "CYCLE_END"
                ).sendToTarget()
            UHF_DEF.RFID_PACKET_TYPE_18K6C_INVENTORY_ROUND_BEGIN ->
                handler.obtainMessage(
                    UHF_DEF.MSG_MSG,
                    0, 0,
                    "ROUND_BEGIN"
                ).sendToTarget()
            UHF_DEF.RFID_PACKET_TYPE_18K6C_INVENTORY_ROUND_END ->
                handler.obtainMessage(
                    UHF_DEF.MSG_MSG,
                    0, 0,
                    "ROUND_END"
                ).sendToTarget()
            UHF_DEF.RFID_PACKET_TYPE_18K6C_INVENTORY -> {
                val inv = RFID_PACKET_18K6C_INVENTORY()
                inv.disposal(buf, UHF_DEF.PACKAGE_SIZE)
                handler.obtainMessage(
                    UHF_DEF.MSG_INVTAG,
                    inv.epc_size,
                    0,
                    inv.inv_data
                ).sendToTarget()
            }
        }
        return 0
    }
}
