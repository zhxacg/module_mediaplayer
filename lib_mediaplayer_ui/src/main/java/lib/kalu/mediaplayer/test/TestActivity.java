package lib.kalu.mediaplayer.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentBuffering;
import lib.kalu.mediaplayer.core.component.ComponentComplete;
import lib.kalu.mediaplayer.core.component.ComponentError;
import lib.kalu.mediaplayer.core.component.ComponentInit;
import lib.kalu.mediaplayer.core.component.ComponentMenu;
import lib.kalu.mediaplayer.core.component.ComponentPause;
import lib.kalu.mediaplayer.core.component.ComponentPrepareGradient;
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.core.component.ComponentSubtitle;
import lib.kalu.mediaplayer.core.component.ComponentWarningPlayInfo;
import lib.kalu.mediaplayer.core.component.ComponentWarningTrySee;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyTrack;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {

    public static final String INTENT_ARGS = "intent_args";
    public static final String INTENT_TV = "intent_tv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.lib_mp_test_activity);

        initComponent();
        initListener();
        startPlayer();

        // 视频轨道
        findViewById(R.id.module_mediaplayer_track_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackInfo(1);
            }
        });
        // 音频轨道
        findViewById(R.id.module_mediaplayer_track_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackInfo(2);
            }
        });
        // 字幕轨道
        findViewById(R.id.module_mediaplayer_track_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackInfo(3);
            }
        });

        // module_mediaplayer_subtitle_offset1
        findViewById(R.id.module_mediaplayer_subtitle_offset1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                boolean result = playerLayout.setPlaybackSubtitleOffsetMs(5000);
                if (LogUtil.DEBUG) {
                    LogUtil.log("TestActivity -> appendSubtitleOffsetMs -> result = " + result);
                }
            }
        });

        // module_mediaplayer_subtitle_offset2
        findViewById(R.id.module_mediaplayer_subtitle_offset2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                boolean result = playerLayout.setPlaybackSubtitleOffsetMs(-5000);
                if (LogUtil.DEBUG) {
                    LogUtil.log("TestActivity -> appendSubtitleOffsetMs -> result = " + result);
                }
            }
        });

        // module_mediaplayer_subtitle_add
        findViewById(R.id.module_mediaplayer_subtitle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                boolean result = playerLayout.addSubtitleTrack("");
                if (LogUtil.DEBUG) {
                    LogUtil.log("TestActivity -> addSubtitleTrack -> result = " + result);
                }
            }
        });
    }

    public void toggleTrack(TrackInfo trackInfo) {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.toggleTrack(trackInfo);
    }

//    public void toggleTrackRoleFlagSubtitle(int roleFlag) {
//        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//        playerLayout.toggleTrackRoleFlagSubtitle(roleFlag);
//    }
//
//    public void toggleTrackRoleFlagAudio(int roleFlag) {
//        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//        playerLayout.toggleTrackRoleFlagAudio(roleFlag);
//    }
//
//    public void toggleTrackRoleFlagVideo(int roleFlag) {
//        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//        playerLayout.toggleTrackRoleFlagVideo(roleFlag);
//    }

    private void showTrackInfo(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(TestDialog.BUNDLE_TYPE, type);

        TestDialog dialog = new TestDialog();
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "TestDialog");
    }

    private void initComponent() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        // menu
        ComponentMenu menu = new ComponentMenu(getApplicationContext());
        playerLayout.addComponent(menu);
        // loading
        ComponentPrepareGradient loading = new ComponentPrepareGradient(getApplicationContext());
        loading.setComponentBackgroundColorInt(Color.BLACK);
        playerLayout.addComponent(loading);
        // complete
        ComponentComplete end = new ComponentComplete(getApplicationContext());
        playerLayout.addComponent(end);
        // error
        ComponentError error = new ComponentError(getApplicationContext());
        error.setComponentBackgroundColorInt(Color.BLACK);
        playerLayout.addComponent(error);
        // net
        ComponentBuffering speed = new ComponentBuffering(getApplicationContext());
        playerLayout.addComponent(speed);
        // init
        ComponentInit init = new ComponentInit(getApplicationContext());
        playerLayout.addComponent(init);
        // pause
        ComponentPause pause = new ComponentPause(getApplicationContext());
        playerLayout.addComponent(pause);
        // try
        ComponentWarningTrySee trys = new ComponentWarningTrySee(getApplicationContext());
        playerLayout.addComponent(trys);
        // 起播详情
        ComponentWarningPlayInfo info = new ComponentWarningPlayInfo(getApplicationContext());
        playerLayout.addComponent(info);
        // 字幕
        ComponentSubtitle subtitle = new ComponentSubtitle(getApplicationContext());
        playerLayout.addComponent(subtitle);

        // seek
        ComponentSeek seek = new ComponentSeek(getApplicationContext());
        playerLayout.addComponent(seek);
//        ComponentSeek2 seek2 = new ComponentSeek2(getApplicationContext());
//        playerLayout.addComponent(seek2);
    }

    private void initListener() {
        // playerLayout
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.setOnPlayerWindowStateChangeListener(new OnPlayerWindowStateChangeListener() {
            @Override
            public void onState(int state) {
                switch (state) {
                    case PlayerType.WindowType.DEFAULT:
                        //普通模式
                        break;
                    case PlayerType.WindowType.FULL:
                        //全屏模式
                        break;
                    case PlayerType.WindowType.FLOAT:
                        //小屏模式
                        break;
                }
            }
        });
        playerLayout.setOnPlayerProgressListener(new OnPlayerProgressListener() {
            @Override
            public void onProgress(long trySeeDuration, long position, long duration) {
            }
        });
        playerLayout.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onError(int errCode) {
            }
        });
    }

    private void startPlayer() {

        try {
            StartArgs args = (StartArgs) getIntent().getSerializableExtra(INTENT_ARGS);
            if (null == args)
                throw new Exception("error: args null");
            PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
            playerLayout.setOnPlayerEventListener(new OnPlayerEventListener() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onStart() {
                }

                @Override
                public void onError(int errCode) {
                }
            });
            playerLayout.setOnPlayerEpisodeListener(new OnPlayerEpisodeListener() {
                @Override
                public void onEpisode(int position, int count) {

                }
            });

            playerLayout.start(args.newBuilder()
                    .setProxy(new Proxy.Builder()
                            .setProxyUrl(new ProxyUrl() {

                                @Override
                                public void formatOpenUrl(boolean isFirst, String url) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatOpenUrl -> isFirst = " + isFirst + ", url = " + url + ", thread = " + Thread.currentThread().getName());
                                    }
                                }

                                @Override
                                public String formatM3u8Url(String url) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatM3u8Url -> url = " + url + ", thread = " + Thread.currentThread().getName());
                                    }
                                    return url;
                                }

                                @Override
                                public String formatSubtitleUrl(String url) {
                                    return url;
                                }

                                @Override
                                public String formatSegmentUrl(String url) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatSegmentUrl -> url = " + url + ", thread = " + Thread.currentThread().getName());
                                    }
                                    return url;
                                }

                                @Override
                                public String formatSegmentPath(String baseUrl, String segmentUrl) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatSegmentPath -> baseUrl = " + baseUrl + ", segmentUrl = " + segmentUrl + ", thread = " + Thread.currentThread().getName());
                                    }
                                    return segmentUrl;
                                }

                                @Override
                                public String formatMultivariantM3u8Url(String mainUrl, String multivariantPath) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatReferenceM3u8Url -> mainUrl = " + mainUrl + ", multivariantPath = " + multivariantPath + ", thread = " + Thread.currentThread().getName());
                                    }
                                    return multivariantPath;
                                }
                            })
                            .setProxyTrack(new ProxyTrack() {
                                @Override
                                public void formatVideoTrackInfo(List<TrackInfo> tracksList, StartArgs startArgs) {

                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatVideoTrackInfo -> tracksList = " + tracksList);
                                    }

                                    try {
                                        UrlArgs urlArgs = startArgs.getUrlArgs();
                                        UrlArgs.Item mainVideo = urlArgs.getMainVideo();
                                        UrlArgs.Item[] extVideo = urlArgs.getExtVideo();
                                        for (TrackInfo item : tracksList) {
                                            int i = tracksList.indexOf(item);
                                            if (i == 0) {
                                                item.setLabel(mainVideo.getLabel());
                                            } else {
                                                item.setLabel(extVideo[i - 1].getLabel());
                                            }
                                        }
                                    } catch (Exception e) {
                                    }

                                }

                                @Override
                                public void formatAudioTrackInfo(List<TrackInfo> tracksList, StartArgs startArgs) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatAudioTrackInfo -> tracksList = " + tracksList);
                                    }

                                    try {
                                        UrlArgs urlArgs = startArgs.getUrlArgs();
                                        UrlArgs.Item[] extAudio = urlArgs.getExtAudio();
                                        for (TrackInfo item : tracksList) {
                                            int i = tracksList.indexOf(item);
                                            item.setLabel(extAudio[i].getLabel());
                                            item.setLanguage(extAudio[i].getLanguage());
                                        }
                                    } catch (Exception e) {
                                    }
                                }

                                @Override
                                public void formatSubtitleTrackInfo(List<TrackInfo> tracksList, StartArgs startArgs) {
                                }
                            })
                            .build())
                    .build());
        } catch (
                Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startFull() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.startFull();
    }

    private void stopFull() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.stopFull();
    }

    private void startFloat() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.startFloat();
    }

    private void stopFloat() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.stopFloat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.resume(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.pause(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.stop();
        videoLayout.release();
    }
}