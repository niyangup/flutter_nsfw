package com.niyangup.flutter_nsfw

import android.content.Context
import android.os.FileUtils
import android.util.Log
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.github.devzwy.nsfw.NSFWHelper
import java.io.File
import java.nio.file.FileSystem

/** FlutterNsfwPlugin */
class FlutterNsfwPlugin : FlutterPlugin, MethodCallHandler {
    private var mContext: Context? = null

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_nsfw")
        channel.setMethodCallHandler(this)
        Log.d("TAG", "onAttachedToEngine: ")
        this.mContext = flutterPluginBinding.applicationContext

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.d("TAG", "onMethodCall: ${call.method}")
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "initNsfw") {
            handleInitNsfw(call, result)
        } else if (call.method == "getNSFWScore") {
            handleGetNSFWScore(call, result)
        } else {
            result.notImplemented()
        }
    }

    private fun handleGetNSFWScore(call: MethodCall, result: MethodChannel.Result) {
        val filePath: String? = call.argument<String>("filePath")
        filePath?.let {
            val file = File(filePath)
            NSFWHelper.getNSFWScore(file) {
                val data = "nsfw:${it.nsfwScore}\nsfw:${it.sfwScore}\n扫描耗时：${it.timeConsumingToScanData} ms"
                Log.d("TAG", "handleNSFW: $data")
            }
        }
        result.success("")
    }

    /**
     * 初始化
     */
    private fun handleInitNsfw(call: MethodCall, result: Result) {
        val enableLog = call.argument<Boolean>("enableLog") ?: true
        val filePath = call.argument<String>("filePath") ?: return
        val isOpenGPU = call.argument<Boolean>("isOpenGPU") ?: true
        val numThreads = call.argument<Int>("numThreads") ?: 4

        if (enableLog) {
            NSFWHelper.openDebugLog()
        }


        //扫描前必须初始化
        NSFWHelper.initHelper(
                context = mContext!!,
                isOpenGPU = isOpenGPU,
                modelPath = filePath,
                numThreads = numThreads
        )

        result.success("")

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        Log.d("TAG", "onDetachedFromEngine: ")
        mContext = binding.applicationContext
    }
}
