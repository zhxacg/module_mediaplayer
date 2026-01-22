# 指定外部模糊字典
-obfuscationdictionary proguard-rules-dict-mini.txt
# 指定class模糊字典
-classobfuscationdictionary proguard-rules-dict-mini.txt
# 指定package模糊字典
-packageobfuscationdictionary proguard-rules-dict-mini.txt


-keep class lib.kalu.mediaplayer.core.component.** {
     public <methods>;
}


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

# view
-keep class lib.kalu.mediaplayer.PlayerLayout {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.PlayerView{
    public <methods>;
}

# renderers
-keep class lib.kalu.mediax.renderers.**{
    public <methods>;
}