package lib.kalu.mediaplayer;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayerConst {

    // 默认超时 20s
    public static int DEFAULT_CONNECT_TIMEOUT = 20_000;

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

    // dash hls 自适应码率 默认关闭
    public static boolean DEFAULT_ADAPTIVE_ENABLE = false;

    // log 日志 默认关闭
    public static boolean DEFAULT_LOG_ENABLE = false;
}
