package lib.kalu.exoplayer2.subtitle;

import android.os.Looper;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.text.SubtitleDecoderFactory;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;

/**
 * 字幕时移， 快进 快退
 */
public class OffsetSubtitleRenderer extends TextRenderer {

    private long offsetUs; // 时移偏移量（微秒，内部时间单位）


    public OffsetSubtitleRenderer(TextOutput output, @Nullable Looper outputLooper, SubtitleDecoderFactory decoderFactory) {
        super(output, outputLooper, decoderFactory);
    }

    public OffsetSubtitleRenderer(TextOutput output, @Nullable Looper outputLooper) {
        super(output, outputLooper);
    }

    public final void setOffsetMs(long offsetMs) {
        this.offsetUs = offsetMs * 1000;
    }
}
