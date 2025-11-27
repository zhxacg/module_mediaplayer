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

# libs
-keep class com.google.*

# sdk
-keep class lib.kalu.mediaplayer.PlayerSDK {
    public <fields>;
    public <methods>;
}

# bean
-keep class lib.kalu.mediaplayer.bean.** {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.bean.**$** {
    public <methods>;
}

# proxy
-keep class lib.kalu.mediaplayer.proxy.Proxy {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.proxy.Proxy$Builder {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.proxy.ProxyUrl {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.proxy.ProxyTrack {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.proxy.ProxyBuried {
    public <methods>;
}

# type
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$KernelType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$RenderType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$WindowType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$ScaleType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$SeekType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$DecoderType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$EventType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$SpeedType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$TrackType {
    *;
}

# listener
-keep class lib.kalu.mediaplayer.listener.** {
    public <fields>;
    public <methods>;
}

# util
-keep class lib.kalu.mediaplayer.util.UdpUtil {
    public <fields>;
    public <methods>;
}

# test
-keep class lib.kalu.mediaplayer.test.TestActivity {
    public <fields>;
    public <methods>;
}

# component
-keep class lib.kalu.mediaplayer.core.component.** {
     public <fields>;
     public <methods>;
}

# view
-keep class lib.kalu.mediaplayer.PlayerLayout {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.PlayerView{
    public <methods>;
}

#-keep class xx.xx.xx.*        本包下的类名保持
#-keep class xx.xx.xx.**       把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.** {*;}  把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;}      保持类名，同时保持里面的内容不被混淆