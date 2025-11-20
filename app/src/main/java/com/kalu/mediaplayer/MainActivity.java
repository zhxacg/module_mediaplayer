package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kalu.mediaplayer.proxy.ProxyBuried;
import com.kalu.mediaplayer.proxy.ProxyUrl;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.menu.Menu;
import lib.kalu.mediaplayer.bean.proxy.Proxy;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.test.TestActivity;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * description:
 * created by kalu on 2021/11/23
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        findViewById(R.id.main_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1
                initPlayer();
                // 2
                StartArgs args = new StartArgs.Builder()
                        .setUrl(getUrl())
                        .setExtraTrackSubtitle(getExtraSubtitleUrls())
                        .setExtraTrackVideo(getExtraVideoUrls())
                        .setExtraTrackAudio(getExtraAudioUrls())
                        .setTitle("测试视频")
                        .setLive(isLive())
                        .setLooping(isLooping())
                        .setPlayWhenReadySeekToPosition(getSeek())
                        .setPlayWhenReadyDelayedTime(getPlayWhenReadyDelayedTime())
                        .setPlayWhenReady(isPlayWhenReady())
                        .setTrySeeDuration(getTrySeeDuration())
                        .setShowSpeed(isShowNet())
                        .setMenu(createMenu())
                        .build();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra(TestActivity.INTENT_ARGS, args);
                startActivity(intent);
            }
        });
    }


    private void init() {

        // decoders
        try {
            String[] strings = getResources().getStringArray(R.array.decoders);
            ViewGroup viewGroup = findViewById(R.id.main_decoder);
            for (int i = 0; i < strings.length; i++) {
                LayoutInflater.from(this).inflate(R.layout.activity_main_radio_button, viewGroup);
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                radioButton.setText(strings[i]);
            }
        } catch (Exception e) {
        }


        // kernel
        try {
            String[] kernels = getResources().getStringArray(R.array.kernels);
            ViewGroup viewGroup = findViewById(R.id.main_kernel_group);
            for (int i = 0; i < kernels.length; i++) {
                LayoutInflater.from(this).inflate(R.layout.activity_main_radio_button, viewGroup);
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                radioButton.setText(kernels[i]);
            }
        } catch (Exception e) {
        }

        // url
        try {
            String[] names = getResources().getStringArray(R.array.names);
            String[] urls = getResources().getStringArray(R.array.urls);
            if (names.length != urls.length)
                throw new Exception();
            ViewGroup viewGroup = findViewById(R.id.main_urls);
            for (int i = 0; i < names.length; i++) {
                LayoutInflater.from(this).inflate(R.layout.activity_main_radio_button, viewGroup);
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                radioButton.setText(names[i]);
                radioButton.setTag(urls[i]);
            }
        } catch (Exception e) {
        }

        // copy
        try {
            List<String> list = Arrays.asList("test.mp4", "test.vtt");
            for (int i = 0; i < list.size(); i++) {
                String fromPath = list.get(i);
                String savePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fromPath;
                try {
                    InputStream is = getApplicationContext().getAssets().open(fromPath);
                    FileOutputStream fos = new FileOutputStream(savePath);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                        // buffer字节
                        fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                    }
                    fos.flush();// 刷新缓冲区
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "初始化资源文件 => 错误", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            Toast.makeText(getApplicationContext(), "初始化资源文件 => 成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    private Menu createMenu() {
        ArrayList<Menu.Item> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

            // 选集
            if (i == 0) {

                int count = 24;
                String url = getUrl();
                ArrayList<String> strings = new ArrayList<>();
                for (int j = 0; j < count; j++) {
                    strings.add(url);
                }

                Menu.Episode item = new Menu.Episode();
                item.setName("选集");
                item.setPlayPos(4);
                item.setPlayCount(count);
                item.setPlayUrls(strings);
                item.setFreeCount(2);
                item.setFreeRes(R.drawable.ic_free);
                item.setVipRes(R.drawable.ic_vip);
                list.add(item);
            }
            // 倍速
            else if (i == 1) {
                Menu.Default item = new Menu.Default();
                item.setName("倍速");
                item.setData(new int[]{
                        PlayerType.SpeedType._0_5,
                        PlayerType.SpeedType._1_0,
                        PlayerType.SpeedType._1_5,
                        PlayerType.SpeedType._2_0,
                        PlayerType.SpeedType._2_5,
                        PlayerType.SpeedType._3_0,
                        PlayerType.SpeedType._3_5,
                        PlayerType.SpeedType._4_0,
                        PlayerType.SpeedType._4_5,
                        PlayerType.SpeedType._5_0});
                list.add(item);
            }
            // 画面比例
            else {
                Menu.Default item = new Menu.Default();
                item.setName("画面比例");
                item.setData(new int[]{
                        PlayerType.ScaleType.AUTO,
                        PlayerType.ScaleType.FULL,
                        PlayerType.ScaleType.REAL,
                        PlayerType.ScaleType._16_9,
                        PlayerType.ScaleType._16_10,
                        PlayerType.ScaleType._5_4,
                        PlayerType.ScaleType._4_3,
                        PlayerType.ScaleType._1_1});
                list.add(item);
            }
        }

        return new Menu.Builder().setData(list).build();
    }

    private long getSeek() {
        CheckBox checkBox = findViewById(R.id.main_seek_yes);
        return checkBox.isChecked() ? 300 * 1000L : 0L;
    }

    private boolean isShowNet() {
        CheckBox checkBox = findViewById(R.id.main_net_yes);
        return checkBox.isChecked();
    }

    private int getPlayWhenReadyDelayedTime() {
        CheckBox checkBox = findViewById(R.id.main_play_when_delayed_yes);
        return checkBox.isChecked() ? 3000 : 0;
    }

    private long getTrySeeDuration() {
        CheckBox checkBox = findViewById(R.id.main_trysee_yes);
        return checkBox.isChecked() ? 20 * 1000L : 0L;
    }

    private boolean isPlayWhenReady() {
        CheckBox checkBox = findViewById(R.id.main_play_when_ready_yes);
        return checkBox.isChecked();
    }

    private boolean isLive() {
        try {
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int n = 0; n < childCount; n++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(n);
                boolean checked = radioButton.isChecked();
                if (!checked)
                    continue;
                CharSequence text = radioButton.getText();
                if (!"hls_m3u8_live".equals(text))
                    continue;
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLooping() {
        CheckBox checkBox = findViewById(R.id.main_play_when_looping_yes);
        return checkBox.isChecked();
    }


    private List<TrackInfo> getExtraSubtitleUrls() {
        /**
         *  2025-04-25 20:01:32.609 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  streamsList = [# com.yyt.zapptv.model.proto.Playback$StreamTrack@37d8a524
         *     format: "hls"
         *     label: "HD"
         *     quality: "HD"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/720p/index.m3u8", # com.yyt.zapptv.model.proto.Playback$StreamTrack@35014555
         *     format: "hls"
         *     label: "SD"
         *     quality: "SD"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/480p/index.m3u8", # com.yyt.zapptv.model.proto.Playback$StreamTrack@5f9f9c57
         *     format: "hls"
         *     is_login: true
         *     label: "FULL HD"
         *     quality: "FHD"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/1080p/index.m3u8"]
         *     2025-04-25 20:01:32.612 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  subtitlesList = [# com.yyt.zapptv.model.proto.Playback$SubtitleTrack@b7ab61ea
         *     format: "vtt"
         *     label: "English"
         *     language: "en"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/English.vtt", # com.yyt.zapptv.model.proto.Playback$SubtitleTrack@6d66d2c6
         *     format: "vtt"
         *     label: "Espa\303\261ol"
         *     language: "es"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Spanish.vtt", # com.yyt.zapptv.model.proto.Playback$SubtitleTrack@925f4fa3
         *     format: "vtt"
         *     label: "Portugu\303\252s (Brasil)"
         *     language: "pt"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Portuguese.vtt"]
         *     2025-04-25 20:01:32.612 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  audiosList = []
         */


        try {
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int n = 0; n < childCount; n++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(n);
                boolean checked = radioButton.isChecked();
                if (!checked)
                    continue;
                CharSequence text = radioButton.getText();
                if (!"hls_m3u8_vod_extra".equals(text))
                    continue;

                ArrayList<TrackInfo> list = new ArrayList<>();
                String[] urls = getResources().getStringArray(R.array.hls_extra_subtitle_urls);
                String[] languages = getResources().getStringArray(R.array.hls_extra_subtitle_languages);
                for (int i = 0; i < 3; i++) {
                    TrackInfo subtitleTrack = new TrackInfo();
                    subtitleTrack.setRoleFlags((int) System.nanoTime());
                    subtitleTrack.setLanguage(languages[i]);
                    subtitleTrack.setUrl(urls[i]);
                    subtitleTrack.setMimeType(PlayerType.TrackType.TEXT_VTT);
                    //
                    list.add(subtitleTrack);
                }
                return list;
            }
            throw new Exception();
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> getExtraAudioUrls() {
        return null;
    }

    private List<String> getExtraVideoUrls() {

        try {
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int n = 0; n < childCount; n++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(n);
                boolean checked = radioButton.isChecked();
                if (!checked)
                    continue;
                CharSequence text = radioButton.getText();
                if (!"hls_m3u8_vod_extra".equals(text))
                    continue;
                ArrayList<String> list = new ArrayList<>();
                String[] urls = getResources().getStringArray(R.array.hls_extra_video_urls);
                for (int i = 0; i < urls.length; i++) {
                    list.add(urls[i]);
                }
                return list;
            }
            throw new Exception();
        } catch (Exception e) {
            return null;
        }
    }

    private String getUrl() {
        try {

            //
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (checked) {

                    return radioButton.getTag().toString();

//                    CharSequence text = radioButton.getText();
//                    if ("hls_m3u8_vod_extra".equals(text)) {
//
//
//                        StringBuilder stringBuilder = new StringBuilder();
//                        stringBuilder.append("#EXTM3U");
//                        stringBuilder.append("\n");
//                        stringBuilder.append("#EXT-X-VERSION:3");
//
//                        /**
//                         *
//                         * #EXT-X-STREAM-INF
//                         *
//                         * 1. 必须属性
//                         * BANDWIDTH
//                         * 指定该变体流的平均比特率（单位：bps）。客户端以此为基准评估网络带宽是否足够播放该流。
//                         * 示例：BANDWIDTH=1280000（1.28 Mbps）
//                         * URI（隐含关联）
//                         * 虽非直接写在EXT-X-STREAM-INF内，但该标签后必须紧跟一个指向子播放列表（Media Playlist）的URI。子播放列表包含实际媒体片段（.ts文件）的索引。
//                         * 2. 常用可选属性
//                         * RESOLUTION  stringBuilder.append("\n");
//                         * 视频分辨率（宽度×高度，单位：像素）。
//                         * 示例：RESOLUTION=1280x720（720p）
//                         * CODECS
//                         * 编码格式，包含视频和音频的编解码器信息。
//                         * 示例：CODECS="avc1.64001F,mp4a.40.2"（H.264视频 + AAC音频）
//                         * FRAME-RATE
//                         * 视频帧率（单位：fps）。
//                         * 示例：FRAME-RATE=30
//                         * AUDIO
//                         * 关联音频流的组ID（当音频与视频分离时使用）。
//                         * 示例：AUDIO="audio-group"
//                         * SUBTITLES
//                         * 关联字幕流的组ID。
//                         * 示例：SUBTITLES="subtitle-group"
//                         * PROGRAM-ID
//                         * 唯一标识符，用于区分同一M3U8中的不同节目（通常为1）。
//                         * 示例：PROGRAM-ID=1
//                         */
//
//                        /**
//                         * #EXT-X-STREAM-INF:BANDWIDTH=493000,CODECS="mp4a.40.2,avc1.66.30",RESOLUTION=224x100,FRAME-RATE=24
//                         */
//
//                        // 视频轨道
//                        String[] urls = getResources().getStringArray(R.array.hls_extra_video_urls);
//                        stringBuilder.append("\n");
//                        stringBuilder.append("\n");
//                        stringBuilder.append("# video track");
//                        for (int n = 0; n < 3; n++) {
//                            stringBuilder.append("\n");
//                            if (n == 0) {
//                                stringBuilder.append("#EXT-X-STREAM-INF:BANDWIDTH=1200000,CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=854x480,FRAME-RATE=24,SUBTITLES=\"subs-group\"");
//                            } else if (n == 1) {
//                                stringBuilder.append("#EXT-X-STREAM-INF:BANDWIDTH=6400000,CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=1280x720,FRAME-RATE=24,SUBTITLES=\"subs-group\"");
//                            } else {
//                                stringBuilder.append("#EXT-X-STREAM-INF:BANDWIDTH=12800000,CODECS=\"mp4a.40.2,avc1.66.30\",RESOLUTION=1920x1080,FRAME-RATE=24,SUBTITLES=\"subs-group\"");
//                            }
//                            stringBuilder.append("\n");
//                            stringBuilder.append(urls[n]);
//                        }
//
//
//                        /**
//                         *
//                         * #EXT-X-MEDIA 属性详解
//                         *
//                         * 1. TYPE - 媒体类型
//                         * 必填属性，指定媒体资源的类型，可选值包括：
//                         * AUDIO：音频流（如不同语言的音轨）。
//                         * SUBTITLES：字幕流（如不同语言的字幕）。
//                         * CLOSED-CAPTIONS：隐藏字幕（类似字幕流，但实现方式可能不同）。
//                         * VIDEO：视频流（较少用，通常视频流通过 #EXT-X-STREAM-INF 定义）。
//                         *
//                         * 示例：
//                         *
//                         * plaintext
//                         * #EXT-X-MEDIA:TYPE=AUDIO,NAME="English",GROUP-ID="audio-en",DEFAULT=YES,AUTOSELECT=YES,URI="audio_en.m3u8"
//                         *
//                         * 2. GROUP-ID - 分组 ID
//                         * 必填属性，用于将同类媒体资源分组（如不同语言的音频流归为同一组）。同一组内的媒体资源可通过 NAME 或 LANGUAGE 区分。
//                         * 示例：GROUP-ID="audio-lang"（音频分组）、GROUP-ID="subs-lang"（字幕分组）。
//                         * 3. NAME - 媒体名称
//                         * 可选属性，媒体资源的可读名称（如语言名称），用于客户端展示（如播放器的语言选择菜单）。
//                         * 示例：NAME="中文"、NAME="English"。
//                         * 4. LANGUAGE - 语言代码
//                         * 可选属性，指定媒体资源的语言，遵循 RFC 5646 标准（如 zh-CN、en）。
//                         * 示例：LANGUAGE="zh-Hans"（简体中文）、LANGUAGE="en"（英语）。
//                         * 5. DEFAULT - 是否为默认资源
//                         * 可选属性，值为 YES 或 NO，指定该媒体资源是否为客户端默认选择。
//                         * 同一分组中最多只能有一个 DEFAULT=YES 的资源。
//                         * 示例：DEFAULT=YES（默认音频流）。
//                         * 6. AUTOSELECT - 是否自动选择
//                         * 可选属性，值为 YES 或 NO，指定客户端是否在无用户干预时自动选择该资源。
//                         * 若 AUTOSELECT=YES，客户端会根据环境（如系统语言）自动选择匹配的资源。
//                         * 示例：AUTOSELECT=YES（自动选择匹配语言的字幕）。
//                         * 7. FORCED - 是否为强制显示
//                         * 可选属性，值为 YES 或 NO，仅用于字幕流（TYPE=SUBTITLES）。
//                         * YES：表示该字幕为强制显示（如非对话的字幕，无需用户手动选择）。
//                         * 示例：FORCED=YES（强制显示的字幕）。
//                         * 8. URI - 资源地址
//                         * 必填属性，指定媒体资源的 URI（相对或绝对路径），通常为另一个 M3U8 播放列表（如音频流的子播放列表）。
//                         * 示例：URI="audio_eng.m3u8"、URI="subs_chn.srt"（若字幕为 SRT 格式）。
//                         * 9. INSTREAM-ID - 流内唯一标识
//                         * 可选属性，用于标识同一媒体类型中的不同流（如同一语言的不同音频编码）。
//                         * 示例：INSTREAM-ID="aac-eng"、INSTREAM-ID="opus-eng"。
//                         */
//
//                        /**
//                         * 多语言音频流
//                         * #EXT-X-MEDIA:TYPE=AUDIO,NAME="中文",GROUP-ID="audio-lang",LANGUAGE="zh-CN",DEFAULT=YES,URI="audio_zh.m3u8"
//                         * #EXT-X-MEDIA:TYPE=AUDIO,NAME="English",GROUP-ID="audio-lang",LANGUAGE="en",DEFAULT=NO,URI="audio_en.m3u8"
//                         */
//
//                        // 音频轨道
////                        stringBuilder.append("\n");
////                        stringBuilder.append("\n");
////                        stringBuilder.append("# audio track");
//
//
//                        /**
//                         * 多语言字幕流
//                         * #EXT-X-MEDIA:TYPE=SUBTITLES,NAME="English",GROUP-ID="subs-lang",LANGUAGE="English",AUTOSELECT=YES,URI="subs_zh.srt"
//                         * #EXT-X-MEDIA:TYPE=SUBTITLES,NAME="Spanish",GROUP-ID="subs-lang",LANGUAGE="Spanish",AUTOSELECT=YES,URI="subs_en.srt"
//                         * #EXT-X-MEDIA:TYPE=SUBTITLES,NAME="Portuguese",GROUP-ID="subs-lang",LANGUAGE="Portuguese",AUTOSELECT=YES,URI="subs_en.srt"
//                         */
//
//                        // 字幕轨道
////                        String[] subtitles = getResources().getStringArray(R.array.hls_extra_subtitle_urls);
////                        stringBuilder.append("\n");
////                        stringBuilder.append("\n");
////                        stringBuilder.append("# subtitle track");
////                        for (int n = 0; n < 3; n++) {
////                            stringBuilder.append("\n");
////                            if (n == 0) {
////                                stringBuilder.append("#EXT-X-MEDIA:TYPE=SUBTITLES,AUTOSELECT=YES,GROUP-ID=\"subs-group\",LANGUAGE=\"en\",NAME=\"English\",URI=\"");
////                            } else if (n == 1) {
////                                stringBuilder.append("#EXT-X-MEDIA:TYPE=SUBTITLES,AUTOSELECT=NO,GROUP-ID=\"subs-group\",LANGUAGE=\"es\",NAME=\"Spanish\",URI=\"");
////                            } else {
////                                stringBuilder.append("#EXT-X-MEDIA:TYPE=SUBTITLES,AUTOSELECT=NO,GROUP-ID=\"subs-group\",LANGUAGE=\"pt\",NAME=\"Portuguese\",URI=\"");
////                            }
////                            stringBuilder.append(subtitles[n]);
////                            stringBuilder.append("\"");
////                        }
//
//                        String filename = stringBuilder.hashCode() + "";
//                        String filePath = getFilesDir().getAbsolutePath() + "/" + filename + ".m3u8";
//                        String content = stringBuilder.toString();
//
//                        FileOutputStream fos = new FileOutputStream(filePath);
//                        byte[] bytes = content.getBytes();
//                        fos.write(bytes);
//
//                        return filePath;
//                    } else {
//                        return radioButton.getTag().toString();
//                    }
                }
            }

            //
            EditText editText = findViewById(R.id.main_edit);
            Editable editableText = editText.getEditableText();
            if (editableText.length() > 0)
                return editableText.toString();

            //
            throw new Exception();
        } catch (Exception e) {
            LogUtil.log("MainActivity => getUrl => Exception " + e.getMessage(), e);
            return "";
        }
    }

    private int getKernelType() {
        try {
            RadioGroup radioGroup = findViewById(R.id.main_kernel_group);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (checked) {
                    return getResources().getIntArray(R.array.kernels_ids)[i];
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return -1;
        }
    }

    private int getDecodeType() {
        try {
            RadioGroup radioGroup = findViewById(R.id.main_decoder);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (checked) {
                    return getResources().getIntArray(R.array.decoders_ids)[i];
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return -1;
        }
    }


    private Boolean isLogEnable() {
        try {
            CheckBox checkBox = findViewById(R.id.main_log_yes);
            return checkBox.isChecked();
        } catch (Exception e) {
            return false;
        }
    }

    private void initPlayer() {

        @PlayerType.RenderType
        int renderType;
        int renderTypeId = ((RadioGroup) findViewById(R.id.main_render)).getCheckedRadioButtonId();
        switch (renderTypeId) {
            case R.id.main_render_textureview:
                renderType = PlayerType.RenderType.TEXTURE_VIEW;
                break;
            case R.id.main_render_glsurfaceview:
                renderType = PlayerType.RenderType.GL_SURFACE_VIEW;
                break;
            default:
                renderType = PlayerType.RenderType.SURFACE_VIEW;
                break;
        }

        @PlayerType.ScaleType
        int scaleType;
        int scaleTypeId = ((RadioGroup) findViewById(R.id.main_scale)).getCheckedRadioButtonId();
        switch (scaleTypeId) {
            case R.id.main_scale2:
                scaleType = PlayerType.ScaleType.REAL;
                break;
            case R.id.main_scale3:
                scaleType = PlayerType.ScaleType.FULL;
                break;
            case R.id.main_scale4:
                scaleType = PlayerType.ScaleType._1_1;
                break;
            case R.id.main_scale5:
                scaleType = PlayerType.ScaleType._4_3;
                break;
            case R.id.main_scale6:
                scaleType = PlayerType.ScaleType._5_4;
                break;
            case R.id.main_scale7:
                scaleType = PlayerType.ScaleType._16_9;
                break;
            case R.id.main_scale8:
                scaleType = PlayerType.ScaleType._16_10;
                break;
            default:
                scaleType = PlayerType.ScaleType.DEFAULT;
                break;
        }

        boolean cacheChecked = ((CheckBox) findViewById(R.id.main_cache_yes)).isChecked();


        CheckBox checkBox = findViewById(R.id.main_log_yes);
        boolean checked = checkBox.isChecked();

        PlayerSDK.init()
                // 日志开关
                .setLog(isLogEnable())
                // 播放器类型（MediaPlayer Media3Player ExoPlayer IjkPLayer）
                .setKernelType(getKernelType())
                // 渲染类型（TextuteView SurafecView）
                .setRenderType(renderType)
                // 解码器类型（仅针对 Media3Player ExoPlayer IjkPLayer）
                .setDecoderType(getDecodeType())
                // 画面比例（自动 全屏 原始 1:1 4:3 5:4 16:9 16:10）
                .setScaleType(scaleType)
                // 超时时间（默认20s）
                .setConnectTimeout(20000)
                // 缓冲超时重播（默认false）
                .setBufferingTimeoutRetry(false)
                // 播放器每次播放器都销毁（默认false）
                .setInitRelease(false)
                // 播放器生命周期自动销毁（默认true）
                .setSupportAutoRelease(false)
                // 快进类型（仅针对 MediaPlayer ExoPlayer IjkPlayer）
                .setSeekType(PlayerType.SeekType.DEFAULT)
                // 缓存
                .setCache(new Cache.Builder()
                        .setEnable(cacheChecked)
                        .setExternal(false)
                        .setSizeMB(1000)
                        .setDir("test_cache")
                        .build())
                // 代理
                .setProxy(new Proxy.Builder()
                        .setProxyBuried(new ProxyBuried())
                        .setProxyUrl(new ProxyUrl())
                        .build())
                .build();
    }
}