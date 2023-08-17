package com.fuse.reader.fuse_reader

import android.content.Context
import android.hardware.usb.UsbDeviceConnection
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.cmcid.myreader.uhf.Reader.beginInv
import com.cmcid.myreader.uhf.UHF_DEF.MSG_MSG
import com.cmcid.myreader.usb.ReaderDevice
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.Timer
import java.util.TimerTask


/** FuseReaderPlugin */
class FuseReaderPlugin: FlutterPlugin, MethodCallHandler {

  private lateinit var context:Context
  private val readCardInterval = 1000L //读卡频率
  private val timer = Timer()
  private lateinit var channel : MethodChannel
  private val CHANNEL_NAME = "com.fuse.reader/methods"
  private lateinit var mReaderDevice: ReaderDevice

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
    channel.setMethodCallHandler(this)
    mReaderDevice = ReaderDevice()
    mReaderDevice.setHandler(handler)
    context = flutterPluginBinding.applicationContext
    registerReceiver(context)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if (call.method == "getConnectionStatus") {
      val isConnected = mReaderDevice.connection!=null
      result.success(isConnected)
    }else if (call.method == "searchUsb") {
      // 註冊並獲取權限
      mReaderDevice.searchUsb(context)
      result.success("註冊並獲取權限請求成功。。。。。")
    }else if (call.method == "startRead") {
      // 手動讀卡
      val ret = beginInv()
      mReaderDevice.sendGetCardID()
      //mReaderDevice.sendGetID()
      result.success("手動讀卡調用成功。。。。。")
    }else if (call.method == "startAutoRead") {
      // 启动自动读卡任务
      startAutoReadingCards()
      result.success("自动读卡任务已启动")
    } else if (call.method == "stopAutoRead") {
      // 停止自动读卡任务
      stopAutoReadingCards()
       result.success("自动读卡任务已停止")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    unregisterReceiver(binding.applicationContext)
  }

  private fun registerReceiver(context: Context) {
    //注册
    mReaderDevice.regReceiver(context)
    mReaderDevice.searchUsb(context)
  }

  private fun unregisterReceiver(context: Context) {
    //取消注册
    mReaderDevice.closeUsbService(context)
  }

  private fun startAutoReadingCards() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
              mReaderDevice.sendGetCardID()
            }
        }, 0, readCardInterval)
  }

  private fun stopAutoReadingCards() {
        timer.cancel()
  }

  private val handler: Handler = Handler(Looper.getMainLooper()) { msg ->
    when (msg.what) {
      MSG_MSG -> {
        channel.invokeMethod("onReadResult", msg.obj.toString())
      }
    }
    true
  }
}


