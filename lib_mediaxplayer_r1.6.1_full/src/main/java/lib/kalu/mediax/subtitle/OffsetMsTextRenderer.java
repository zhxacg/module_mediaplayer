package lib.kalu.mediax.subtitle;

import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.text.SubtitleDecoderFactory;
import androidx.media3.exoplayer.text.TextOutput;
import androidx.media3.exoplayer.text.TextRenderer;
import androidx.media3.extractor.text.SubtitleDecoder;

@UnstableApi
public class OffsetMsTextRenderer extends TextRenderer {

    private long offsetMs; // 时移偏移量（微秒，内部时间单位）

    public OffsetMsTextRenderer(TextOutput output, @Nullable Looper outputLooper) {
        super(output, outputLooper);
    }

    public OffsetMsTextRenderer(TextOutput output, @Nullable Looper outputLooper, SubtitleDecoderFactory subtitleDecoderFactory) {
        super(output, outputLooper, subtitleDecoderFactory);
    }

    // 开启对旧字幕格式支持（text/vtt）
    @Override
    public void experimentalSetLegacyDecodingEnabled(boolean legacyDecodingEnabled) {
        super.experimentalSetLegacyDecodingEnabled(true);
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

//    // 快退时会触发位置重置，此处刷新解码器状态
//    @Override
//    public void onPositionReset(long positionUs, boolean joining) {
//        super.onPositionReset(positionUs, joining);
//        // 清除解码器缓存（针对快退时需要重新解码更早字幕的场景）
//        try {
//            // 通过反射获取父类中存储的decoder字段（需根据ExoPlayer版本调整字段名）
//            java.lang.reflect.Field decoderField = TextRenderer.class.getDeclaredField("subtitleDecoder");
//            decoderField.setAccessible(true);
//            SubtitleDecoder subtitleDecoder = (SubtitleDecoder) decoderField.get(this);
//            if (subtitleDecoder != null) {
//                subtitleDecoder.flush(); // 刷新解码器，清除缓存的已解码数据
//            }
//        } catch (Exception e) {
//        }
//    }
}
