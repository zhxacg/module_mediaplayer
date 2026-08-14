package lib.kalu.mediaplayer.bean.type;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(CLASS)
@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
public @interface PlayerType {


    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface DeviceType {
        //普通模式
        int BOX = 1_001;
        //全屏模式
        int PHONE = 1002;
        //窗口模式
        int DEFAULT = BOX;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({BOX, PHONE, DEFAULT})
        @interface Value {
        }
    }


    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface WindowType {
        //普通模式
        int DEFAULT = 2_001;
        //全屏模式
        int FULL = 2_002;
        //窗口模式
        int FLOAT = 2_003;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({DEFAULT, FULL, FLOAT})
        @interface Value {
        }
    }

    /**
     * 播放状态，主要是指播放器的各种状态
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface EventType {
        int INIT = 3_001;            // 初始化
        int READY = 3_002;            // 准备就绪
        int START = 3_003;          // 播放开始
        int END = 3_004;    // 播放完成
        int PAUSE = 3_015; // 播放暂停
        int RESUME = 3_016; // 播放恢复
        int STOP = 3_017; // 播放停止
        int RELEASE = 3_018; // 播放销毁
        int RETRY_RELOAD = 3_019; // 重试1
        int RETRY_CUR_URL = 3_020; // 重试2
        int RETRY_OTHER_URL = 3_021; // 重试3


        // 视频信息
        int MEDIA_INFO_PREPARE = 3_202;    // 起播加载
        int MEDIA_INFO_VIDEO_RENDERING_START = 3_203;    // 视频出画面
        int MEDIA_INFO_BUFFERING_START = 3_204;    // 缓冲
        int MEDIA_INFO_BUFFERING_STOP = 3_205;    // 视频出画面
        int MEDIA_INFO_UPDATE_PLAYBACLK_SPEED = 3_206; // 切换倍速
        int MEDIA_INFO_UPDATE_SEEK_START_FORWARD = 3_207;    // 快进
        int MEDIA_INFO_UPDATE_SEEK_START_REWIND = 3_208;    // 快退
        int MEDIA_INFO_UPDATE_SEEK_FINISH = 3_209;
        int MEDIA_INFO_PLAY_WHEN_READY_SEEK = 3_210; // 启播快进
        int MEDIA_INFO_PLAY_WHEN_READY_PAUSE = 3_211; // 启播暂停
        int MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_START = 3_212; // 延迟播放 倒计时开始
        int MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_END = 3_213; // 延迟播放 倒计时结束


        // 错误
        int ERROR_NETWORK = 3_301; // 网络未连接
        int ERROR_URL_EMPTY = 3_302; // 缓冲超时
        int ERROR_STREAM_SOURCE = 3_303; // 资源错误
        int ERROR_PLAY = 3_304; // 播放错误
        int ERROR_TIMEOUT_LOAD = 3_305; // 加载超时
        int ERROR_TIMEOUT_BUFFER = 3_306; // 缓冲超时
        int ERROR_INIT = 3_307; // 初始化错误
        int ERROR_DECODE = 3_308; // 解码

        // 窗口模式
        int WINDOW_FULL_START = 3_401;
        int WINDOW_FULL_SUCC = 3_402;
        int WINDOW_FULL_FAIL = 3_403;
        int WINDOW_FLOAT_START = 3_404;
        int WINDOW_FLOAT_SUCC = 3_405;
        int WINDOW_FLOAT_FAIL = 3_406;

        // 组件
        int COMPONENT_MENU_SHOW = 3_501;
        int COMPONENT_MENU_HIDE = 3_502;
        int COMPONENT_SEEK_SHOW = 3_503;
        int COMPONENT_SEEK_HIDE = 3_504;

        // 试看
        int TRY_SEE_START = 3_601;
        int TRY_SEE_END = 3_602;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({
                INIT,
                READY,
                START,
                END, // 播放完成
                PAUSE,
                RESUME,
                STOP,
                RELEASE,
                RETRY_RELOAD,
                RETRY_CUR_URL, // 重试
                RETRY_OTHER_URL, // 重试
                MEDIA_INFO_PREPARE, // 起播加载
                MEDIA_INFO_UPDATE_PLAYBACLK_SPEED, // 切换倍速
                MEDIA_INFO_VIDEO_RENDERING_START,    // 视频出画面
                MEDIA_INFO_BUFFERING_START,    // 缓冲
                MEDIA_INFO_BUFFERING_STOP,    // 视频出画面
                MEDIA_INFO_UPDATE_SEEK_START_FORWARD,    // 快进
                MEDIA_INFO_UPDATE_SEEK_START_REWIND,   // 快退
                MEDIA_INFO_UPDATE_SEEK_FINISH,
                MEDIA_INFO_PLAY_WHEN_READY_PAUSE, // 启播暂停
                MEDIA_INFO_PLAY_WHEN_READY_SEEK, // 启播快进
                MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_START, // 延迟播放 倒计时开始
                MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_END, // 延迟播放 倒计时结束
                WINDOW_FULL_START,
                WINDOW_FULL_SUCC,
                WINDOW_FULL_FAIL,
                WINDOW_FLOAT_START,
                WINDOW_FLOAT_SUCC,
                WINDOW_FLOAT_FAIL,
                TRY_SEE_START,
                TRY_SEE_END,
                COMPONENT_MENU_SHOW,
                COMPONENT_MENU_HIDE,
                COMPONENT_SEEK_SHOW,
                COMPONENT_SEEK_HIDE,
                ERROR_NETWORK, // 网络未连接
                ERROR_URL_EMPTY, // URL错误
                ERROR_STREAM_SOURCE, // 资源错误
                ERROR_PLAY, // 播放错误
                ERROR_TIMEOUT_LOAD, // 启播超时
                ERROR_TIMEOUT_BUFFER, // 缓冲超时
                ERROR_INIT, // 初始化错误
                ERROR_DECODE, // 解码
        })
        @interface Value {
        }
    }

    /**
     * 播放视频缩放类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface ScaleType {
        int AUTO = 4_001;  // 自适应屏幕，可能存在黑边
        int FULL = 4_002;  // 画面拉甚至全屏, 可能变形
        int _16_9 = 4_004;  // 画面拉伸16：9, 可能变形
        int _16_10 = 4_005; // 画面拉伸16：10, 可能变形
        int _5_4 = 4_006;   // 画面拉伸5：4, 可能变形
        int _4_3 = 4_007;   // 画面拉伸4：3, 可能变形
        int _1_1 = 4_008;   // 画面拉伸1:1, 可能变形
        int DEFAULT = AUTO; // 默认

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({AUTO,
                FULL,
                _16_9,
                _16_10,
                _5_4,
                _4_3,
                _1_1,
                DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface RotationType {
        int _0 = 5_001;
        int _90 = 5_002;
        int _180 = 5_003;
        int _270 = 5_004;
        int DEFAULT = _0;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({_0,
                _90,
                _180,
                _270,
                DEFAULT})
        @interface Value {
        }
    }

    /**
     * 通过注解限定类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface KernelType {
        int ANDROID = 6_001; // MediaPlayer，基于原生自带的播放器控件
        int EXO_V2 = 6_002; // exoplayer2
        int MEDIA_V3 = 6_003; // androidx media
        int IJK = 6_004; // ijk
        int VLC = 6_005; // vlc
        int FFPLAYER = 6_006; // ffmpeg
        int DEFAULT = ANDROID;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({IJK,
                ANDROID,
                EXO_V2,
                MEDIA_V3,
                VLC,
                FFPLAYER,
                DEFAULT})
        @interface Value {
        }
    }

    /**
     * 通过注解限定类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface RenderType {
        int TEXTURE_VIEW = 8_001;
        int SURFACE_VIEW = 8_002;
        int GL_SURFACE_VIEW = 8_003;
        int DEFAULT = SURFACE_VIEW;

        @IntDef({TEXTURE_VIEW,
                SURFACE_VIEW,
                GL_SURFACE_VIEW,
                DEFAULT})
        @Retention(RetentionPolicy.SOURCE)
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface DecoderType {
        int ALL = 10_000; // 软解&硬解
        int ONLY_CODEC = 10_001; // 硬解
        int ONLY_FFMPEG = 10_002; // 软解
        int ONLY_VIDEO_CODEC_AUDIO_FFMPEG = 10_003; // 视频硬解 音频软解
        int ONLY_VIDEO_FFMPEG_AUDIO_CODEC = 10_004; // 视频软解 音频硬解
        int ONLY_VIDEO_CODEC = 10_005;
        int ONLY_VIDEO_FFMPEG = 10_006;
        int ONLY_AUDIO_CODEC = 10_007;
        int ONLY_AUDIO_FFMPEG = 10_008;

        int DEFAULT = ALL;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                DecoderType.ALL,
                DecoderType.ONLY_CODEC,
                DecoderType.ONLY_FFMPEG,
                DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG,
                DecoderType.ONLY_VIDEO_FFMPEG_AUDIO_CODEC,
                DecoderType.ONLY_VIDEO_CODEC,
                DecoderType.ONLY_VIDEO_FFMPEG,
                DecoderType.ONLY_AUDIO_CODEC,
                DecoderType.ONLY_AUDIO_FFMPEG,
                DecoderType.DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface SeekType {

        int DEFAULT = 9_001;
        int EXO_CLOSEST_SYNC = 9_002;
        int EXO_PREVIOUS_SYNC = 9_003;
        int EXO_NEXT_SYNC = 9_004;
        int EXO_EXACT = 9_005;
        int ANDROID_SEEK_PREVIOUS_SYNC = 9_006;
        int ANDROID_SEEK_NEXT_SYNC = 9_007;
        int ANDROID_SEEK_CLOSEST_SYNC = 9_008;
        int ANDROID_SEEK_CLOSEST = 9_009;
        int IJK_SEEK_FASTSEEK = 9_010;
        int IJK_SEEK_NOBUFFER = 9_011;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                SeekType.DEFAULT,
                SeekType.EXO_CLOSEST_SYNC,
                SeekType.EXO_PREVIOUS_SYNC,
                SeekType.EXO_NEXT_SYNC,
                SeekType.EXO_EXACT,
                SeekType.ANDROID_SEEK_PREVIOUS_SYNC,
                SeekType.ANDROID_SEEK_NEXT_SYNC,
                SeekType.ANDROID_SEEK_CLOSEST_SYNC,
                SeekType.ANDROID_SEEK_CLOSEST,
                SeekType.IJK_SEEK_FASTSEEK,
                SeekType.IJK_SEEK_NOBUFFER,
        })
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface TrackType {

        String TEXT_VTT = "text/vtt";
        String TEXT_SSA = "text/x-ssa";
        String TEXT_ASS = "text/x-ass";
        String TEXT_SRT = "application/x-subrip";

        String AUDIO_MP4 = "audio/mp4";
        String AUDIO_AAC = "audio/mp4a-latm";
        String AUDIO_MATROSKA = "audio/x-matroska";
        String AUDIO_WEBM = "audio/webm";
        String AUDIO_MPEG = "audio/mpeg";
        String AUDIO_MPEG_L1 = "audio/mpeg-L1";
        String AUDIO_MPEG_L2 = "audio/mpeg-L2";
        String AUDIO_MPEGH_MHA1 = "audio/mha1";
        String AUDIO_MPEGH_MHM1 = "audio/mhm1";
        String AUDIO_RAW = "audio/raw";
        String AUDIO_ALAW = "audio/g711-alaw";
        String AUDIO_MLAW = "audio/g711-mlaw";
        String AUDIO_AC3 = "audio/ac3";
        String AUDIO_E_AC3 = "audio/eac3";
        String AUDIO_E_AC3_JOC = "audio/eac3-joc";
        String AUDIO_AC4 = "audio/ac4";
        String AUDIO_TRUEHD = "audio/true-hd";
        String AUDIO_DTS = "audio/vnd.dts";
        String AUDIO_DTS_HD = "audio/vnd.dts.hd";
        String AUDIO_DTS_EXPRESS = "audio/vnd.dts.hd;profile=lbr";
        String AUDIO_DTS_X = "audio/vnd.dts.uhd;profile=p2";
        String AUDIO_VORBIS = "audio/vorbis";
        String AUDIO_OPUS = "audio/opus";
        String AUDIO_AMR = "audio/amr";
        String AUDIO_AMR_NB = "audio/3gpp";
        String AUDIO_AMR_WB = "audio/amr-wb";
        String AUDIO_FLAC = "audio/flac";
        String AUDIO_ALAC = "audio/alac";
        String AUDIO_MSGSM = "audio/gsm";
        String AUDIO_OGG = "audio/ogg";
        String AUDIO_WAV = "audio/wav";
        String AUDIO_MIDI = "audio/midi";
        String AUDIO_EXOPLAYER_MIDI = "audio/x-exoplayer-midi";
        String AUDIO_UNKNOWN = "audio/x-unknown";

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @StringDef(value = {
                TrackType.TEXT_VTT,
                TrackType.TEXT_SSA,
                TrackType.TEXT_SRT,
                TrackType.AUDIO_MP4,
                TrackType.AUDIO_AAC,
                TrackType.AUDIO_MATROSKA,
                TrackType.AUDIO_WEBM,
                TrackType.AUDIO_MPEG,
                TrackType.AUDIO_MPEG_L1,
                TrackType.AUDIO_MPEG_L2,
                TrackType.AUDIO_MPEGH_MHA1,
                TrackType.AUDIO_MPEGH_MHM1,
                TrackType.AUDIO_RAW,
                TrackType.AUDIO_ALAW,
                TrackType.AUDIO_MLAW,
                TrackType.AUDIO_AC3,
                TrackType.AUDIO_E_AC3,
                TrackType.AUDIO_E_AC3_JOC,
                TrackType.AUDIO_AC4,
                TrackType.AUDIO_TRUEHD,
                TrackType.AUDIO_DTS,
                TrackType.AUDIO_DTS_HD,
                TrackType.AUDIO_DTS_EXPRESS,
                TrackType.AUDIO_DTS_X,
                TrackType.AUDIO_VORBIS,
                TrackType.AUDIO_OPUS,
                TrackType.AUDIO_AMR,
                TrackType.AUDIO_AMR_NB,
                TrackType.AUDIO_AMR_WB,
                TrackType.AUDIO_FLAC,
                TrackType.AUDIO_ALAC,
                TrackType.AUDIO_MSGSM,
                TrackType.AUDIO_OGG,
                TrackType.AUDIO_WAV,
                TrackType.AUDIO_MIDI,
                TrackType.AUDIO_EXOPLAYER_MIDI,
                TrackType.AUDIO_UNKNOWN})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface MetaType {
        int VIDEO_RTMP = 1;
        int VIDEO_RTSP = 2;
        int VIDEO_DASH = 3;
        int VIDEO_HLS = 4;
        int VIDEO_SS = 5;
        int VIDEO_MP4 = 6;
        int VIDEO_OTHER = 7;
        int AUDIO = 8;
        int SUBTITLE = 9;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                MetaType.VIDEO_RTMP,
                MetaType.VIDEO_RTSP,
                MetaType.VIDEO_DASH,
                MetaType.VIDEO_HLS,
                MetaType.VIDEO_SS,
                MetaType.VIDEO_MP4,
                MetaType.VIDEO_OTHER,
                MetaType.AUDIO,
                MetaType.SUBTITLE})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface UrlType {
        int VIDEO = 1;
        int AUDIO = 2;
        int SUBTITLE = 3;
        int DATA_HLS_MULTIVARIANT_PLAYLIST = 4;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                UrlType.DATA_HLS_MULTIVARIANT_PLAYLIST,
                UrlType.VIDEO,
                UrlType.AUDIO,
                UrlType.SUBTITLE})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface StreamType {
        int DEFAULT = 1;
        int MERGE_ALL = 2;
        int FORMAT_MULTI_VARIANT_PLAYLIST = 3;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                StreamType.DEFAULT,
                StreamType.MERGE_ALL,
                StreamType.FORMAT_MULTI_VARIANT_PLAYLIST})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface ScreenOrientation {
        int PORTRAIT = 1;
        int LANDSPACE = 2;
        int UNKNOW = 3;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                ScreenOrientation.PORTRAIT,
                ScreenOrientation.LANDSPACE,
                ScreenOrientation.UNKNOW})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface SchemeType {

        String FILE = "file://";
        String RTMP = "rtmp://";
        String RTSP = "rtsp://";
        String _M3U = ".m3u";
        String _M3U_ = ".m3u?";
        String _M3U8 = ".m3u8";
        String _M3U8_ = ".m3u8?";
        String _TS = ".ts";
        String _TS_ = ".ts?";
        String _MP4 = ".mp4";
        String _MP4_ = ".mp4?";
        String _MPD = ".mpd";
        String _MPD_ = ".mpd?";
        String _VTT = ".vtt";
        String _VTT_ = ".vtt?";
        String _SSA = ".ssa";
        String _SSA_ = ".ssa?";
        String _ASS = ".ass";
        String _ASS_ = ".ass?";
        String _SRT = ".srt";
        String _SRT_ = ".srt?";
        String _MATCHES = ".*\\.ism(l)?(/manifest(\\(.+\\))?)?";

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @StringDef(value = {
                SchemeType.FILE,
                SchemeType.RTMP,
                SchemeType.RTSP,
                SchemeType._MPD,
                SchemeType._M3U,
                SchemeType._M3U8,
                SchemeType._TS,
                SchemeType._MP4,
                SchemeType._VTT,
                SchemeType._VTT_,
                SchemeType._SSA,
                SchemeType._SSA_,
                SchemeType._ASS,
                SchemeType._ASS_,
                SchemeType._SRT,
                SchemeType._SRT_,
                SchemeType._MATCHES})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface MarkType {

        String SEPARATOR = "/";
        String UNDERLINE = "_";
        //        String HLS_PLAYLIST = "hls_playlist_";
//        String HLS_SEGMENT = "hls_segment_";

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @StringDef(value = {
                MarkType.SEPARATOR,
                MarkType.UNDERLINE})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface ParserType {
        int VIDEO_AUDIO_SUBTITLE = 1;
        int VIDEO_AUDIO = 2;
        int VIDEO = 3;
        int AUDIO = 4;
        int SUBTITLE = 5;
        int DEFAULT = VIDEO_AUDIO_SUBTITLE;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({VIDEO_AUDIO_SUBTITLE,
                VIDEO_AUDIO,
                VIDEO,
                AUDIO,
                SUBTITLE,
                DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface ResolutionType {

        String DEFAULT = "";
        String _8K = "8K";
        String _4K = "4K";
        String _1080 = "1080";
        String _720 = "720";
        String _480 = "480";
        String _360 = "360";

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @StringDef({
                DEFAULT,
                _8K,
                _4K,
                _1080,
                _720,
                _480,
                _360})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface BuriedType {
        int VIDEO_RENDERING_START = 1;
        int START = 2;
        int ERROR_PREPARE = 3; // 启播错误
        int ERROR_PLAY = 4; // 播放错误
        int PAUSE = 5;
        int RESUME = 6;
        int STOP = 7;
        int RELEASE = 8;
        int TRY_SEE_END = 9;
        int COMPLETED = 10;
        int BUFFERING_START = 11;
        int BUFFERING_STOP = 12;
        int SEEK_START_FORWARD = 13;
        int SEEK_START_REWIND = 14;
        int SEEK_FINISH = 15;
        int UPDATE_WINDOW = 16;
        int UPDATE_EVENT = 17;
        int UPDATE_SUBTITLE_OFFSET_MS = 18;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                BuriedType.VIDEO_RENDERING_START,
                BuriedType.START,
                BuriedType.ERROR_PREPARE,
                BuriedType.ERROR_PLAY,
                BuriedType.PAUSE,
                BuriedType.RESUME,
                BuriedType.STOP,
                BuriedType.RELEASE,
                BuriedType.COMPLETED,
                BuriedType.BUFFERING_START,
                BuriedType.BUFFERING_STOP,
                BuriedType.SEEK_START_FORWARD,
                BuriedType.SEEK_START_REWIND,
                BuriedType.SEEK_FINISH,
                BuriedType.UPDATE_WINDOW,
                BuriedType.UPDATE_EVENT,
                BuriedType.UPDATE_SUBTITLE_OFFSET_MS,
                BuriedType.TRY_SEE_END
        })
        @interface Value {
        }
    }
}