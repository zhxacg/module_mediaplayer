package lib.kalu.mediaplayer.core.kernel.video.mediax;

import android.content.Context;

import androidx.media3.common.C;
import androidx.media3.common.util.Clock;
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.LivePlaybackSpeedControl;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.DefaultAllocator;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import lib.kalu.mediaplayer.bean.configuration.AdaptiveConfiguration;
import lib.kalu.mediaplayer.bean.configuration.BufferConfiguration;
import lib.kalu.mediaplayer.bean.configuration.LiveConfiguration;
import lib.kalu.mediaplayer.util.DisplayRefreshRateUtils;
import lib.kalu.mediaplayer.util.LogUtil;

public class VideoMediaxConfig {

    private static final String TAG = "CusTrackSelector";

    public static final DefaultBandwidthMeter createDefaultBandwidthMeter(Context context) {
        return new DefaultBandwidthMeter.Builder(context)
                .build();
    }

    public static final LivePlaybackSpeedControl createLivePlaybackSpeedControl(LiveConfiguration config) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "createLivePlaybackSpeedControl -> config = " + config);
        }

        return new DefaultLivePlaybackSpeedControl.Builder()
                // 兜底最小播放速度：当无法计算动态速度时，使用的保底最小速度（最终 minPlaybackSpeed 会等于该值）
                .setFallbackMinPlaybackSpeed(config.getFallbackMinPlaybackSpeed())
                // 兜底最大播放速度：同上，保底最大速度（最终 maxPlaybackSpeed 会等于该值）
                .setFallbackMaxPlaybackSpeed(config.getFallbackMaxPlaybackSpeed())
                // 速度更新最小间隔：两次速度调整的最小时间差（避免频繁变速）
                .setMinUpdateIntervalMs(config.getMinUpdateIntervalMs())
                // 比例控制因子：速度调整的 “灵敏度”—— 延迟差值越大，速度调整幅度越大（核心算法参数）
                .setProportionalControlFactor(config.getProportionalControlFactorUs())
                // 匀速阈值：直播延迟误差小于该值时，使用 1.0f 匀速播放（不调整速度）
                .setMaxLiveOffsetErrorMsForUnitSpeed(config.getMaxLiveOffsetErrorUsForUnitSpeed())
                // 缓冲保护阈值：当直播延迟低于「目标延迟 - 该值」时，触发减速，避免缓冲不足导致卡顿
                .setTargetLiveOffsetIncrementOnRebufferMs(config.getTargetLiveOffsetIncrementOnRebufferUs())
                // 最小延迟平滑因子：对 “最小可播放延迟” 进行平滑处理的系数（避免延迟波动导致速度频繁变化）
                .setMinPossibleLiveOffsetSmoothingFactor(config.getMinPossibleLiveOffsetSmoothingFactor()).build();
    }

    public static final LoadControl createLoadControl(BufferConfiguration config) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "createLoadControl -> config = " + config);
        }

        return new DefaultLoadControl.Builder()
                // 盒子内存较小，分配粒度设为 32KB，减少内存碎片和峰值占用；手机内存充足，设为 64KB
                .setAllocator(new DefaultAllocator(true, config.getAvailableCount()))

                // 设置缓冲区门限：[minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs]
                // 1) minBufferMs: 最小缓冲区。已缓存时长低于此值时，触发网络重新加载数据。
                // 2) maxBufferMs: 最大缓冲区。已缓存时长达到此值时，停止主动加载，防止占用过多内存。
                //    - BOX (TV 盒子): 20s ~ 30s，防止低配 TV 盒子爆 OOM。
                //    - PHONE (手机):  30s ~ 50s，利用高带宽与大内存提升拖动/快进体验。
                // 3) bufferForPlaybackMs: 启播门限。首次播放或 Seek 后的最小缓冲量。
                //    - BOX (TV 盒子): 2500ms，为低端解码器与硬件渲染 Surface 预留充裕时间，防止黑屏卡顿。
                //    - PHONE (手机):  1000ms，追求极致秒开体验。
                // 4) bufferForPlaybackAfterRebufferMs: 卡顿恢复门限。二次缓冲后恢复播放的最小缓存量。
                //    - BOX (TV 盒子): 4000ms，牺牲少量恢复速度，保证恢复后不再连续卡顿。
                //    - PHONE (手机):  2000ms，快速恢复播放。
                .setBufferDurationsMs(
                        config.getMinBufferMs(),
                        config.getMaxBufferMs(),
                        config.getBufferForPlaybackMs(),
                        config.getBufferForPlaybackAfterRebufferMs()
                )

                // 优先保证首帧/卡顿恢复渲染（保留默认行为 true）
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();
    }

    public static final TrackSelector createTrackSelector(Context context, AdaptiveConfiguration config) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "createTrackSelector -> config = " + config);
        }

        // 1. 获取系统默认配置并初始化 Parameters 构建器
        DefaultTrackSelector.Parameters.Builder parametersBuilder = DefaultTrackSelector.Parameters
                .getDefaults(context)
                .buildUpon()
                // 【帧率限制】：限制最大视频帧率不超过当前屏幕的物理刷新率（如 60Hz/120Hz）
                // 作用：避免解码和渲染高于屏幕硬件支持的超高帧率流，节省系统 CPU/GPU 资源与功耗
                .setMaxVideoFrameRate((int) DisplayRefreshRateUtils.getCurrentRefreshRate(context))

                // 【主轨道优先】：设置字幕、音频、视频的默认选择偏好为“主轨道”（Main Role）
                // 作用：当流资源包含多音轨/多字幕（如主音轨、旁白解说、辅助音轨）时，优先选择官方主音轨/主视频
                .setPreferredTextRoleFlags(C.ROLE_FLAG_MAIN)
                .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN)
                .setPreferredVideoRoleFlags(C.ROLE_FLAG_MAIN)

                // 【无缝自适应限制 1】：禁止跨不同 MIME 类型的视频轨道进行 ABR 动态切换（例如 H.264 与 H.265 之间）
                // 作用：防止码率切换时因视频编码格式变更导致硬解码器重建（Codec Re-init），从而引发黑屏或卡顿
                .setAllowVideoMixedMimeTypeAdaptiveness(false)

                // 【无缝自适应限制 2】：禁止跨不同解码器支持级别的轨道进行切换（例如硬件解码与软件解码之间）
                .setAllowVideoMixedDecoderSupportAdaptiveness(false)

                // 【无缝自适应限制 3】：禁止非无缝切换（Non-seamless）
                // 作用：强制要求 ABR 只能在关键帧（GOP 边界）无缝衔接时切换，极大地保证了直播/点播画面切换时的平滑度
                .setAllowVideoNonSeamlessAdaptiveness(false);

        // ----------------------------------------------------------------
        // 2. 模式分支判断
        // ----------------------------------------------------------------

        // 【开启 ABR 模式】：开启自适应码率，播放器会根据实时网络带宽动态升降画质
        if (config != null && config.isEnable()) {
            // 允许根据网络状况自动选码率，不强制锁定最高码率
            parametersBuilder.setForceHighestSupportedBitrate(false);

            // 使用配置类中的各个参数构造 Factory，控制 ABR（自适应码率）升降档与缓存清理策略
            AdaptiveTrackSelection.Factory adaptiveFactory = new AdaptiveTrackSelection.Factory(
                    // 1. 升码率门限（ms）：已缓存时长必须大于此值，才允许向更高清晰度/码率切换，防止盲目升档导致卡顿
                    config.getMinDurationForQualityIncreaseMs(),

                    // 2. 降码率等待时长（ms）：已缓存时长小于此值时，才因网络变差降低码率；缓存充裕时先消耗现有缓存不急着降档
                    config.getMaxDurationForQualityDecreaseMs(),

                    // 3. 丢弃缓存保留底线（ms）：切更高画质废弃旧低画质缓存时，当前播放点之后必须保留的最小时长，防止新画质还没下完就卡顿
                    config.getMinDurationToRetainAfterDiscardMs(),

                    // 4. 允许丢弃的最大视频宽度（px）：只有宽度小于等于此值（如 1280）的轨道缓存才允许被废弃重下，保护高分辨率缓存不浪费流量
                    config.getMaxWidthToDiscard(),

                    // 5. 允许丢弃的最大视频高度（px）：只有高度小于等于此值（如 720）的轨道缓存才允许被废弃重下
                    config.getMaxHeightToDiscard(),

                    // 6. 带宽利用率折扣系数（0.0~1.0）：仅使用估算网络带宽的指定比例（如 0.7f 代表 70%）匹配码率，预留余量应对网络突发抖动
                    config.getBandwidthFraction(),

                    // 7. 直播升码率比例门限（0.0~1.0）：直播专享，当前已缓存时长与距离直播前沿（Live Edge）总长之比超过该阈值时，才允许升码率
                    config.getBufferedFractionToLiveEdgeForQualityIncrease(),

                    // 8. 系统时钟：提供标准时间戳基准，通常传 Clock.DEFAULT
                    Clock.DEFAULT
            );

            // 返回配置了自定义 ABR 策略的 TrackSelector
            return new DefaultTrackSelector(context, parametersBuilder.build(), adaptiveFactory);

        }
        // 【禁用 ABR 模式】：锁定固定码率
        else {
            // 强制选择设备支持的最高码率轨道，且后续播放过程中禁止 ABR 自动降码率切换
            parametersBuilder.setForceHighestSupportedBitrate(true);

            // 使用默认的 DefaultTrackSelector 构造函数
            return new DefaultTrackSelector(context, parametersBuilder.build());
        }
    }
}