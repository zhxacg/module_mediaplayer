# 保护泛型
-keepattributes Signature

# 保护主动抛出异常
-keepattributes Exceptions

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# Lambda
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

# 保护注解
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
#-keep @interface * {
#    *;
#}

-keep class lib.kalu.exoplayer2.rtmp.RtmpClient{
    native <methods>;
}

-dontnote com.google.android.exoplayer2.ext.ffmpeg.FfmpegVideoRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.ffmpeg.FfmpegVideoRenderer {
    <init>(long, android.os.Handler, com.google.android.exoplayer2.video.VideoRendererEventListener, int);
}

-dontnote com.google.android.exoplayer2.decoder.VideoDecoderOutputBuffer
-keepclassmembers class com.google.android.exoplayer2.decoder.VideoDecoderOutputBuffer {
    public java.nio.ByteBuffer[] yuvPlanes;
    public void initForPrivateFrame(int, int);
    public boolean initForYuvFrame(int , int , int , int , int);
    public void init(long, int, java.nio.ByteBuffer);
}

#-keep class xx.xx.xx.*   本包下的类名保持
#-keep class xx.xx.xx.**  把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.**{*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;} 保持类名，同时保持里面的内容不被混淆

# exoplayer r2.19.1
-dontwarn com.google.android.exoplayer2.**
-dontwarn org.checkerframework.**
-dontwarn kotlin.annotations.jvm.**
-dontwarn javax.annotation.**
-dontwarn java.lang.ClassValue
-dontwarn java.lang.SafeVarargs
-dontwarn javax.lang.model.element.Modifier
-dontwarn sun.misc.Unsafe
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.j2objc.annotations.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keepclassmembernames class com.google.common.base.Function { *; }
-dontnote com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer {
  <init>(long, android.os.Handler, com.google.android.exoplayer2.video.VideoRendererEventListener, int);
}
-dontnote com.google.android.exoplayer2.ext.av1.Libgav1VideoRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.av1.Libgav1VideoRenderer {
  <init>(long, android.os.Handler, com.google.android.exoplayer2.video.VideoRendererEventListener, int);
}
-dontnote com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioSink);
}
-dontnote com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioSink);
}
-dontnote com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioSink);
}
-dontnote com.google.android.exoplayer2.source.dash.offline.DashDownloader
-keepclassmembers class com.google.android.exoplayer2.source.dash.offline.DashDownloader {
  <init>(com.google.android.exoplayer2.MediaItem, com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote com.google.android.exoplayer2.source.hls.offline.HlsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.hls.offline.HlsDownloader {
  <init>(com.google.android.exoplayer2.MediaItem, com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader {
  <init>(com.google.android.exoplayer2.MediaItem, com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote com.google.android.exoplayer2.source.dash.DashMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.dash.DashMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.rtsp.RtspMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.rtsp.RtspMediaSource$Factory {
  <init>();
}
-keepclassmembers class com.google.android.exoplayer2.upstream.RawResourceDataSource {
  public static android.net.Uri buildRawResourceUri(int);
}
-dontnote com.google.android.exoplayer2.ext.rtmp.RtmpDataSource
-keepclassmembers class com.google.android.exoplayer2.ext.rtmp.RtmpDataSource {
  <init>();
}
