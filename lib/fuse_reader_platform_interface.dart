import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'fuse_reader_method_channel.dart';

abstract class FuseReaderPlatform extends PlatformInterface {
  FuseReaderPlatform() : super(token: _token);

  static final Object _token = Object();

  static FuseReaderPlatform _instance = MethodChannelFuseReader();

  static FuseReaderPlatform get instance => _instance;

  static set instance(FuseReaderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> searchUsb() {
    throw UnimplementedError('searchUsb() has not been implemented.');
  }

  Future<String?> startRead() {
    throw UnimplementedError('startRead() has not been implemented.');
  }

  Future<String?> startAutoRead(int readInterval) {
    throw UnimplementedError('startAutoRead() has not been implemented.');
  }

  Future<void> stopAutoRead() {
    throw UnimplementedError('stopAutoRead() has not been implemented.');
  }

  Stream<String> get onReadResult {
    throw UnimplementedError('onReadResult has not been implemented.');
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> getConnectionStatus() {
    throw UnimplementedError('getConnectionStatus() has not been implemented.');
  }
}
