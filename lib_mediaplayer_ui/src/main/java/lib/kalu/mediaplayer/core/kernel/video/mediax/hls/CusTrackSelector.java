package lib.kalu.mediaplayer.core.kernel.video.mediax.hls;

import android.content.Context;

import androidx.media3.common.C;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;

import lib.kalu.mediaplayer.util.DisplayRefreshRateUtils;

public class CusTrackSelector {
    public static final TrackSelector createTrackSelector(Context context, boolean adaptiveEnable) {

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
                .setAllowVideoNonSeamlessAdaptiveness(false)

                // 1. 关键：禁止视频尺寸发生巨变时进行“无缝切换”
                // 如果高低分辨率宽高比或尺寸差异过大，强制重置/Flush 解码器，防止旧纹理残留
                .setAllowVideoMixedDecoderSupportAdaptiveness(false)

                // 2. 如果使用的是 TextureView，请尝试限制最大输出尺寸
                // 确保渲染 Surface 在初始化时就分配好足够大的 Buffer，避免分辨率缩小后底板露出来
                .setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE); // 根据实际最高清晰度设置

        // ----------------------------------------------------------------
        // 2. 模式分支判断
        // ----------------------------------------------------------------

        // 【开启 ABR 模式】：开启自适应码率，播放器会根据实时网络带宽动态升降画质
        if (adaptiveEnable) {
            // 允许根据网络状况自动选码率，不强制锁定最高码率
            parametersBuilder.setForceHighestSupportedBitrate(false);

            // 定制平滑切换策略的 ABR 工厂（针对直播/高画质稳定性进行防抖调优）
            AdaptiveTrackSelection.Factory adaptiveFactory = new AdaptiveTrackSelection.Factory(
                    10_000_000,  // minDurationForQualityIncreaseMs：升码率延迟（至少保持当前画质 10 秒且网络持续达标才允许升码率，防止网络微小抖动频繁升码率）
                    2_500_000,   // maxDurationForQualityDecreaseMs：降码率等待时间（网络变差时等待 2.5 秒后再降码率，避免偶尔的网络突发丢包误判）
                    25_000_000,  // minDurationToRetainAfterDiscardMs：高码率数据保留时长（切换码率后，缓冲区内至少保留 25 秒已下载数据，确保切换顺畅）
                    0.7F         // bandwidthFraction：带宽预留系数（仅使用估算网络总带宽的 70% 来选择码率，预留 30% 带宽作为安全缓冲，防止网络波动导致卡顿）
            );

            // 返回配置了自定义 ABR 策略的 TrackSelector
            return new DefaultTrackSelector(context, parametersBuilder.build(), adaptiveFactory);

        }
        // 【禁用 ABR 模式】：锁定固定码率（方案 A：使用官方推荐的参数配置，替代已废弃的 FixedTrackSelection）
        else {
            // 强制选择设备支持的最高码率轨道，且后续播放过程中禁止 ABR 自动降码率切换
            parametersBuilder.setForceHighestSupportedBitrate(true);

            // 使用默认的 DefaultTrackSelector 构造函数（底层会自动将选定的轨道固定）
            return new DefaultTrackSelector(context, parametersBuilder.build());
        }
    }
}
