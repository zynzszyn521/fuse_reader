package com.cmcid.myreader.usb

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
import com.cmcid.myreader.uhf.Reader.setConnection
import com.cmcid.myreader.uhf.UHF_DEF.MSG_MSG
import com.cmcid.myreader.util.Common.arrByte2String
import com.cmcid.myreader.util.Common.bcc
import com.cmcid.myreader.util.Common.bytesToLong
import com.cmcid.myreader.util.Common.htonl
import com.cmcid.myreader.util.Common.memcpy
import com.cmcid.myreader.util.Common.memset
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class ReaderDevice {
    private var head_length = 0
    private var data_length = 0
    private val m_data = ByteArray(256)
    private var head_cmd: Byte = 0
    private var head_seq: Byte = 0
    private var data_size = 0
    private var mPermissionIntent: PendingIntent? = null
    private var mUsbManager: UsbManager? = null
    var endpointIn: UsbEndpoint? = null
        private set
    var endpointOut: UsbEndpoint? = null
        private set
    private var mUsbInterface: UsbInterface? = null
    var connection: UsbDeviceConnection? = null
        private set
    private var mReadingthread: Thread? = null
    private var isReading = false
    private var handler: Handler? = null

    //获取设备权限广播
    private val mUsbPermissionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_DEVICE_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let { initDevice(it) }
                    }
                }
            }
        }
    }

    //获取usb插拔广播
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {   // 插入
                searchUsb(context)
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {  // 拔出
                closeUsbService(context)
            }
        }
    }

    //搜索usb设备
    fun searchUsb(context: Context) {
        mUsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val devices = mUsbManager!!.deviceList
        val iterator: Iterator<UsbDevice> = devices.values.iterator()
        while (iterator.hasNext()) {
            val device = iterator.next()
            if (mUsbManager!!.hasPermission(device)) {
                initDevice(device)
            } else {
                mUsbManager!!.requestPermission(device, mPermissionIntent)
            }
        }
    }

    //初始化设备
    fun initDevice(device: UsbDevice): Boolean {
        val iC = device.interfaceCount
        endpointIn = null
        endpointOut = null
        for (k in 0 until iC) {
            val usbInterface = device.getInterface(k)
            //获取接口上的两个端点，分别对应 OUT 和 IN
            for (i in 0 until usbInterface.endpointCount) {
                val end = usbInterface.getEndpoint(i)
                if (end.direction == UsbConstants.USB_DIR_IN) {
                    endpointIn = end
                    Log.i("USB", "InType:" + endpointIn!!.type)
                } else {
                    endpointOut = end
                    Log.i("USB", "OutType:" + endpointOut!!.type)
                }
            }
            mUsbInterface = usbInterface
            //if(mUsbEndpointIn!=null && mUsbEndpointOut!=null)break;
        }
        if (endpointIn == null || endpointOut == null) {
            return false
        }
        //mUsbInterface = usbInterface;
        connection = mUsbManager!!.openDevice(device)
        if (connection != null) {
            setConnection(
                connection!!,
                endpointOut!!,
                endpointIn!!
            )
            startReading()
            return true
        }
        return false
    }

    //开线程讀取工號和姓名等信息（目前只支持方形綠色卡機）
    fun sendGetID() {
        val buf = ByteArray(64)
        memset(buf, 0, 0.toByte(), 64)
        buf[0] = 0x1B
        buf[1] = 0x1A
        buf[2] = 0
        buf[3] = 0
        buf[4] = 0
        buf[5] = 0
        buf[6] = 0
        connection!!.bulkTransfer(endpointOut, buf, 64, 1000)
    }

    //开线程讀取硬卡號信息
    fun sendGetCardID() {
        val buf = ByteArray(64)
        memset(buf, 0, 0.toByte(), 64)
        buf[0] = 0x1B
        buf[1] = 0x01
        buf[2] = 0
        buf[3] = 0
        buf[4] = 0
        buf[5] = 0
        buf[6] = 0
        connection!!.bulkTransfer(endpointOut, buf, 64, 1000)
    }

    //开线程读取数据
    fun sendGetVersion() {
        val buf = ByteArray(64)
        memset(buf, 0, 0.toByte(), 64)
        buf[0] = 0x1B
        buf[1] = 0x04
        buf[2] = 0
        buf[3] = 0
        buf[4] = 0
        buf[5] = 0
        buf[6] = 0
        connection!!.bulkTransfer(endpointOut, buf, 64, 1000)
    }


    protected fun recvPkgData(bytes: ByteArray, size: Int) {
        Log.i("Allen", "執行讀卡並接收到內容")
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
                        data_size = b * 256
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
                    if (bcc(m_data, 0, data_size) == b) {
                        recvData(head_cmd, head_seq, m_data, data_length)
                        head_length = 0
                    }
                }
            }
        }
    }

    protected fun recvData(cmd: Byte, seq: Byte, data: ByteArray, size: Int) {
        when (cmd) {
            CMD_CARDID -> {
                var obj: Any? = null
                if (size >= 4) {
                    var idValue = arrByte2String(data, 0, 4)
                    obj = JSONObject()
                    obj.put("cardno", idValue.replace(" ", ""))
                    handler!!.obtainMessage(
                        MSG_MSG, size, 0, obj
                    ).sendToTarget()
                }
            }

            CMD_VER -> {}
            CMD_EMP -> {
                var obj: Any? = null
                val cardno = arrByte2String(data, 0, 4)
                val sno =
                    htonl(
                        bytesToLong(data, 10)
                    ).toString()
                var empno = Char(data[5].toUShort()).toString()
                empno += htonl(bytesToLong(data, 6))
                var empname: String? = null
                val empData = ByteArray(9)
                empData[8] = 0
                memcpy(empData, data, 0, 4 + 16, 8)
                try {
                    empname = String(empData, charset("BIG5"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
//                obj = """
//                $cardno
//                $sno
//                $empno
//                ${empname!!.trim { it <= ' ' }}
//                ===================
//                """.trimIndent()
                obj = JSONObject()
                obj.put("cardno", cardno.replace(" ", ""))
                obj.put("sno", sno)
                obj.put("empno", empno)
                obj.put("empname", empname?.trim())
                
                handler!!.obtainMessage(
                    MSG_MSG, size, 0, obj
                ).sendToTarget()
            }
        }
        //handler.obtainMessage(
        //            UHF_DEF.MSG_MSG, cmd, 0, Common.arrByte2String(data,size)).sendToTarget();
    }

    //开线程读取数据
    private fun startReading() {
        connection!!.claimInterface(mUsbInterface, true)
        isReading = true
        val qr = StringBuffer()
        mReadingthread = Thread(object : Runnable {
            override fun run() {
                val bytes = ByteArray(endpointIn!!.maxPacketSize)
                head_length = 0
                while (isReading) {
                    synchronized(this) {
                        bytes[5] = 0
                        val ret =
                            connection!!.bulkTransfer(endpointIn, bytes, bytes.size, 100)
                        if (ret > 0) {
                            recvPkgData(bytes, ret)
                        }
                    }
                }
                connection!!.close()
            }
        })
        mReadingthread!!.start()
    }

    //关闭usb服务
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

        //注册插拔广播
        Log.i("Allen", "開始註冊插拔廣播")
        val usbFilter = IntentFilter()
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context.registerReceiver(mUsbReceiver, usbFilter)


        //注册usb权限广播
        Log.i("Allen", "開始註冊USB權限廣播")
        mPermissionIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION_DEVICE_PERMISSION), 0
        )
        val permissionFilter = IntentFilter(ACTION_DEVICE_PERMISSION)
        context.registerReceiver(mUsbPermissionReceiver, permissionFilter)
    }

    fun setHandler(h: Handler?) {
        handler = h
    }

    companion object {
        const val START_CODE = 0x1B.toByte()
        const val HEAD_LENGTH = 6
        const val CMD_CARDID = 0x01.toByte()
        const val CMD_VER = 0x04.toByte()
        const val CMD_EMP = 0x1A.toByte()
        private const val ACTION_DEVICE_PERMISSION = "com.cmcid.USB_PERMISSION"
    }
}