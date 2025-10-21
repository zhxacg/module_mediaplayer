# 指定外部模糊字典
-obfuscationdictionary proguard-rules-dict-mini.txt
# 指定class模糊字典
-classobfuscationdictionary proguard-rules-dict-mini.txt
# 指定package模糊字典
-packageobfuscationdictionary proguard-rules-dict-mini.txt


-dontwarn lib.kalu.exoplayer2.**
-dontwarn com.google.android.exoplayer2.**

-keep class com.google.android.exoplayer2.**{
    public <fields>;
    public <methods>;
    protected <methods>;
}
-keep class com.google.android.exoplayer2.**$**{
    public <fields>;
    public <methods>;
    protected <methods>;
}

-keep class lib.kalu.exoplayer2.renderers.*{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.exoplayer2.utils.*{
    public <methods>;
}
-keep class lib.kalu.exoplayer2.subtitle.OffsetMsTextRenderer{
    *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}