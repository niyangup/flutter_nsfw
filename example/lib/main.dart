import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_nsfw/flutter_nsfw.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    Directory appDocDir = await getApplicationDocumentsDirectory();
    String appDocPath = appDocDir.path;
    var file = File(appDocPath + "/nsfw.tflite");
    if (!file.existsSync()) {
      var data = await rootBundle.load("assets/nsfw.tflite");
      final buffer = data.buffer;
      await file.writeAsBytes(buffer.asUint8List(data.offsetInBytes, data.lengthInBytes));
    }
    await FlutterNsfw.initNsfw(file.path);
  }

  String imgPath = "";

  String result = "";

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (imgPath.isNotEmpty)
                Center(
                    child: Image.file(
                  File(imgPath),
                  width: 300,
                  height: 300,
                  fit: BoxFit.cover,
                )),
              Text('检测结果: $result'),
              ElevatedButton(
                child: Text('选择图片'),
                onPressed: () async {
                  final ImagePicker _picker = ImagePicker();
                  final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
                  if (image != null) {
                    setState(() {
                      imgPath = image.path;
                    });
                    var resultMap = await FlutterNsfw.getNSFWScore(imgPath);
                    setState(() {
                      result = json.encode(resultMap);
                    });
                  }
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
