package lib.kalu.mediaplayer.bean.proxy;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    String formatOpenUrl(String url);

    String formatSegmentPath(String baseUrl, String segmentUrl);
}
