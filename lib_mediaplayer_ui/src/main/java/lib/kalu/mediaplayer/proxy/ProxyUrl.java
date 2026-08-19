package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    default void formatOpenUrl(String url) {
    }

    /**
     * 单层 m3u8
     */
    default String formatM3u8Url(String url) {
        return url;
    }

    /**
     * 多层 嵌套m3u8
     */
    default String formatChildM3u8Url(String url, String childUrl) {
        return childUrl;
    }

    default String formatSegmentUrl(String url) {
        return url;
    }

    default String formatSegmentPath(String baseUrl, String segmentPath) {
        return segmentPath;
    }

    default String formatSubtitleUrl(String url) {
        return url;
    }
}
