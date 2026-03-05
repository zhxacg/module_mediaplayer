package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    void formatInit(String url);

    String formatReferenceM3u8Url(String mainUrl, String referencePath);

    String formatM3u8Url(String url);

    String formatSubtitleUrl(String url);

    String formatSegmentUrl(String url);

    String formatSegmentPath(String baseUrl, String segmentPath);
}
