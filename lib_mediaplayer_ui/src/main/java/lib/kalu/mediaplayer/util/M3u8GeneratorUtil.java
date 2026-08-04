package lib.kalu.mediaplayer.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

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
 * 将 UrlArgs 内存数据转换为 HLS Master M3U8 字符串及保存至缓存文件/Data URI
 */
public final class M3u8GeneratorUtil {

    private static final String TAG = "M3u8GeneratorUtil";
    private static final String AUDIO_GROUP_ID = "aud-group-main";
    private static final String SUB_GROUP_ID = "sub-group-main";

    // 默认保存的缓存文件名
    private static final String M3U8_FILE_NAME = "master_temp.m3u8";

    /**
     * 将 UrlArgs 保存为以 .m3u8 结尾的缓存文件，并返回对应的 Uri
     * 规则：存在即删除，确保每次都是最新的 master 索引
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getCacheM3u8Path(Context context, UrlArgs args) {
        if (context == null || args == null) return null;

        // 1. 生成 .m3u8 文本内容
        String m3u8Content = buildMasterM3u8Text(args);
        if (m3u8Content.isEmpty()) return null;

        // 2. 在 Cache 目录下创建指定以 .m3u8 结尾的文件
        File cacheFolder = context.getCacheDir();
        File m3u8File = new File(cacheFolder, M3U8_FILE_NAME);

        // 3. 每次存在先删除旧的 .m3u8 文件
        if (m3u8File.exists()) {
            boolean isDeleted = m3u8File.delete();
            Log.d(TAG, "原 .m3u8 缓存文件存在，清理结果: " + isDeleted);
        }

        // 4. 写入新的数据
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(m3u8File);
            fos.write(m3u8Content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            Log.d(TAG, "成功生成新的 .m3u8 文件: " + m3u8File.getAbsolutePath());

            // 5. 返回 file://.../playlist_cache.m3u8 的 Uri
            return m3u8File.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "写入 .m3u8 文件失败", e);
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 根据 UrlArgs 构建 Master M3U8 纯文本
     */
    public static String buildMasterM3u8Text(UrlArgs args) {
        if (args == null) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");

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

        // 1. 整理分类视频轨与音频轨
        for (UrlArgs.Item item : allStreams) {
            if (item == null || !item.containsUrl()) continue;

            if (item.getParser() == PlayerType.ParserType.AUDIO) {
                audioStreams.add(item);
            } else {
                // VIDEO 或 DEFAULT 均按视频流处理
                videoStreams.add(item);
            }
        }

        boolean hasAudioGroup = !audioStreams.isEmpty();
        boolean hasSubGroup = !subtitles.isEmpty();

        // 2. 拼接音频组标签 (#EXT-X-MEDIA:TYPE=AUDIO)
        if (hasAudioGroup) {
            sb.append("# ================= 音频轨定义 =================\n");
            for (UrlArgs.Item audio : audioStreams) {
                sb.append(String.format(
                        "#EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID=\"%s\",NAME=\"%s\",DEFAULT=%s,AUTOSELECT=YES,LANGUAGE=\"%s\",URI=\"%s\"\n",
                        AUDIO_GROUP_ID,
                        getSafeString(audio.getLabel(), "Audio"),
                        audio.isDefault() ? "YES" : "NO",
                        getSafeString(audio.getLanguage(), "zh"),
                        audio.getUrl()
                ));
            }
            sb.append("\n");
        }

        // 3. 拼接外挂字幕组标签 (#EXT-X-MEDIA:TYPE=SUBTITLES)
        if (hasSubGroup) {
            sb.append("# ================= 字幕轨定义 =================\n");
            for (UrlArgs.Item sub : subtitles) {
                if (sub == null || !sub.containsUrl()) continue;
                sb.append(String.format(
                        "#EXT-X-MEDIA:TYPE=SUBTITLES,GROUP-ID=\"%s\",NAME=\"%s\",DEFAULT=%s,AUTOSELECT=YES,FORCED=NO,LANGUAGE=\"%s\",URI=\"%s\"\n",
                        SUB_GROUP_ID,
                        getSafeString(sub.getLabel(), "Subtitle"),
                        sub.isDefault() ? "YES" : "NO",
                        getSafeString(sub.getLanguage(), "zh"),
                        sub.getUrl()
                ));
            }
            sb.append("\n");
        }

        // 4. 拼接多码率视频流标签 (#EXT-X-STREAM-INF)
        if (!videoStreams.isEmpty()) {
            sb.append("# ================= 视频码率流定义 =================\n");
            for (int i = 0; i < videoStreams.size(); i++) {
                UrlArgs.Item video = videoStreams.get(i);

                // 根据 Label/分辨率名称估算码率与像素大小，保持 ABR 梯度
                long estimatedBandwidth = estimateBandwidth(video.getLabel(), i);
                String resolution = estimateResolution(video.getLabel());

                sb.append("#EXT-X-STREAM-INF:BANDWIDTH=").append(estimatedBandwidth);

                if (resolution != null) {
                    sb.append(",RESOLUTION=").append(resolution);
                }
                if (hasAudioGroup) {
                    sb.append(",AUDIO=\"").append(AUDIO_GROUP_ID).append("\"");
                }
                if (hasSubGroup) {
                    sb.append(",SUBTITLES=\"").append(SUB_GROUP_ID).append("\"");
                }
                sb.append("\n");
                sb.append(video.getUrl()).append("\n\n");
            }
        }

        return sb.toString();
    }

    /**
     * 保留 Data URI 生成方式
     */
    public static String buildMasterM3u8DataUri(UrlArgs args) {
        String m3u8Text = buildMasterM3u8Text(args);
        String base64 = Base64.encodeToString(m3u8Text.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        return "data:application/x-mpegURL;base64," + base64;
    }

    private static String getSafeString(String str, String defaultVal) {
        return (str != null && !str.isEmpty()) ? str : defaultVal;
    }

    private static long estimateBandwidth(String label, int index) {
        if (label != null) {
            String l = label.toLowerCase();
            if (l.contains("4k") || l.contains("2160")) return 15000000L;
            if (l.contains("1080")) return 5000000L;
            if (l.contains("720")) return 2500000L;
            if (l.contains("480")) return 1200000L;
            if (l.contains("360")) return 800000L;
        }
        return Math.max(800000L, 5000000L - (index * 1500000L));
    }

    private static String estimateResolution(String label) {
        if (label == null) return null;
        String l = label.toLowerCase();
        if (l.contains("4k") || l.contains("2160")) return "3840x2160";
        if (l.contains("1080")) return "1920x1080";
        if (l.contains("720")) return "1280x720";
        if (l.contains("480")) return "854x480";
        if (l.contains("360")) return "640x360";
        return null;
    }
}