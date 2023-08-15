import 'dart:async';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'fuse_reader_platform_interface.dart';

class MethodChannelFuseReader extends FuseReaderPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('com.fuse.reader/methods');

  StreamSubscription<String>? _readResultSubscription;
  final StreamController<String> _readResultStreamController =
      StreamController<String>.broadcast();

  MethodChannelFuseReader() {
    methodChannel.setMethodCallHandler(_handleMethodCall);
  }

  @override
  Future<String?> startRead() async {
    try {
      final String result = await methodChannel.invokeMethod('startRead');
      return result;
    } on PlatformException catch (e) {
      if (kDebugMode) {
        print("Failed to read: ${e.message}");
      }
      return "";
    }
  }

  @override
  Future<String?> startAutoRead() async {
    try {
      final String result = await methodChannel.invokeMethod('startAutoRead');
      return result;
    } on PlatformException catch (e) {
      if (kDebugMode) {
        print("Failed to read: ${e.message}");
      }
      return "";
    }
  }

  @override
  Future<void> stopAutoRead() async {
    try {
      await methodChannel.invokeMethod<void>('stopAutoRead');
      _readResultSubscription?.cancel();
    } on PlatformException catch (e) {
      if (kDebugMode) {
        print("Failed to stop read: ${e.message}");
      }
    }
  }

  @override
  Stream<String> get onReadResult => _readResultStreamController.stream;

  Future<void> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'onReadResult':
        _readResultStreamController.add(call.arguments as String);
        break;
      default:
        throw PlatformException(
          code: 'Unimplemented',
          details:
              'The fuse_reader plugin for platform "${Platform.operatingSystem}" does not implement the method "${call.method}"',
        );
    }
  }

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
