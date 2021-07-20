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
import java.util.HashMap

/** FlutterNsfwPlugin */
class FlutterNsfwPlugin : FlutterPlugin, MethodCallHandler {
    private var mContext: Context? = null

    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_nsfw")
        channel.setMethodCallHandler(this)
        Log.d("TAG", "onAttachedToEngine: ")
        this.mContext = flutterPluginBinding.applicationContext

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.d("TAG", "onMethodCall: ${call.method}")
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "initNsfw" -> {
                handleInitNsfw(call, result)
            }
            "getNSFWScore" -> {
                handleGetNSFWScore(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun handleGetNSFWScore(call: MethodCall, result: MethodChannel.Result) {
        var resultMap: HashMap<String, String>? = null
        val filePath: String? = call.argument<String>("filePath")
        if (filePath != null) {
            val file = File(filePath)
            NSFWHelper.getNSFWScore(file) {
                val data = "nsfw:${it.nsfwScore}\nsfw:${it.sfwScore}\n扫描耗时：${it.timeConsumingToScanData} ms"
                Log.d("TAG", "handleNSFW: $data")
                resultMap = HashMap()
                resultMap?.apply {
                    put("nsfw", "${it.nsfwScore}")
                    put("sfw", "${it.sfwScore}")
                    put("timeConsumingToLoadData", "${it.timeConsumingToScanData}")
                    put("timeConsumingToScanData", "${it.timeConsumingToScanData}")
                }
                result.success(resultMap)
            }
        } else {
            result.success(null)
        }

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
