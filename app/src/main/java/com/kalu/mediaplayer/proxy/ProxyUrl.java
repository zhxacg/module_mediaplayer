package com.kalu.mediaplayer.proxy;

public class ProxyUrl implements lib.kalu.mediaplayer.bean.proxy.ProxyUrl {

    @Override
    public String formatBaseUrl(String url) {
        return "";
    }

    @Override
    public String formatSegmentUrl(String baseUrl, String segmentUrl) {
        return segmentUrl + "?key=formatUrl";
    }
}
