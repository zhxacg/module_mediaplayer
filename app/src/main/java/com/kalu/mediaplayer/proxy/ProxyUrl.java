package com.kalu.mediaplayer.proxy;

import android.net.Uri;

import lib.kalu.mediaplayer.util.LogUtil;

public class ProxyUrl implements lib.kalu.mediaplayer.proxy.ProxyUrl {

    @Override
    public String formatOpenUrl(String url) {
        return url;
    }

    @Override
    public String formatSegmentPath(String baseUrl, String segmentUrl) {
        return segmentUrl;
    }
}
