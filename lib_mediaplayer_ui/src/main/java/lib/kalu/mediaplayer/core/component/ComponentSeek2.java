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
            LogUtil.log("ComponentSeek2 => onUpdateProgress => isFromUser = " + isFromUser + ", trySeeDuration = " + trySeeDuration + ", progress = " + progress + ", duration = " + duration);
        }

        MultiSegmentProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek2_pb);
        progressBar.setProgress((int) progress, (int) duration);

        List<HlsSpanInfo> segments = getPlayerView().getSegments();
        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSeek2 => initMultiSegmentProgress => segments = " + segments);
        }
        if (null != segments) {
            int size = segments.size();
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentSeek2 => initMultiSegmentProgress => segments.size = " + size);
            }
            int index = -1;
            long[] longs = new long[size * 2];
            for (int i = 0; i < size; i++) {
                HlsSpanInfo hlsSpanInfo = segments.get(i);
                if (null == hlsSpanInfo)
                    continue;
                long relativeStartTimeUs = hlsSpanInfo.getRelativeStartTimeUs();
                long durationUs = hlsSpanInfo.getDurationUs();
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentSeek2 => initMultiSegmentProgress => i = " + i + ", relativeStartTimeUs = " + relativeStartTimeUs + ", durationUs = " + durationUs);
                }
                longs[++index] = (relativeStartTimeUs) / 1000;
                longs[++index] = (relativeStartTimeUs + durationUs) / 1000;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentSeek2 => initMultiSegmentProgress => longs.length = " + longs.length);
            }
            progressBar.addBufferSegments(longs);
        }
    }

    private void test() {

//        //
//        Handler handler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                if (msg.what == 11) {
//                    //
//                    Message message = Message.obtain();
//                    message.what = 12;
//                    message.arg1 = 100;
//                    sendMessageDelayed(message, 100);
//                } else if (msg.what == 12) {
//                    //
//                    MultiSegmentProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek2_pb);
//                    progressBar.setProgress(msg.arg1, 100_000);
//                    //
//                    Message message = Message.obtain();
//                    message.what = 12;
//                    message.arg1 = (msg.arg1 + 100);
//                    sendMessageDelayed(message, 100);
//                } else if (msg.what == 21) {
//                    //
//                    Message message = Message.obtain();
//                    message.what = 22;
//                    int start = 0;
//                    int end = start + new Random().nextInt(1000);
//                    message.arg1 = start;
//                    message.arg2 = end;
//                    sendMessageDelayed(message, 100);
//                } else if (msg.what == 22) {
//                    //
//                    MultiSegmentProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek2_pb);
//                    progressBar.addBufferSegment(msg.arg1, msg.arg2);
//                    //
//                    Message message = Message.obtain();
//                    message.what = 22;
//                    int start = msg.arg1 + new Random().nextInt(1000);
//                    int end = start + new Random().nextInt(1000);
//                    message.arg1 = start;
//                    message.arg2 = end;
//                    sendMessageDelayed(message, 100);
//                }
//            }
//        };
//
//        // 模拟缓存进度
//        handler.sendEmptyMessageDelayed(21, 100);
//
//        // 模拟播放进度
//        handler.sendEmptyMessageDelayed(11, 100);
    }
}
