package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    void formatInit();

    String formatM3u8Url(String url);

    String formatSubtitleUrl(String url);

    String formatSegmentUrl(String url);

    String formatSegmentPath(String baseUrl, String segmentPath);

    String formatMultivariantReferencePath(String baseUrl, String referencePath);
}
