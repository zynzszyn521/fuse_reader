import 'fuse_reader_platform_interface.dart';

abstract class FuseReader {
  static Future<String?> getPlatformVersion() {
    return FuseReaderPlatform.instance.getPlatformVersion();
  }

  static Future<String?> startScan() async {
    return await FuseReaderPlatform.instance.startScan();
  }

  static Future<void> stopScan() async {
    await FuseReaderPlatform.instance.stopScan();
  }

  static Stream<String> get onScanResult {
    return FuseReaderPlatform.instance.onScanResult;
  }
}
