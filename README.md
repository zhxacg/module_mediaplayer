#   

#### support

|  type  | android mediaplayer | androidx media3 | exoplayer2 | ijkplayer | ffplayer | vlcplayer |
|:----:|:-------------------:|:---------------:|:----------:|:---------:|:--------:|:---------:|
| 音频硬解 |      &#10004;       |    &#10004;     |  &#10004;  | &#10004;  | &#10007; | &#10007;  |
| 音频软解 |      &#10007;       |    &#10004;     |  &#10004;  | &#10004;  | &#10004; | &#10004;  |
| 视频硬解 |      &#10004;       |    &#10004;     |  &#10004;  | &#10004;  | &#10007; | &#10007;  |
| 视频软解 |      &#10007;       |    &#10004;     |  &#10004;  | &#10004;  | &#10004; | &#10004;  |

#

#### ijkplayer

| type |   aac    |   h264   |   h265   |   http   |  https   |   dash   |
|:----:|:--------:|:--------:|:--------:|:--------:|:--------:|:--------:|
| mini | &#10004; | &#10004; | &#10007; | &#10004; | &#10007; | &#10007; |
| lite | &#10004; | &#10004; | &#10007; | &#10004; | &#10004; | &#10004; |
| full | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; | &#10004; |

#

#### androidx media3

|  type  | ffmpeg-audio | ffmpeg-video |   hls    |   dash   | smooth-streaming |   rtmp   |
|:----:|:------------:|:------------:|:--------:|:--------:|:----------------:|:--------:|
| mini |   &#10007;   |   &#10007;   | &#10004; | &#10007; |     &#10007;     | &#10007; |
| lite |   &#10007;   |   &#10007;   | &#10004; | &#10004; |     &#10004;     | &#10007; |
| full |   &#10004;   |   &#10004;   | &#10004; | &#10004; |     &#10004;     | &#10004; |

#

#### exoplayer v2

|  type  | ffmpeg-audio | ffmpeg-video |   hls    |   dash   | smooth-streaming |   rtmp   |
|:----:|:------------:|:------------:|:--------:|:--------:|:----------------:|:--------:|
| mini |   &#10007;   |   &#10007;   | &#10004; | &#10007; |     &#10007;     | &#10007; |
| lite |   &#10004;   |   &#10007;   | &#10004; | &#10004; |     &#10004;     | &#10004; |
| full |   &#10004;   |   &#10004;   | &#10004; | &#10004; |     &#10004;     | &#10004; |

#

#### 更新

```
2025-10-21
1. 新增：exoplayer2 media3 字幕时移api appSuntitleOffsetMs(long offsetMs)
```

```
2025-05-16
1. 新增：exoplayer2 media3 视频多轨道 音频多轨道 字幕多轨道 支持
```

```
2024-07-18
1. 新增：GLSurfaceView
```

```
2024-07-17
1. 新增：media3 音频软解 视频软解 
2. 新增：exoplayer2 音频软解 视频软解 
```

```
2024-07-15
1. 更新：androidx media3 v1.4.0
```

```
2024-07-13
1. 更新：画面比例 自动|全屏|原始|1:1|4:3|5:4|16:9|16:10
```

```
2024-07-10
1. 增加：选集demo
```

```
2024-06-17
1. 增加：试看demo;
2. 增加：续播demo;
```

```
2024-05-30
1. 更新：视频快进快退逻辑；
```

```
2024-05-24
1. 更新：视频切换逻辑，解决ijk切换视频源黑屏；
```

```
2023-11-15
1. 新增：倍速；
2. 新增：试播；
```

```
2023-09-20
1. 新增：音频播放器
```

```
2023-08-21
1. 新增：exoplayer_r2.19.1
```

```
2023-08-13
1. 新增：ijkplayer-ffmpeg3.4.13
2. 新增：ijkplayer-ffmpeg3.4.13_full
```

```
2023-08-09
1. 新增：exoplayer_r2.19.0
```

```
2023-07-02
1. 解决：ijk-mediacodec+surfaceview切换大小屏黑屏
```

```
2023-06-25
1. 更新：exoplayer软解支持aac音频
```

```
2023-06-09
1. 新增：ijkplayer支持dash【*_full版本】
2. 更新：ijkplayer-ffmpeg3.4.12
3. 更新：ijkplayer-ffmpeg4.4.4
```

```
2023-03-22
1. 更新：exoplayer-2.18.5
```

```
2023-03-14
1. 新增：ffplayer_1.0
```

```
2023-03-10
1. 更新：vlcplayer-r3.5.1
```

```
2023-02-22
1. 更新：exoplayer-r2.18.3
```

```
2023-02-03
1. 更新：vlc-3.5.1
```

```
2023-01-31
1. 新增：音频播放器，可选exoplayer、mediaplayer
```

```
2023-01-12
1. 优化：libijkplayer-ffmpeg.so编译脚本
```

```
2023-01-11
1. 新增：exoplayer-ffmpeg视频软解
2. 优化：进度条更新【TextureView基于回调方法，SurfaceView基于Handler消息】
```

```
2022-12-07
1. 修改：exoplayer-udp-超时时长[com/google/android/exoplayer2/upstream/UdpDataSource.java-55]
```

```
2022-12-02
1. 新增：视频网络缓冲, 显示提示loading和网速
```

```
2022-11-25
1. 新增：exoplayer-ffmpeg音频软解
```

```
2022-11-24
1. exoplayer-2.18.2
```

```
2022-11-09
1. ijk增加log开关
```

```
2022-11-03
1. ijk增加armv7、arm64、x86、x86_64
```

```
2022-10-27
1. 新增cmake编译ijkplayer
```

```
2022-09-19
1. fix bug => exoplayer 子线程获取时长exception【com.google.android.exoplayer2.ExoPlayerImpl -> verifyApplicationThread】
```

```
2022-09-02
1.新增：全屏播放
2.新增：小窗播放
```

```
2022-08-05
1.新增：针对RecyclerView自动回收销毁机制, 增加autoRelease方法
```

```
2022-08-02
1.新增：支持试播，指定开始时间、试播时长
2.新增：支持视频指定配音音频，配音音频和原音音频可以切换
```

```
2022-07-21
1.新增vlc-r3.4.9
```

```
2022-06-29
1.更新exoplayer-r2.18.0
2.编译ijk-so
```

```
2022-06-02
1.解决快进bug
```

```
2022-02-19
1.更新ffmpeg4.0版本, ijk软解
```

```
2022-02-11
1. 新增ijkplayer硬解
```

```
2021-12-19
1. 解决android6.0 crash [指定版本=>'com.google.guava:guava:30.1-android']
```

```
2021-12-15
1. 更新exoplayer2.16.1
```

```
2021-11-23
1. 优化ui
2. 删除冗余模块
3. 添加rtsp支持
```

```
2021-09-29
1. 更新exoplayer-2.15.0
2. 重构lib_mediaplayer_ui模块
```

#  

#### 初始化参数

```
PlayerSDK.init()
                // 日志开关
                .setLog(true)
                // 数据埋点（监听播放器操作日志）
                .setBuriedEvent(null)
                // 播放器类型（MediaPlayer Media3Player ExoPlayer IjkPLayer）
                .setKernelType(PlayerType.KernelType.IJK)
                // 渲染类型（TextuteView SurafecView）
                .setRenderType(PlayerType.RenderType.SURFACE_VIEW)
                // 解码器类型（仅针对 Media3Player ExoPlayer IjkPLayer）
                .setDecoderType(PlayerType.DecoderType.IJK_ALL_FFMPEG)
                // 画面比例（自动 全屏 原始 1:1 4:3 5:4 16:9 16:10）
                .setScaleType(PlayerType.ScaleType.AUTO)
                // 超时时间（默认20s）
                .setConnectTimeout(20000)
                // 缓冲超时重播（默认false）
                .setBufferingTimeoutRetry(false)
                // 播放器每次播放器都销毁（默认false）
                .setInitRelease(false)
                // 播放器生命周期自动销毁（默认true）
                .setSupportAutoRelease(false)
                // 试看（默认关闭）
                .setTrySeeDuration(0L)
                // 缓存类型（默认关闭, 仅针对 ExoPlayer）
                .setCacheType(PlayerType.CacheType.DEFAULT)
                // 缓存类型（默认内部）
                .setCacheLocal(PlayerType.CacheLocalType.DEFAULT)
                // 缓存大小
                .setCacheSize(PlayerType.CacheSizeType.DEFAULT)
                // 缓存文件夹
                .setCacheDirName(null)
                // 快进类型（仅针对 MediaPlayer ExoPlayer）
                .setSeekType(PlayerType.SeekType.DEFAULT)
                // 网络类型（仅针对 ExoPlayer）
                .setNetType(PlayerType.NetType.EXO_OKHTTP)
                .build();
```

#

#### 起播参数

```
// step1 参数
        StartArgs build = new StartArgs.Builder()
                // 必传：视频url
                .setUrl("")
                // 视频title
                .setTitle("")
                // 渲染类型（TextuteView SurafecView）
                .setRenderType(PlayerType.RenderType.SURFACE_VIEW)
                // 起播快进
                .setSeek(0L)
                // 单片循环
                .setLooping(false)
                // 静音
                .setMute(false)
                // 媒资是直播，则获取不到时长duration
                .setLive(false)
                // 自动播放
                .setPlayWhenReady(true)
                // 外挂字幕
                .setSubtitleUrl("")
                // 播放器生命周期自动销毁（默认true）
                .setSupportAutoRelease(true)
                // 试看
                .setTrySeeDuration(0L)
                // 选集, 补充数据
                .setExtra(null);
        // step2 播放
        PlayerLayout.start(build);
```

#

#### 资料

```
https://exoplayer.dev/
https://github.com/google/ExoPlayer
https://mvnrepository.com/search?q=exoplayer
https://github.com/bilibili/ijkplayer
https://github.com/bilibili/FFmpeg/tags
https://code.videolan.org/videolan/vlc-android
https://mvnrepository.com/artifact/org.videolan.android/libvlc-all
```

#

#### 优化

```
adb shell am start -n com.kalu.mediaplayer/com.kalu.mediaplayer.MainActivity
https://hejunlin.blog.csdn.net/article/details/57075026?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-57075026-blog-80149176.235%5Ev31%5Epc_relevant_increate_t0_download_v2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-57075026-blog-80149176.235%5Ev31%5Epc_relevant_increate_t0_download_v2&utm_relevant_index=2
```
