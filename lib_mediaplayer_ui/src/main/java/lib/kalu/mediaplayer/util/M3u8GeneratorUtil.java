package lib.kalu.mediaplayer.util;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;

/**
 * 将 UrlArgs 内存数据转换为符合 HLS 规范的 Master M3U8 索引文件/Data URI
 */
public final class M3u8GeneratorUtil {

    private static final String TAG = "M3u8GeneratorUtil";
    private static final String AUDIO_GROUP_ID = "audio-streams";
    private static final String SUBTITLE_GROUP_ID = "subtitle-streams";

    // 默认保存的缓存文件名
    private static final String M3U8_FILE_NAME = "temp.m3u8";

    // 通用码率梯队，已补全 8K ~ 360P 梯队 (bps)
    private static final long[] DEFAULT_BANDWIDTH_TIERS = new long[]{
            30000000L, // 1st: ~30 Mbps (8K 超高清)
            15000000L, // 2nd: ~15 Mbps (4K 超高清)
            5000000L,  // 3rd: ~5 Mbps (1080P 全高清)
            2500000L,  // 4th: ~2.5 Mbps (720P 高清)
            1200000L,  // 5th: ~1.2 Mbps (480P 标清)
            600000L    // 6th: ~600 Kbps (360P 流畅)
    };

    private M3u8GeneratorUtil() {
        // 私有构造，防止实例化
    }

    /**
     * 将 UrlArgs 保存为以 .m3u8 结尾的缓存文件，并返回绝对路径 String
     * 规则：线程安全，存在即删除，确保每次都是最新的 master 索引
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static synchronized String saveCacheM3u8Path(Context context, UrlArgs args) {
        if (context == null || args == null) return null;

        // 1. 生成 .m3u8 文本内容
        String m3u8Content = buildMasterM3u8Text(args);
        if (m3u8Content.isEmpty()) return null;

        // 2. 在 Cache 目录下创建指定以 .m3u8 结尾的文件
        File cacheFolder = context.getFilesDir();
        if (cacheFolder == null) return null;

        File m3u8File = new File(cacheFolder, M3U8_FILE_NAME);

        // 3. 每次存在先删除旧的 .m3u8 文件
        if (m3u8File.exists()) {
            boolean isDeleted = m3u8File.delete();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "原 .m3u8 缓存文件存在，清理结果: " + isDeleted);
            }
        }

        // 4. 写入新的数据 (使用 try-with-resources 自动关闭流)
        try (FileOutputStream fos = new FileOutputStream(m3u8File)) {
            fos.write(m3u8Content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "成功生成新的 .m3u8 文件: " + m3u8File.getAbsolutePath());
            }

            // 5. 返回绝对路径
            return m3u8File.getAbsolutePath();

        } catch (IOException e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "写入 .m3u8 文件失败", e);
            }
            return "";
        }
    }

    /**
     * 格式化 Base64 Data URI 生成方式 (内存直传供播放器使用)
     */
    public static String formatMasterM3u8DataUri(UrlArgs args) {
        String m3u8Text = buildMasterM3u8Text(args);
        if (m3u8Text.isEmpty()) return "";
        String base64 = Base64.encodeToString(m3u8Text.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        return "data:application/x-mpegURL;base64," + base64;
    }

    /**
     * 根据 UrlArgs 构建 Master M3U8 纯文本
     */
    public static String buildMasterM3u8Text(UrlArgs args) {
        if (args == null) return "";

        List<UrlArgs.Item> allStreams = new ArrayList<>();
        if (args.getDefaultStreams() != null) {
            allStreams.addAll(args.getDefaultStreams());
        }
        if (args.getExtraStreams() != null) {
            allStreams.addAll(args.getExtraStreams());
        }

        List<UrlArgs.Item> videoStreams = new ArrayList<>();
        List<UrlArgs.Item> audioStreams = new ArrayList<>();
        List<UrlArgs.Item> subtitles = args.getExtraSubtitles() != null ? args.getExtraSubtitles() : new ArrayList<>();

        // 1. 整理分类视频轨与音频轨，并实现视频轨道 isDefault 优先置顶
        for (UrlArgs.Item item : allStreams) {
            if (item == null || !item.containsUrl()) continue;

            if (item.getParser() == PlayerType.ParserType.AUDIO) {
                audioStreams.add(item);
            } else {
                // 如果视频 Item 的 def/isDefault 为 true，强行插入到列表顶部（第 0 项）
                if (item.isDefault()) {
                    videoStreams.add(0, item);
                } else {
                    videoStreams.add(item);
                }
            }
        }

        // 缺乏视频流则取消构建
        if (videoStreams.isEmpty()) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildMasterM3u8Text: 没有可播放的视频流，取消构建！");
            }
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");

        boolean hasAudioGroup = !audioStreams.isEmpty();
        boolean hasSubGroup = !subtitles.isEmpty();

        // 计算组内 DEFAULT 项的唯一索引（保证 GROUP 内有且仅有一个 DEFAULT=YES）
        int defaultAudioIdx = findDefaultIndex(audioStreams);
        int defaultSubIdx = findDefaultIndex(subtitles);

        // 2. 拼接独立音频轨标签 (#EXT-X-MEDIA:TYPE=AUDIO)
        if (hasAudioGroup) {
            sb.append("# ================= 独立音频轨定义 =================\n");
            for (int i = 0; i < audioStreams.size(); i++) {
                UrlArgs.Item audio = audioStreams.get(i);
                boolean isDefault = (i == defaultAudioIdx);
                String labelName = getSafeString(audio.getLabel(), "Audio_" + (i + 1));
                String lang = getSafeString(audio.getLanguage(), "zh");

                sb.append(String.format(
                        "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"%s\",NAME=\"%s\",DEFAULT=%s,AUTOSELECT=YES,LANGUAGE=\"%s\",URI=\"%s\"\n",
                        AUDIO_GROUP_ID,
                        labelName,
                        isDefault ? "YES" : "NO",
                        lang,
                        audio.getUrl()
                ));
            }
            sb.append("\n");
        }

        // 3. 拼接外挂字幕组标签 (#EXT-X-MEDIA:TYPE=SUBTITLES)
        if (hasSubGroup) {
            sb.append("# ================= 字幕轨定义 =================\n");
            for (int i = 0; i < subtitles.size(); i++) {
                UrlArgs.Item sub = subtitles.get(i);
                if (sub == null || !sub.containsUrl()) continue;

                boolean isDefault = (i == defaultSubIdx);
                String labelName = getSafeString(sub.getLabel(), "Subtitle_" + (i + 1));
                String lang = getSafeString(sub.getLanguage(), "zh");

                sb.append(String.format(
                        "#EXT-X-MEDIA:TYPE=SUBTITLES,GROUP-ID=\"%s\",NAME=\"%s\",DEFAULT=%s,AUTOSELECT=YES,FORCED=NO,LANGUAGE=\"%s\",URI=\"%s\"\n",
                        SUBTITLE_GROUP_ID,
                        labelName,
                        isDefault ? "YES" : "NO",
                        lang,
                        sub.getUrl()
                ));
            }
            sb.append("\n");
        }

        // 4. 拼接多码率视频流标签 (#EXT-X-STREAM-INF)
        sb.append("# ================= 视频码率流定义 =================\n");
        for (int i = 0; i < videoStreams.size(); i++) {
            UrlArgs.Item video = videoStreams.get(i);

            // 估算码率与分辨率
            long estimatedBandwidth = estimateBandwidth(video.getLabel(), i);
            String resolution = estimateResolution(video.getResolution());
            if (null == resolution)
                continue;

            sb.append("#EXT-X-STREAM-INF:BANDWIDTH=").append(estimatedBandwidth);

            if (resolution != null) {
                sb.append(",RESOLUTION=").append(resolution);
            }
            if (hasAudioGroup) {
                sb.append(",AUDIO=\"").append(AUDIO_GROUP_ID).append("\"");
            }
            if (hasSubGroup) {
                sb.append(",SUBTITLES=\"").append(SUBTITLE_GROUP_ID).append("\"");
            }
            sb.append("\n");
            sb.append(video.getUrl()).append("\n\n");
        }

        return sb.toString();
    }

    private static int findDefaultIndex(List<UrlArgs.Item> items) {
        if (items == null || items.isEmpty()) return -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) != null && items.get(i).isDefault()) {
                return i;
            }
        }
        return 0;
    }

    private static String getSafeString(String str, String defaultVal) {
        return (str != null && !str.trim().isEmpty()) ? str : defaultVal;
    }

    /**
     * 计算预估 Bandwidth (支持 8K/4K/1080P/720P/480P/360P)
     */
    private static long estimateBandwidth(String label, int index) {
        if (label != null) {
            String l = label.toLowerCase();
            if (l.contains("8k") || l.contains("4320")) return 30000000L;
            if (l.contains("4k") || l.contains("2160")) return 15000000L;
            if (l.contains("1080")) return 5000000L;
            if (l.contains("720")) return 2500000L;
            if (l.contains("480")) return 1200000L;
            if (l.contains("360")) return 800000L;
        }
        if (index < DEFAULT_BANDWIDTH_TIERS.length) {
            return DEFAULT_BANDWIDTH_TIERS[index];
        }
        return Math.max(300000L, DEFAULT_BANDWIDTH_TIERS[DEFAULT_BANDWIDTH_TIERS.length - 1] - ((index - DEFAULT_BANDWIDTH_TIERS.length + 1) * 100000L));
    }

    /**
     * 根据 label 解析分辨率像素 (支持 8K/4K/1080P/720P/480P/360P)
     */
    private static String estimateResolution(@PlayerType.ResolutionType.Value String resolution) {
        if (resolution == null || resolution.isEmpty()) return null;
        String l = resolution.toLowerCase();
        if (l.contains("8k") || l.contains("4320")) return "7680x4320";
        if (l.contains("4k") || l.contains("2160")) return "3840x2160";
        if (l.contains("1080")) return "1920x1080";
        if (l.contains("720")) return "1280x720";
        if (l.contains("480")) return "854x480";
        if (l.contains("360")) return "640x360";
        return null;
    }
}