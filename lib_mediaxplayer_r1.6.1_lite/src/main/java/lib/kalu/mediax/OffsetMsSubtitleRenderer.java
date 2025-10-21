package lib.kalu.mediax;

import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.text.SubtitleDecoderFactory;
import androidx.media3.exoplayer.text.TextOutput;
import androidx.media3.exoplayer.text.TextRenderer;

@UnstableApi
public class OffsetMsSubtitleRenderer extends TextRenderer {

    private long offsetMs; // 时移偏移量（微秒，内部时间单位）

    public OffsetMsSubtitleRenderer(TextOutput output, @Nullable Looper outputLooper) {
        super(output, outputLooper);
    }

    public OffsetMsSubtitleRenderer(TextOutput output, @Nullable Looper outputLooper, SubtitleDecoderFactory subtitleDecoderFactory) {
        super(output, outputLooper, subtitleDecoderFactory);
    }

    @Override
    public void render(long positionUs, long elapsedRealtimeUs) {
        long formatOffsetUs = formatOffsetUs(positionUs);
        super.render(formatOffsetUs, elapsedRealtimeUs);
    }

    public final void appendOffsetMs(long offsetMs) {
        this.offsetMs += offsetMs;
    }

    private long formatOffsetUs(long positionUs) {
        if (offsetMs == 0) {
            return positionUs;
        } else {
            return positionUs + offsetMs * 1000;
        }
    }
}
