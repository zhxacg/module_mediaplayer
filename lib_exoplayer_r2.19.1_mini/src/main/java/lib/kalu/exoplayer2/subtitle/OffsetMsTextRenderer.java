package lib.kalu.exoplayer2.subtitle;

import android.os.Looper;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.text.SubtitleDecoderFactory;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;

import lib.kalu.exoplayer2.util.ExoLogUtil;

/**
 * 字幕时移， 快进 快退
 */
public class OffsetMsTextRenderer extends TextRenderer {

    private long offsetMs; // 时移偏移量（微秒，内部时间单位）

    public OffsetMsTextRenderer(TextOutput output, @Nullable Looper outputLooper, SubtitleDecoderFactory decoderFactory) {
        super(output, outputLooper, decoderFactory);
    }

    public OffsetMsTextRenderer(TextOutput output, @Nullable Looper outputLooper) {
        super(output, outputLooper);
    }

    @Override
    public void render(long positionUs, long elapsedRealtimeUs) {

        long formatOffsetUs = formatOffsetUs(positionUs);
        if (ExoLogUtil.DEBUG) {
            ExoLogUtil.log("OffsetMsTextRenderer -> render -> formatOffsetUs = " + formatOffsetUs + ", offsetMs = " + offsetMs + ", positionUs = " + positionUs + ", elapsedRealtimeUs = " + elapsedRealtimeUs);
        }

        super.render(formatOffsetUs, elapsedRealtimeUs);
    }

    public final void setOffsetMs(long offsetMs) {
        if (ExoLogUtil.DEBUG) {
            ExoLogUtil.log("OffsetMsTextRenderer -> setOffsetMs -> offsetMs = " + offsetMs);
        }
        this.offsetMs = offsetMs;
    }

    private long formatOffsetUs(long positionUs) {
        if (offsetMs == 0) {
            return positionUs;
        } else {
            return positionUs + offsetMs * 1000;
        }
    }
}
