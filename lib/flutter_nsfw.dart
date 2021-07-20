import 'dart:async';

import 'package:flutter/services.dart';

class FlutterNsfw {
  static const MethodChannel _channel = const MethodChannel('flutter_nsfw');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///初始化
  ///
  /// [enableLog] 是否开启log
  /// [filePath] tfile文件的路径 必填
  /// [isOpenGPU] 是否开启GPU扫描加速，部分机型兼容不友好的可关闭。默认开启
  /// [numThreads] 扫描数据时内部分配的线程 默认4
  static Future<void> initNsfw(
    String filePath, {
    bool enableLog = true,
    bool isOpenGPU = true,
    int numThreads = 4,
  }) async {
    await _channel.invokeMethod('initNsfw', {
      "filePath": filePath,
      "enableLog": enableLog,
      "isOpenGPU": isOpenGPU,
      "numThreads": numThreads,
    });
  }

  ///初始化
  /// [filePath] 图片文件url
  static Future<Map<Object?, Object?>> getNSFWScore(String filePath) async {
    final result = await _channel.invokeMethod('getNSFWScore', {
      "filePath": filePath,
    });
    return result;
  }
}
