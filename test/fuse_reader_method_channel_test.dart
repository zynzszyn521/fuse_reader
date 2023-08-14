import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:fuse_reader/fuse_reader_method_channel.dart';

void main() {
  MethodChannelFuseReader platform = MethodChannelFuseReader();
  const MethodChannel channel = MethodChannel('fuse_reader');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
