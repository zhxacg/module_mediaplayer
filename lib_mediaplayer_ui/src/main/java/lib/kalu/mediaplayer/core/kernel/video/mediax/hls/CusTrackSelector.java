package lib.kalu.mediaplayer.core.kernel.video.mediax.hls;

import android.content.Context;

import androidx.media3.common.C;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.adaptive.InitialAdaptiveThenFixedTrackSelectionFactory;
import lib.kalu.mediaplayer.util.DisplayRefreshRateUtils;

public class CusTrackSelector {
    public static final TrackSelector createTrackSelector(Context context, boolean adaptiveEnable) {


        // 开播后 自适应码率
        if (adaptiveEnable) {

            return new DefaultTrackSelector(
                    context,
                    DefaultTrackSelector.Parameters
                            .getDefaults(context)
                            .buildUpon()

                            // 最大 FPS
                            .setMaxVideoFrameRate(
                                    (int) DisplayRefreshRateUtils
                                            .getCurrentRefreshRate(context)
                            )

                            // 不强制最高码率
                            .setForceHighestSupportedBitrate(false)

                            // 字幕
                            .setPreferredTextRoleFlags(
                                    C.ROLE_FLAG_MAIN
                            )

                            // 音频
                            .setPreferredAudioRoleFlags(
                                    C.ROLE_FLAG_MAIN
                            )

                            // 视频
                            .setPreferredVideoRoleFlags(
                                    C.ROLE_FLAG_MAIN
                            )

                            // 防止不同 MIME 类型混合 Adaptive
                            .setAllowVideoMixedMimeTypeAdaptiveness(false)

                            // 防止不同 Decoder 支持混合 Adaptive
                            .setAllowVideoMixedDecoderSupportAdaptiveness(false)

                            // 防止非无缝 Adaptive
                            .setAllowVideoNonSeamlessAdaptiveness(false)

                            .build(),

                    new AdaptiveTrackSelection.Factory(
                            10_000_000,  // 升码率至少保持 10 秒
                            2_500_000,   // 降码率等待 2.5 秒
                            25_000_000,  // 切换后至少保留 25 秒
                            0.7F          // 只使用估算带宽的 70%
                    )
            );
        }
        // 开播后 禁止自动切换码率
        else {

            BandwidthMeter bandwidthMeter =
                    new DefaultBandwidthMeter.Builder(context)
                            .build();

            InitialAdaptiveThenFixedTrackSelectionFactory factory =
                    new InitialAdaptiveThenFixedTrackSelectionFactory(
                            bandwidthMeter
                    );

            DefaultTrackSelector.Parameters parameters = DefaultTrackSelector.Parameters
                    .getDefaults(context)
                    .buildUpon().build();

            return new DefaultTrackSelector(
                    context,
                    parameters,
                    factory
            );
        }
    }
}
