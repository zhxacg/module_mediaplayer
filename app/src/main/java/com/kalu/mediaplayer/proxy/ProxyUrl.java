package com.kalu.mediaplayer.proxy;
import com.google.android.exoplayer2.upstream.DataSpec;

public class ProxyUrl implements lib.kalu.mediaplayer.bean.proxy.ProxyUrl {

    @Override
    public com.google.android.exoplayer2.upstream.DataSpec formatDataSpec(com.google.android.exoplayer2.upstream.DataSpec dataSpec) {
        return null;
    }

    @Override
    public String formatUrl(String baseUrl, String segmentUrl) {
        return segmentUrl + "?key=formatUrl";
    }
}
