# 指定外部模糊字典
-obfuscationdictionary proguard-rules-dict-mini.txt
# 指定class模糊字典
-classobfuscationdictionary proguard-rules-dict-mini.txt
# 指定package模糊字典
-packageobfuscationdictionary proguard-rules-dict-mini.txt


# mediax
-dontwarn androidx.media3.**
-keep class androidx.media3.** {
    *;
}

-dontwarn lib.kalu.mediax.**
-keep class lib.kalu.mediax.renderers.*{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediax.util.MediaLogUtil{
    public <methods>;
}
-keep class lib.kalu.mediax.subtitle.OffsetMsTextRenderer{
    public <methods>;
}