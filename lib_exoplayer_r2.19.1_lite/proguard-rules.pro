# 指定外部模糊字典
-obfuscationdictionary proguard-rules-dict-mini.txt
# 指定class模糊字典
-classobfuscationdictionary proguard-rules-dict-mini.txt
# 指定package模糊字典
-packageobfuscationdictionary proguard-rules-dict-mini.txt


-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

# 保护泛型
-keepattributes Signature
# 保护主动抛出异常
-keepattributes Exceptions
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 保护注解
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
#-keep @interface * {
#    *;
#}


-dontwarn com.google.android.exoplayer2.**
-keep class com.google.android.exoplayer2.**{
    *;
}
-keep class com.google.android.exoplayer2.**$**{
    *;
}

-dontwarn lib.kalu.exoplayer2.**
-keep class lib.kalu.exoplayer2.renderers.**{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.exoplayer2.util.ExoLogUtil{
    public <methods>;
}
-keep class lib.kalu.exoplayer2.subtitle.OffsetMsTextRenderer{
    *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}