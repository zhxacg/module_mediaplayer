package lib.kalu.mediaplayer.bean.proxy;

import com.google.android.exoplayer2.upstream.DataSpec;

import java.io.Serializable;

public interface ProxyUrl extends Serializable {

    DataSpec formatDataSpec(DataSpec dataSpec);

    String formatUrl(String baseUrl, String segmentUrl);
}
