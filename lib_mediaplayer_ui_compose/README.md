# Kotlin Compose 播放器

本模块使用 Compose 控件和 `AndroidEmbeddedExternalSurface`，不创建旧 `PlayerView` / `PlayerLayout`，也不使用 `AndroidView` 包装旧播放器。复用 `lib_mediaplayer_ui` 中的内核适配器、参数、轨道模型和监听器。

## 接入

```groovy
implementation project(':lib_mediaplayer_ui_compose')
// 选择 MPV 时还需要实际的播放器 AAR。原 UI 模块的 compileOnly 不会把内核带入 APK。
implementation files('路径/lib_mediaplayer_mpv_0.41.0_merged_release.aar')
```

宿主需要启用 Compose。当前项目使用 Kotlin 1.9.22、Compose compiler 1.5.10、Compose 1.6.8、compileSdk 34、minSdk 23，构建使用 JDK 17。

```kotlin
import lib.kalu.mediaplayer.compose.PlayerLayout
import lib.kalu.mediaplayer.compose.rememberPlayerController
import lib.kalu.mediaplayer.bean.args.StartArgs
import lib.kalu.mediaplayer.bean.type.PlayerType

@Composable
fun VideoPage(url: String) {
    val player = rememberPlayerController()
    LaunchedEffect(player, url) {
        // 在 start 之前注册现有 OnPlayerEventListener / OnPlayerProgressListener。
        player.start(
            StartArgs.newBuilder()
                .setKernelType(PlayerType.KernelType.MPV)
                .setUrl(url)
                .setPlayWhenReady(true)
                .build()
        )
    }
    PlayerLayout(player, Modifier.fillMaxWidth().height(240.dp))
}
```

`rememberPlayerController` 在离开 Composition 时释放内核；普通重组不会重启播放。手动构造的 `PlayerController(context)` 由调用者负责 `release()`。一个控制器同时只允许绑定一个 `PlayerLayout`。

`start()` 可早于 Surface 创建；收到有效 Surface 后启动。Compose 入口强制异步 prepare，不修改调用者传入的 StartArgs。切换 URL / stop / release 会取消尚未执行的延迟启动并丢弃旧内核回调。进后台暂停，返回前台仅在用户没有手动暂停时恢复。

## API 对应关系

| 原接口 | Compose 接口 |
| --- | --- |
| `PlayerLayout.start/pause/resume/stop/release/seekTo/...` | `PlayerController` 保留同名业务方法和 Java 参数、返回类型 |
| `StartArgs`、`UrlArgs`、`TrackInfo`、各类监听器 | 继续使用原包中的类型 |
| `getTrackInfo*`、`toggleTrack`、字幕偏移、外挂字幕、分片 | 转发给所选内核，能力取决于内核实现 |
| `startFull/stopFull` | Compose Dialog 内全屏播放 |
| `startFloat/stopFloat` | 应用内 Compose Popup；不是跨应用悬浮窗/PiP |
| `requestScreenOrientation` | 在已挂载的 Activity 上请求方向 |
| `addComponent`、`findComponent`、XML、View 构造器及触摸分发 | 改为 `PlayerLayout(controls = { ... })` Compose 插槽 |

**兼容的是播放业务方法，不是旧 View 类的二进制或 XML 兼容。** `ApiCompatibilityTest` 对旧 PlayerLayout 声明的公开非 View 方法逐一验证参数、返回类型。原模块保留，现有 View 页面无需迁移即可继续使用。

控制方法在主线程调用，回调派发到主线程。`state` 是只读 Compose 可观察状态，包含播放进度、准备/播放/缓冲状态、字幕、视频尺寸、窗口模式及错误码。不要在 Composable 函数体直接调用 `start()`。

`screenshot()` 保留同步返回路径的签名，通过 Android MediaMetadataRetriever 提取当前时间附近的源视频帧，不包含 Compose 控件/字幕，也不能保证等于正在显示的那一帧；设备不支持的网络流/HLS 返回 null。该操作可能产生 I/O，不应在频繁重组或动画中调用。截图保存在应用缓存目录，由应用按需清理。

默认控件包含播放/暂停、进度拖动、全屏、缓冲提示和字幕；轨道菜单等应用 UI 可通过 Compose 插槽调用现有轨道 API 构建。窗口 attach/visibility 回调对应 Composition 和生命周期，不再表示旧 View 的像素可见性。剧集监听器类型保留，但本模块没有新增自动剧集调度器。

## 构建与示例

```bash
# JDK 17 环境下，从仓库根目录运行
bash ./gradlew :lib_mediaplayer_ui_compose:assembleRelease :lib_mediaplayer_ui_compose:testDebugUnitTest
bash ./gradlew -PcomposeSample :compose_sample:assembleDebug
```

示例位于 `sample/`，仅在传入 `-PcomposeSample` 时加入构建，不修改原 app。默认公开 MP4 为 Google 示例 Big Buck Bunny；可通过 Activity intent 的 `url` 字符串参数覆盖。

Release AAR：`build/outputs/aar/lib_mediaplayer_ui_compose-release.aar`。
直接复制此 AAR **不会**包含项目依赖或 Compose / MPV 依赖；优先使用项目依赖，或发布时提供带传递依赖的 Maven POM。

尺寸测试覆盖横竖屏、90° / -90° 旋转、拉伸、指定比例及尚未获取视频尺寸的情况。设备验证记录见 `VALIDATION.md`。
