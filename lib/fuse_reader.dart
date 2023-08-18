import 'fuse_reader_platform_interface.dart';

abstract class FuseReader {
  //獲取Android版本
  static Future<String?> getPlatformVersion() {
    return FuseReaderPlatform.instance.getPlatformVersion();
  }
  //獲取USB賦予讀取後的連接狀態(如果用戶沒有授權則返回false)
  static Future<bool?> getConnectionStatus() {
    return FuseReaderPlatform.instance.getConnectionStatus();
  }
  //重新請求獲取USB權限
  static Future<String?> searchUsb() async {
    return await FuseReaderPlatform.instance.searchUsb();
  }
  //開始手動讀卡
  static Future<String?> startRead() async {
    return await FuseReaderPlatform.instance.startRead();
  }
  //開始自動讀卡
  static Future<String?> startAutoRead(int readInterval) async {
    return await FuseReaderPlatform.instance.startAutoRead(readInterval);
  }
  //停止自動讀卡
  static Future<void> stopAutoRead() async {
    await FuseReaderPlatform.instance.stopAutoRead();
  }

  static Stream<String> get onReadResult {
    return FuseReaderPlatform.instance.onReadResult;
  }
}
