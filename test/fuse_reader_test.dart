import 'package:flutter_test/flutter_test.dart';
import 'package:fuse_reader/fuse_reader.dart';
import 'package:fuse_reader/fuse_reader_platform_interface.dart';
import 'package:fuse_reader/fuse_reader_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFuseReaderPlatform
    with MockPlatformInterfaceMixin
    implements FuseReaderPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FuseReaderPlatform initialPlatform = FuseReaderPlatform.instance;

  test('$MethodChannelFuseReader is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFuseReader>());
  });

  test('getPlatformVersion', () async {
    FuseReader FuseReaderPlugin = FuseReader();
    MockFuseReaderPlatform fakePlatform = MockFuseReaderPlatform();
    FuseReaderPlatform.instance = fakePlatform;

    expect(await FuseReaderPlugin.getPlatformVersion(), '42');
  });
}
