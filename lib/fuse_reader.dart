import 'fuse_reader_platform_interface.dart';

abstract class FuseReader {
  static Future<String?> getPlatformVersion() {
    return FuseReaderPlatform.instance.getPlatformVersion();
  }

  static Future<bool?> getConnectionStatus() {
    return FuseReaderPlatform.instance.getConnectionStatus();
  }

  static Future<String?> searchUsb() async {
    return await FuseReaderPlatform.instance.searchUsb();
  }

  static Future<String?> startRead() async {
    return await FuseReaderPlatform.instance.startRead();
  }

  static Future<String?> startAutoRead() async {
    return await FuseReaderPlatform.instance.startAutoRead();
  }

  static Future<void> stopAutoRead() async {
    await FuseReaderPlatform.instance.stopAutoRead();
  }

  static Stream<String> get onReadResult {
    return FuseReaderPlatform.instance.onReadResult;
  }
}
