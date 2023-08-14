package com.cmcid.myreader.usb

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Handler
import android.util.Log
import com.cmcid.myreader.uhf.Reader
import com.cmcid.myreader.uhf.UHF_DEF
import com.cmcid.myreader.util.Common
import java.io.UnsupportedEncodingException
import java.util.HashMap
import java.util.Iterator

object ReaderDevice {
    private const val START_CODE: Byte = 0x1B
    private const val HEAD_LENGTH = 6
    private const val CMD_EMP: Byte = 0x1A
    private var head_length = 0
    private var data_length = 0
    private val m_data = ByteArray(256)
    private var head_cmd: Byte = 0
    private var head_seq: Byte = 0
    private var data_size = 0

    private const val ACTION_DEVICE_PERMISSION = "com.cmcid.USB_PERMISSION"

    private lateinit var mPermissionIntent: PendingIntent
    private lateinit var mUsbManager: UsbManager
    private lateinit var mUsbEndpointIn: UsbEndpoint
    private lateinit var mUsbEndpointOut: UsbEndpoint
    private lateinit var mUsbInterface: UsbInterface
    private lateinit var mUsbDeviceConnection: UsbDeviceConnection
    private var mReadingthread: Thread? = null
    private var isReading = false
    private var handler: Handler? = null

    // 获取设备权限广播
    private val mUsbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (ACTION_DEVICE_PERMISSION == action) {
                synchronized(this) {
                    val device: UsbDevice? = intent?.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent?.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) == true) {
                        device?.let { initDevice(it) }
                    }
                }
            }
        }
    }

    // 获取 USB 插拔广播
    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {   // 插入
                context?.let { searchUsb(it) }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {  // 拔出
                context?.let { closeUsbService(it) }
            }
        }
    }

    fun getConnection(): UsbDeviceConnection? {
        return mUsbDeviceConnection
    }

    fun getEndpointOut(): UsbEndpoint? {
        return mUsbEndpointOut
    }

    fun getEndpointIn(): UsbEndpoint? {
        return mUsbEndpointIn
    }

    // 搜索 USB 设备
    fun searchUsb(context: Context) {
        mUsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val devices: HashMap<String, UsbDevice>? = mUsbManager.deviceList
        val iterator: Iterator<UsbDevice>? = devices?.values?.iterator()
        while (iterator?.hasNext() == true) {
            val device: UsbDevice = iterator.next()

            if (mUsbManager.hasPermission(device)) {
                initDevice(device)
            } else {
                mUsbManager.requestPermission(device, mPermissionIntent)
            }
        }
    }

    // 初始化设备
    fun initDevice(device: UsbDevice): Boolean {
        val iC = device.interfaceCount
        mUsbEndpointIn = null
        mUsbEndpointOut = null
        for (k in 0 until iC) {
            val usbInterface: UsbInterface = device.getInterface(k)
            // 获取接口上的两个端点，分别对应 OUT 和 IN
            for (i in 0 until usbInterface.endpointCount) {
                val end: UsbEndpoint = usbInterface.getEndpoint(i)
                if (end.direction == UsbConstants.USB_DIR_IN) {
                    mUsbEndpointIn = end
                    Log.i("USB", "InType:" + mUsbEndpointIn.type)
                } else {
                    mUsbEndpointOut = end
                    Log.i("USB", "OutType:" + mUsbEndpointOut.type)
                }
            }
            mUsbInterface = usbInterface
        }
        if (mUsbEndpointIn == null || mUsbEndpointOut == null) {
            return false
        }
        mUsbDeviceConnection = mUsbManager.openDevice(device)
        if (mUsbDeviceConnection != null) {
            Reader.setConnection(mUsbDeviceConnection, mUsbEndpointOut, mUsbEndpointIn)
            startReading()
            return true
        }
        return false
    }

    // 发送获取 ID 命令
    fun sendGetID() {
        val buf = ByteArray(64)
        Common.memset(buf, 0, 0.toByte(), 64)
        buf[0] = 0x1B
        buf[1] = 0x1A
        buf[2] = 0
        buf[3] = 0
        buf[4] = 0
        buf[5] = 0
        buf[6] = 0

        mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, buf, 64, 1000)
    }

    private fun recvPkgData(bytes: ByteArray, size: Int) {
        var b: Byte = 0
        for (i in 0 until size) {
            b = bytes[i]
            if (head_length < HEAD_LENGTH) {
                when (head_length) {
                    0 -> if (b == START_CODE) head_length++
                    1 -> {
                        head_length++
                        head_cmd = b
                    }
                    2 -> {
                        head_length++
                        head_seq = b
                    }
                    3 -> head_length++
                    4 -> {
                        head_length++
                        data_size = b.toInt() * 256
                    }
                    5 -> {
                        head_length++
                        data_size += b.toInt()
                        data_length = 0
                    }
                }
            } else {
                if (data_length < data_size) {
                    m_data[data_length++] = b
                } else {
                    if (Common.bcc(m_data, 0, data_size) == b) {
                        recvData(head_cmd, head_seq, m_data, data_length)
                        head_length = 0
                    }
                }
            }
        }
    }

    private fun recvData(cmd: Byte, seq: Byte, data: ByteArray, size: Int) {
        when (cmd) {
            CMD_EMP -> {
                var obj: Any? = null
                val cardno = Common.arrByte2String(data, 0, 4)
                val sno = Common.htonl(Common.bytesToLong(data, 10)).toString()
                var empno = data[5].toChar().toString()
                empno += Common.htonl(Common.bytesToLong(data, 6))
                var empname: String? = null
                val empData = ByteArray(9)
                empData[8] = 0
                Common.memcpy(empData, data, 0, 4 + 16, 8)
                try {
                    empname = String(empData, charset("BIG5"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                obj = "$cardno\r\n$sno\r\n$empno\r\n${empname?.trim()}\r\n==================="
                handler?.obtainMessage(
                    UHF_DEF.MSG_MSG,
                    size,
                    0,
                    obj
                )?.sendToTarget()
            }
        }
    }

    // 开始读取数据的线程
    private fun startReading() {
        mUsbDeviceConnection.claimInterface(mUsbInterface, true)
        isReading = true
        val qr = StringBuffer()

        mReadingthread = Thread {
            val bytes = ByteArray(mUsbEndpointIn.maxPacketSize)
            head_length = 0
            while (isReading) {
                synchronized(this) {
                    bytes[5] = 0
                    val ret = mUsbDeviceConnection.bulkTransfer(
                        mUsbEndpointIn,
                        bytes,
                        bytes.size,
                        100
                    )
                    if (ret > 0) {
                        recvPkgData(bytes, ret)
                    }
                }
            }
            mUsbDeviceConnection.close()
        }
        mReadingthread?.start()
    }

    // 关闭 USB 服务
    fun closeUsbService(context: Context) {
        if (isReading == true) {
            isReading = false
            try {
                Thread.sleep(1000)
            } catch (ex: Exception) {
            }
        }
        context.unregisterReceiver(mUsbReceiver)
        context.unregisterReceiver(mUsbPermissionReceiver)
    }

    fun regReceiver(context: Context) {
        // 注册插拔广播
        val usbFilter = IntentFilter()
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context.registerReceiver(mUsbReceiver, usbFilter)

        // 注册 USB 权限广播
        mPermissionIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION_DEVICE_PERMISSION), 0
        )
        val permissionFilter = IntentFilter(ACTION_DEVICE_PERMISSION)
        context.registerReceiver(mUsbPermissionReceiver, permissionFilter)
    }

    fun setHandler(h: Handler) {
        handler = h
    }
}
