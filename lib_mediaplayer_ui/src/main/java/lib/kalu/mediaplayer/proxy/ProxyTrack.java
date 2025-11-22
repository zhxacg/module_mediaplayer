package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;
import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.TrackInfo;

public interface ProxyTrack extends Serializable {
    void formatVideoTrackInfo(List<TrackInfo> tracksList, StartArgs startArgs);

    void formatAudioTrackInfo(List<TrackInfo> tracksList, StartArgs startArgs);

    void formatSubtitleTrackInfo(List<TrackInfo> tracksList, StartArgs startArgs);
}
