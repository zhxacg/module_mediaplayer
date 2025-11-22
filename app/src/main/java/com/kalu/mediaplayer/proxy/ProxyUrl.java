package com.kalu.mediaplayer.proxy;

import android.net.Uri;

import lib.kalu.mediaplayer.util.LogUtil;

public class ProxyUrl implements lib.kalu.mediaplayer.proxy.ProxyUrl {

    @Override
    public String formatOpenUrl(String url) {
        LogUtil.log("ProxyUrl -> formatOpenUrl -> url = " + url + ", thread = " + Thread.currentThread().getName());
//        return url + "?key=name";
        return url;
    }

    @Override
    public String formatSegmentPath(String baseUrl, String segmentUrl) {
        LogUtil.log("ProxyUrl -> formatSegmentPath -> baseUrl = " + baseUrl + ", segmentUrl = " + segmentUrl + ", thread = " + Thread.currentThread().getName());
        String key = Uri.parse(baseUrl).getQueryParameter("key");
//        return segmentUrl + "?key=" + key + "&value=zm";
        return segmentUrl;
    }
}
