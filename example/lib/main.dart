import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:fuse_reader/fuse_reader.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _readResult = '';
  late StreamSubscription<String> _readResultSubscription;

  @override
  void initState() {
    super.initState();
    //initPlatformState();

    _readResultSubscription = FuseReader.onReadResult.listen((readResult) {
      var result = json.decode(readResult);
      String cardno = result['cardno'];
      // String sno = result['sno'];
      // String empno = result['empno'];
      // String empname = result['empname'];
      setState(() {
        _readResult = cardno;
      });
    });
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion =
          await FuseReader.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Text('Running on: $_platformVersion\n'),
            Text('Read Data: $_readResult\n'),
            IconButton(
                onPressed: () async {
                  bool? bb = await FuseReader.getConnectionStatus();
                  if (bb == false) {
                    await FuseReader.searchUsb();
                    //這裡我想知道，獲取到的cc會返回true嗎
                  } else {
                    _startRead();
                  }
                },
                icon: const Icon(Icons.nfc)),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _readResultSubscription.cancel();
    super.dispose();
  }

  void _startRead() async {
    try {
      //String? aa = await FuseReader.startRead();
      String? aa = await FuseReader.startAutoRead(3);
      print("手動執行掃碼結果：" + aa!);
    } on Exception catch (e) {
      print(e.toString());
    }
  }
}
