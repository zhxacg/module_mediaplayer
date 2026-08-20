package lib.kalu.mediaplayer;

import androidx.media3.common.util.Util;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayerConst {

    // log 日志 默认关闭
    public static boolean DEFAULT_LOG_ENABLE = false;

    // 默认超时 60s
    public static int DEFAULT_CONNECT_TIMEOUT_MS = 60_000;

    // 默认 设备类型盒子
    @PlayerType.DeviceType.Value
    public static int DEFAULT_TYPE_DEVICE = PlayerType.DeviceType.BOX;


    // 默认解析 视频 音频 字幕
    @PlayerType.ParserType.Value
    public static int DEFAULT_TYPE_PRASE = PlayerType.ParserType.VIDEO_AUDIO_SUBTITLE;

    // 默认外挂 视频轨道 分辨率参数信息
    @PlayerType.ResolutionType.Value
    public static String DEFAULT_TYPE_RESOLUTION = PlayerType.ResolutionType.DEFAULT;

    // 默认 视频播放器内核
    @PlayerType.KernelType.Value
    public static int DEFAULT_TYPE_KERNEL = PlayerType.KernelType.ANDROID;

    // 默认 视频渲染类型
    @PlayerType.RenderType.Value
    public static int DEFAULT_TYPE_RENDER = PlayerType.RenderType.SURFACE_VIEW;

    // 解码器类型
    @PlayerType.DecoderType.Value
    public static int DEFAULT_TYPE_DECODER = PlayerType.DecoderType.DEFAULT;

    // 视频缩放比例
    @PlayerType.ScaleType.Value
    public static int DEFAULT_TYPE_SCALE = PlayerType.ScaleType.DEFAULT;

    // 旋转角度
    @PlayerType.RotationType.Value
    public static int DEFAULT_TYPE_ROTATION = PlayerType.RotationType.DEFAULT;

    // 快进参数
    @PlayerType.SeekType.Value
    public static int DEFAULT_TYPE_SEEK = PlayerType.SeekType.DEFAULT;

    // 音频播放器内核
    @PlayerType.KernelType.Value
    public static int DEFAULT_TYPE_KERNEL_EXTERNAL_AUDIO = PlayerType.KernelType.DEFAULT;

    @PlayerType.StreamType.Value
    public static int DEFAULT_STREAM_TYPE = PlayerType.StreamType.DEFAULT;


    /**
     * 自适应码率算法（ABR）配置
     * 提供 [低端盒子 BOX] 与 [移动手机 PHONE] 两套性能优化参数
     */
    public static class AdaptiveConfiguration {

        public static boolean DEFAULT_ENABLE = false;

        // =================================================================
        // 1. 低端 TV 盒子 / 低配设备 (BOX) —— 核心理念：极度保守、稳定优先、减少卡顿与解码器抖动
        // =================================================================
        /**
         * 升码率所需最小缓存：15秒（盒子网络/性能弱，储备足够多的缓存才敢切高画质）
         */
        public static final int BOX_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 15_000;
        /**
         * 降码率门限：15秒（降低降档灵敏度，避免因瞬间网络抖动频繁切流导致老旧硬解崩溃）
         */
        public static final int BOX_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 15_000;
        /**
         * 丢弃缓存保留底线：20秒（盒子下载慢，废弃低画质时必须保留更多已缓存数据防断流）
         */
        public static final int BOX_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 20_000;
        /**
         * 允许废弃最大宽度：720p (仅允许 480p/720p 废弃，1080p及以上决不废弃重下)
         */
        public static final int BOX_MAX_WIDTH_TO_DISCARD = 1280;
        /**
         * 允许废弃最大高度：720p
         */
        public static final int BOX_MAX_HEIGHT_TO_DISCARD = 720;
        /**
         * 带宽利用率折扣：0.6 (预留 40% 估算安全余量，极度保守防止估算偏高导致卡顿)
         */
        public static final float BOX_BANDWIDTH_FRACTION = 0.60F;
        /**
         * 直播升码率阈值：0.85 (已缓存长度占 Live Edge 比例达 85% 才可以升清晰度)
         */
        public static final float BOX_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE = 0.85F;


        // =================================================================
        // 2. 智能手机 / 平板 (PHONE) —— 核心理念：快速响应网络波动、积极提画质、高带宽利用
        // =================================================================
        /**
         * 升码率所需最小缓存：8秒（手机性能强、下载快，有 8s 缓存即可以快速提画质）
         */
        public static final int PHONE_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 8_000;
        /**
         * 降码率门限：25秒（移动网络波动大，缓存充足时多撑一会儿不降档）
         */
        public static final int PHONE_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25_000;
        /**
         * 丢弃缓存保留底线：15秒（手机下载速率高，保留 15s 足够平滑过渡到新清晰度）
         */
        public static final int PHONE_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 15_000;
        /**
         * 允许废弃最大宽度：1080p (允许 1080p 及以下缓存废弃重下更高码率/4K)
         */
        public static final int PHONE_MAX_WIDTH_TO_DISCARD = 1920;
        /**
         * 允许废弃最大高度：1080p
         */
        public static final int PHONE_MAX_HEIGHT_TO_DISCARD = 1080;
        /**
         * 带宽利用率折扣：0.70 (预留 30% 安全余量，兼顾画质与弱网稳定性)
         */
        public static final float PHONE_BANDWIDTH_FRACTION = 0.70F;
        /**
         * 直播升码率阈值：0.70 (缓存达到 70% 即可升码率，追帧和高清兼顾)
         */
        public static final float PHONE_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE = 0.70F;


        // 兼容默认值（默认使用 BOX 方案，确保低性能设备开箱可用不卡顿）
        public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = BOX_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
        public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = BOX_MAX_DURATION_FOR_QUALITY_DECREASE_MS;
        public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = BOX_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS;
        public static final int DEFAULT_MAX_WIDTH_TO_DISCARD = BOX_MAX_WIDTH_TO_DISCARD;
        public static final int DEFAULT_MAX_HEIGHT_TO_DISCARD = BOX_MAX_HEIGHT_TO_DISCARD;
        public static final float DEFAULT_BANDWIDTH_FRACTION = BOX_BANDWIDTH_FRACTION;
        public static final float DEFAULT_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE = BOX_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE;
    }


    /**
     * 用于管理自适应码率切换（ABR）与直播低延时追帧/追时差（Live Offset Control）的动态联动
     */
    public static class LiveConfiguration {

        // =================================================================
        // 1. 低端 TV 盒子 (BOX) —— 降低追帧频率与倍速幅度，防止音频变声/画面卡帧
        // =================================================================

        // // 卡顿后延迟增加 500ms
        // private long targetLiveOffsetIncrementOnRebufferUs = Util.msToUs(500L);
        public static final long BOX_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US = Util.msToUs(500L);
        // private float minPossibleLiveOffsetSmoothingFactor = 0.999F;
        public static final float BOX_MIN_POSSIBLE_LIVE_OFFSET_SMOOTHING_FACTOR = 0.999F;
        // private long maxLiveOffsetErrorUsForUnitSpeed = Util.msToUs(20L);
        public static final long BOX_MAX_LIVE_OFFSET_ERROR_US_FOR_UNIT_SPEED = Util.msToUs(20L);
        //  private long minUpdateIntervalMs = 1000L;
        public static final long BOX_MIN_UPDATE_INTERVAL_MS = 1000L;
        // private float proportionalControlFactorUs = 1.0E-7F;
        public static final float BOX_PROPORTIONAL_CONTROL_FACTOR_US = 1.0E-7F;

        // // 最慢 0.98x 慢放
        // private float fallbackMinPlaybackSpeed = 0.97F;
        public static final float BOX_MIN_PLAYBACK_SPEED = 0.97F;
        // 最高 1.03x 快放（无感追帧）
        // private float fallbackMaxPlaybackSpeed = 1.03F;
        public static final float BOX_MAX_PLAYBACK_SPEED = 1.03F;


        public static final long BOX_TARGET_OFFSET_MS = 4000L;                                // 期望目标延迟 4 秒（长延迟防卡）
        public static final long BOX_MIN_OFFSET_MS = 2000L;                                   // 最小延迟 2 秒
        public static final long BOX_MAX_OFFSET_MS = 25_000L;                                 // 最大容忍延迟 25 秒

        // =================================================================
        // 2. 智能手机 (PHONE) —— 强追帧低延迟模式
        // =================================================================

        public static final long PHONE_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US = Util.msToUs(500L); // 卡顿后延迟增加 300ms private long targetLiveOffsetIncrementOnRebufferUs = Util.msToUs(500L);
        public static final float PHONE_MIN_POSSIBLE_LIVE_OFFSET_SMOOTHING_FACTOR = 0.999F; // private float minPossibleLiveOffsetSmoothingFactor = 0.999F;
        public static final long PHONE_MAX_LIVE_OFFSET_ERROR_US_FOR_UNIT_SPEED = Util.msToUs(20L); // private long maxLiveOffsetErrorUsForUnitSpeed = Util.msToUs(20L);
        public static final float PHONE_PROPORTIONAL_CONTROL_FACTOR_US = 1.0E-7F; // private float proportionalControlFactorUs = 1.0E-7F;

        // private long minUpdateIntervalMs = 1000L;
        public static final long PHONE_MIN_UPDATE_INTERVAL_MS = 1000L;
        public static final float PHONE_MIN_PLAYBACK_SPEED = 0.97F;                            // 最慢 0.95x 慢放 private float fallbackMaxPlaybackSpeed = 1.03F;
        public static final float PHONE_MAX_PLAYBACK_SPEED = 1.03F;                            // 最高 1.08x 快放 private float fallbackMinPlaybackSpeed = 0.97F;

        public static final long PHONE_TARGET_OFFSET_MS = 2500L;                               // 期望目标延迟 2.5 秒
        public static final long PHONE_MIN_OFFSET_MS = 1000L;                                  // 最小延迟 1.0 秒
        public static final long PHONE_MAX_OFFSET_MS = 35_000L;                                // 最大容忍延迟 35 秒

        // =================================================================
        // 3. 全局默认兼容参数 (写在 Cons 中，默认取 BOX 策略保证稳定性)
        // =================================================================
        public static final long DEFAULT_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US = BOX_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US;
        public static final float DEFAULT_MIN_POSSIBLE_LIVE_OFFSET_SMOOTHING_FACTOR = BOX_MIN_POSSIBLE_LIVE_OFFSET_SMOOTHING_FACTOR;
        public static final long DEFAULT_MAX_LIVE_OFFSET_ERROR_US_FOR_UNIT_SPEED = BOX_MAX_LIVE_OFFSET_ERROR_US_FOR_UNIT_SPEED;
        public static final float DEFAULT_PROPORTIONAL_CONTROL_FACTOR_US = BOX_PROPORTIONAL_CONTROL_FACTOR_US;
        public static final long DEFAULT_MIN_UPDATE_INTERVAL_MS = BOX_MIN_UPDATE_INTERVAL_MS;

        public static final long DEFAULT_TARGET_OFFSET_MS = BOX_TARGET_OFFSET_MS;
        public static final long DEFAULT_MIN_OFFSET_MS = BOX_MIN_OFFSET_MS;
        public static final long DEFAULT_MAX_OFFSET_MS = BOX_MAX_OFFSET_MS;
        public static final float DEFAULT_MIN_PLAYBACK_SPEED = BOX_MIN_PLAYBACK_SPEED;
        public static final float DEFAULT_MAX_PLAYBACK_SPEED = BOX_MAX_PLAYBACK_SPEED;
    }

    /**
     * 卡顿/假死检测超时配置
     * 提供 [低端盒子 BOX] 与 [移动手机 PHONE] 两套超时门限
     */
    public static class StuckConfiguration {

        // =================================================================
        // 1. 低端 TV 盒子 (BOX) —— 宽松门限，容忍硬件响应慢与网络抖动，防止误判
        // =================================================================
        // 缓冲卡死超时：15秒（盒子解码/网络响应较慢，多给一点缓冲时间）
        //  this.stuckBufferingDetectionTimeoutMs = 600000;
        public static final int BOX_BUFFERING_DETECTION_TIMEOUT_MS = 600_000;
        // 播放卡死超时：15秒（STATE_READY/PLAYING 下画面/音频无更新）
        public static final int BOX_PLAYING_DETECTION_TIMEOUT_MS = 30_000;
        // 播放未结束卡死超时：30秒（本应结束但长时间无 onPlaybackEnded 触发）
        public static final int BOX_PLAYING_NOT_ENDING_TIMEOUT_MS = 60_000;
        // 播放被抑制超时：15秒（如焦点丢失或后台限制后长时间未恢复）
        public static final int BOX_SUPPRESSED_DETECTION_TIMEOUT_MS = 600_000;

        // =================================================================
        // 2. 智能手机 / 平板 (PHONE) —— 严格门限，快速感知卡死并触发重试
        // =================================================================
        // 缓冲卡死超时：12秒（手机网络与性能强，超过 12s 无进度即可判定卡死）
        public static final int PHONE_BUFFERING_DETECTION_TIMEOUT_MS = 600_000;
        // 播放卡死超时：6秒
        public static final int PHONE_PLAYING_DETECTION_TIMEOUT_MS = 30_000;
        // 播放未结束卡死超时：15秒
        public static final int PHONE_PLAYING_NOT_ENDING_TIMEOUT_MS = 60_000;
        // 播放被抑制超时：10秒
        public static final int PHONE_SUPPRESSED_DETECTION_TIMEOUT_MS = 600_000;

        // =================================================================
        // 3. 全局默认兼容参数 (默认偏向 BOX 策略，保障低端设备稳定)
        // =================================================================
        public static final int DEFAULT_BUFFERING_DETECTION_TIMEOUT_MS = BOX_BUFFERING_DETECTION_TIMEOUT_MS;
        public static final int DEFAULT_PLAYING_DETECTION_TIMEOUT_MS = BOX_PLAYING_DETECTION_TIMEOUT_MS;
        public static final int DEFAULT_PLAYING_NOT_ENDING_TIMEOUT_MS = BOX_PLAYING_NOT_ENDING_TIMEOUT_MS;
        public static final int DEFAULT_SUPPRESSED_DETECTION_TIMEOUT_MS = BOX_SUPPRESSED_DETECTION_TIMEOUT_MS;
    }

    /**
     * 缓冲区加载策略配置（DefaultLoadControl 相关）
     * 提供 [低端盒子 BOX] 与 [移动手机 PHONE] 两套性能优化参数
     */
    public static class BufferConfiguration {

        // =================================================================
        // 1. 低端 TV 盒子 (BOX) —— 内存安全、起播防卡顿、较长卡顿超时
        // =================================================================

        // 盒子内存较小，分配粒度设为 32KB，减少内存碎片和峰值占用；手机内存充足，设为 32K
        public static final int BOX_INDIVIDUAL_ALLOCATION_SIZE = 32 * 1024;
        // 最小缓冲区大小：20秒
        // this.minBufferMs = 50000;
        public static final int BOX_MIN_BUFFER_MS = 50_000;
        // 最大缓冲区大小：50秒（手机内存充裕，多缓存提升拖动 seek 体验）
        // this.maxBufferMs = 50000;
        public static final int BOX_MAX_BUFFER_MS = 50_000;
        // 启播最小缓冲：1.5秒（兼顾秒开体验与弱网抗抖动）
        // this.bufferForPlaybackMs = 1000;
        public static final int BOX_BUFFER_FOR_PLAYBACK_MS = 1_000;
        // 二次缓冲（卡顿恢复）最小缓冲：2秒（快速恢复播放）
        // this.bufferForPlaybackAfterRebufferMs = 2000;
        public static final int BOX_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 2_000;

        // =================================================================
        // 2. 智能手机 / 平板 (PHONE) —— 追求秒开、激进恢复、充裕缓存
        // =================================================================

        /**
         *
         * this.minBufferForLocalPlaybackMs = 1000;
         * <p>
         * this.maxBufferForLocalPlaybackMs = 50000;
         * this.bufferForPlaybackMs = 1000;
         * this.bufferForPlaybackForLocalPlaybackMs = 1000;
         * this.bufferForPlaybackAfterRebufferMs = 2000;
         * this.bufferForPlaybackAfterRebufferForLocalPlaybackMs = 1000;
         * this.targetBufferBytes = -1;
         * this.prioritizeTimeOverSizeThresholds = false;
         * this.prioritizeTimeOverSizeThresholdsForLocalPlayback = true;
         * this.backBufferDurationMs = 0;
         * this.retainBackBufferFromKeyframe = false;
         */


        // 盒子内存较小，分配粒度设为 32KB，减少内存碎片和峰值占用；手机内存充足，设为 16KB
        public static final int PHONE_INDIVIDUAL_ALLOCATION_SIZE = 16 * 1024;

        // 最小缓冲区大小：20秒
        // this.minBufferMs = 50000;
        public static final int PHONE_MIN_BUFFER_MS = 50_000;
        // 最大缓冲区大小：50秒（手机内存充裕，多缓存提升拖动 seek 体验）
        // this.maxBufferMs = 50000;
        public static final int PHONE_MAX_BUFFER_MS = 50_000;
        // 启播最小缓冲：1.5秒（兼顾秒开体验与弱网抗抖动）
        // this.bufferForPlaybackMs = 1000;
        public static final int PHONE_BUFFER_FOR_PLAYBACK_MS = 1_000;
        // 二次缓冲（卡顿恢复）最小缓冲：2秒（快速恢复播放）
        // this.bufferForPlaybackAfterRebufferMs = 2000;
        public static final int PHONE_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 2_000;

        // =================================================================
        // 3. 全局默认兼容参数 (默认偏向 BOX 策略保障低端设备稳定)
        // =================================================================
        public static final int DEFAULT_INDIVIDUAL_ALLOCATION_SIZE = BOX_INDIVIDUAL_ALLOCATION_SIZE;
        public static final int DEFAULT_MIN_BUFFER_MS = BOX_MIN_BUFFER_MS;
        public static final int DEFAULT_MAX_BUFFER_MS = BOX_MAX_BUFFER_MS;
        public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = BOX_BUFFER_FOR_PLAYBACK_MS;
        public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = BOX_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
    }
}