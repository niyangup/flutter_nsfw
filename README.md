# flutter_nsfw

open_nsfw_android 的flutter插件 暂时只支持安卓端


## 如何使用
1.初始化`initNsfw()`,必须要在使用之前初始化完成

2.开始使用`getNSFWScore`

## 识别结果
```dart
NSFWScoreBean.sfw   ... 非涉黄数值 数值越大约安全
NSFWScoreBean.nsfw   ... 涉黄数值  数值越大约危险
NSFWScoreBean.timeConsumingToLoadData  ... 装载数据耗时  单位ms
NSFWScoreBean.timeConsumingToScanData  ... 扫描图片耗时  单位ms
```

## 根据对应值判断结果
```dart
if (it.nsfwScore > 0.7) {
    this.setBackgroundColor(Color.parseColor("#C1FF0000"))
} else if (it.nsfwScore > 0.5) {
    this.setBackgroundColor(Color.parseColor("#C1FF9800"))
} else {
    this.setBackgroundColor(Color.parseColor("#803700B3"))
}
```

## 链接
https://github.com/devzwy/open_nsfw_android
