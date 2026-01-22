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