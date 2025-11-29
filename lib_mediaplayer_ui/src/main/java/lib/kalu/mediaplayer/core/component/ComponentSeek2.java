package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.progress.MultiSegmentProgressBar;

public class ComponentSeek2 extends RelativeLayout implements ComponentApi {

    public ComponentSeek2(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_seek2_root;
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_seek2;
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long progress, long duration) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSeek2 -> onUpdateProgress -> isFromUser = " + isFromUser + ", trySeeDuration = " + trySeeDuration + ", progress = " + progress + ", duration = " + duration);
        }

        MultiSegmentProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek2_pb);
        progressBar.setProgress((int) progress, (int) duration);

        boolean useCache = getPlayerView().isUseCache();
        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSeek2 -> onUpdateProgress -> useCache = " + useCache);
        }
        if (!useCache)
            return;

        List<HlsSpanInfo> segments = getPlayerView().getSegments();
        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSeek2 -> onUpdateProgress -> segments = " + segments);
        }
        if (null != segments) {
            int size = segments.size();
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentSeek2 -> onUpdateProgress -> segments.size = " + size);
            }
            int index = -1;
            long[] longs = new long[size * 2];
            for (int i = 0; i < size; i++) {
                HlsSpanInfo hlsSpanInfo = segments.get(i);
                if (null == hlsSpanInfo)
                    continue;
                long startTimeMs = hlsSpanInfo.getStartTimeMs();
                long endTimeMs = hlsSpanInfo.getEndTimeMs();
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentSeek2 -> onUpdateProgress -> i = " + i + ", startTimeMs = " + startTimeMs + ", endTimeMs = " + endTimeMs);
                }
                longs[++index] = startTimeMs;
                longs[++index] = endTimeMs;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentSeek2 -> onUpdateProgress -> longs.length = " + longs.length);
            }
            progressBar.addBufferSegments(longs);
        }
    }
}
