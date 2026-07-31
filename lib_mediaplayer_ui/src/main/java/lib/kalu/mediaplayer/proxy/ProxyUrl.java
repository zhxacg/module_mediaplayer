package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    default void formatOpenInit(boolean fromUser, String url) {
    }

    /**
     * 多层 嵌套m3u8
     *
     * @param mainUrl
     * @param subUrl
     * @return
     */
    default String formatMultivariantM3u8Url(String mainUrl, String subUrl) {
        return subUrl;
    }

    /**
     * 单层 m3u8
     *
     * @param url
     * @return
     */
    default String formatM3u8Url(String url) {
        return url;
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
