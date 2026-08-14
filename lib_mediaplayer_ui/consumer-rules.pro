-dontwarn lib.kalu.mediaplayer.**
-dontwarn lib.kalu.ffplayer.**
-dontwarn lib.kalu.vlc.**
-dontwarn lib.kalu.exoplayer2.**
-dontwarn lib.kalu.ijkplayer.**
-dontwarn lib.kalu.media3.**

-dontwarn com.google.common.util.concurrent.internal.InternalFutureFailureAccess
-dontwarn com.google.common.util.concurrent.internal.InternalFutures
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.DoNotCall
-dontwarn com.google.errorprone.annotations.DoNotMock
-dontwarn com.google.errorprone.annotations.concurrent.LazyInit
-dontwarn com.google.j2objc.annotations.RetainedWith
-dontwarn javax.annotation.CheckForNull

# 保护主动抛出异常
#-keepattributes Exceptions
#-keepattributes Exceptions,SourceFile,LineNumberTable

## 移除所有log
#-dontwarn lib.kalu.mediaplayer.util.LogUtil
#-dontwarn lib.kalu.mediax.util.MediaLogUtil
#-dontwarn lib.kalu.exoplayer2.util.ExoLogUtil
#-dontwarn lib.kalu.vlc.util.VlcLogUtil
#-dontwarn lib.kalu.ijkplayer.util.IjkLogUtil
#-assumenosideeffects class lib.kalu.mediaplayer.util.LogUtil {
#    public final void log(java.lang.String);
#    public final void log(java.lang.String, java.lang.String);
#    public final void log(java.lang.String, java.lang.Throwable);
#    public final void log(java.lang.String, java.lang.String, java.lang.Throwable);
#}
#-assumenosideeffects class lib.kalu.mediax.util.MediaLogUtil {
#    public final void log(java.lang.String);
#    public final void log(java.lang.String, java.lang.Throwable);
#}
#-assumenosideeffects class lib.kalu.exoplayer2.util.ExoLogUtil {
#    public final void log(java.lang.String);
#    public final void log(java.lang.String, java.lang.Throwable);
#}
#-assumenosideeffects class lib.kalu.vlc.util.VlcLogUtil {
#    public final void log(java.lang.String);
#    public final void log(java.lang.String, java.lang.Throwable);
#}
#-assumenosideeffects class lib.kalu.ijkplayer.util.IjkLogUtil {
#    public final void log(java.lang.String);
#    public final void log(java.lang.String, java.lang.Throwable);
#}
#
#-assumenosideeffects class android.util.Log {
#    public static int d(...);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int e(...);
#    public static int wtf(...);
#}

# 保护泛型
-keepattributes Signature

# 保护注解
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
#-keep @interface * {
#    *;
#}

#-keep class xx.xx.xx.*        本包下的类名保持
#-keep class xx.xx.xx.**       把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.** {*;}  把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;}      保持类名，同时保持里面的内容不被混淆