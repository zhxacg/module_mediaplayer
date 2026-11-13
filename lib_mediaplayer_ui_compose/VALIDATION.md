# 验证记录

日期：2026-11-13。

## 已完成

- JDK 17 / Gradle 8.10.2 构建 Compose Release AAR 及独立示例 Debug APK。
- 7 项 JVM 单元测试：5 项视频尺寸测试、1 项旧 PlayerLayout 公开非 View 业务方法的 Java 签名兼容检查、1 项不带 Media3 运行库创建 StartArgs 的回归测试。
- 示例只带 MPV 内核。设备第一次启动暴露了 `PlayerConst.LiveConfiguration` 静态初始化依赖 `androidx.media3.common.util.Util` 的崩溃；已将固定毫秒常量换算为等值微秒常量，新增回归测试，重新构建安装后未再观察到该崩溃。
- MPV 适配器现在遵循 StartArgs.prepareAsync；Compose 使用参数副本强制异步 prepare。
- 独立示例已安装到 Android 10 / API 29 的 MI 4LTE 设备，收到 INIT 回调。

## 尚未验证

- 设备截图当时为全黑，尝试唤醒后设备从 ADB 断开；没有取得 prepare/start、持续进度、视频画面或声音成功的证据。
- 因此不能声称公开 MP4 已播放成功，也不能保证无起播卡顿。
- 真机上的全屏/小窗切换、进后台恢复、多轨道、HLS、字幕及其他内核仍需运行验证。
- 方法签名兼容不等于所有行为完全一致。Compose 组件/窗口和源视频帧截图的差异见 README。

## 继续设备验证

连接并解锁 Android 6.0+ 设备，在仓库根目录执行：

```bash
bash ./gradlew -PcomposeSample :compose_sample:assembleDebug
adb install -r lib_mediaplayer_ui_compose/sample/build/outputs/apk/debug/compose_sample-debug.apk
adb shell am start -n lib.kalu.mediaplayer.compose.sample/.MainActivity
adb logcat 'ComposeSample:D' 'ComposePlayer:D' 'AndroidRuntime:E' '*:S'
```

确认 prepared / started 回调、持续变化的 position、非黑画面和实际音频，再验证暂停恢复、60 秒 seek、全屏/小窗、释放重播和后台恢复。当前默认 URL 为 `https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4`，网络不可达时可通过 intent 的 url 参数切换可达的测试源。
