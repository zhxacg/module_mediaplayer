# 指定外部模糊字典
-obfuscationdictionary proguard-rules-dict-mini.txt
# 指定class模糊字典
-classobfuscationdictionary proguard-rules-dict-mini.txt
# 指定package模糊字典
-packageobfuscationdictionary proguard-rules-dict-mini.txt


-keep class lib.kalu.mediaplayer.core.component.** {
     public <methods>;
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
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$ParserType {
    *;
}
-keep @interface lib.kalu.mediaplayer.bean.type.PlayerType$ScreenOrientation {
    *;
}