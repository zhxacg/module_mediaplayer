package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    void formatInit(String url);

    /**
     * 多层 嵌套m3u8
     * @param mainUrl
     * @param multivariantPath
     * @return
     */
    String formatMultivariantM3u8Url(String mainUrl, String multivariantPath);

    /**
     * 单层 m3u8
     * @param url
     * @return
     */
    String formatM3u8Url(String url);

    String formatSubtitleUrl(String url);

    String formatSegmentUrl(String url);

    String formatSegmentPath(String baseUrl, String segmentPath);
}
