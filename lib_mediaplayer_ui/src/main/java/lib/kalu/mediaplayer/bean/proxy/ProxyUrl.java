package lib.kalu.mediaplayer.bean.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    String formatBaseUrl(String url);

    String formatSegmentUrl(String baseUrl, String segmentUrl);
}
