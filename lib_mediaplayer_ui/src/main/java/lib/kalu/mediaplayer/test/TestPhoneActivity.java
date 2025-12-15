package lib.kalu.mediaplayer.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentPrepareGradient;
import lib.kalu.mediaplayer.core.component.phone.ComponentControlLandscape;
import lib.kalu.mediaplayer.core.component.phone.ComponentControlPortrait;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyBuried;
import lib.kalu.mediaplayer.proxy.ProxyTrack;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestPhoneActivity extends Activity {

    public static final String INTENT_ARGS = "intent_args";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.module_mediaplayer_test_activity_phone);

        initComponent();
        initListener();
        startPlayer();
    }

    private void initComponent() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        // control
        ComponentControlPortrait controlPortrait = new ComponentControlPortrait(getApplicationContext());
        playerLayout.addComponent(controlPortrait);
        ComponentControlLandscape controlLandscape = new ComponentControlLandscape(getApplicationContext());
        playerLayout.addComponent(controlLandscape);
        // loading
        ComponentPrepareGradient loading = new ComponentPrepareGradient(getApplicationContext());
        loading.setComponentBackgroundColorInt(Color.BLACK);
        playerLayout.addComponent(loading);
    }

    private void initListener() {
        // playerLayout
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.setOnPlayerWindowListener(new OnPlayerWindowListener() {
            @Override
            public void onWindow(int state) {
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
            public void onProgress(long position, long duration) {
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
            public void onError(String info) {

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
                public void onError(String info) {

                }
            });
            playerLayout.setOnPlayerEpisodeListener(new OnPlayerEpisodeListener() {
                @Override
                public void onEpisode(int curIndex) {
                }
            });

            playerLayout.start(args.newBuilder()
                    .setProxy(new Proxy.Builder()
                            .setProxyBuried(new ProxyBuried() {
                                @Override
                                public void onCall(String name, StartArgs startArgs, long position, long duration) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> onCall -> name = " + name + ", position = " + position + ", duration = " + duration + ", url = " + startArgs.getUrl());
                                    }
                                }
                            })
                            .setProxyUrl(new ProxyUrl() {
                                @Override
                                public String formatOpenUrl(String url) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatOpenUrl -> url = " + url + ", thread = " + Thread.currentThread().getName());
                                    }
                                    return url + "?token=1";
                                }

                                @Override
                                public String formatSegmentPath(String baseUrl, String segmentUrl) {
                                    if (LogUtil.DEBUG) {
                                        LogUtil.log("TestActivity -> formatSegmentPath -> baseUrl = " + baseUrl + ", segmentUrl = " + segmentUrl + ", thread = " + Thread.currentThread().getName());
                                    }
                                    return segmentUrl + "?install=2";
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