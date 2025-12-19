package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
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
        return R.layout.lib_mp_component_seek2;
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

        long[] segmentsMs = getPlayerView().getSegmentsMs();
        progressBar.addBufferSegments(segmentsMs);
    }
}
