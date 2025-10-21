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
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentBuffering;
import lib.kalu.mediaplayer.core.component.ComponentComplete;
import lib.kalu.mediaplayer.core.component.ComponentError;
import lib.kalu.mediaplayer.core.component.ComponentInit;
import lib.kalu.mediaplayer.core.component.ComponentMenu;
import lib.kalu.mediaplayer.core.component.ComponentPause;
import lib.kalu.mediaplayer.core.component.ComponentPrepareGradient;
import lib.kalu.mediaplayer.core.component.ComponentSubtitle;
import lib.kalu.mediaplayer.core.component.ComponentWarningPlayInfo;
import lib.kalu.mediaplayer.core.component.ComponentWarningTrySee;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {

    public static final String INTENT_ARGS = "intent_args";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.module_mediaplayer_test_activity);

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

        findViewById(R.id.module_mediaplayer_hls_span).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                List<HlsSpanInfo> list = playerLayout.getSegments();
                if (null != list) {
                    for (HlsSpanInfo span : list) {
                        long durationUs = span.getDurationUs();
                        long relativeStartTimeUs = span.getRelativeStartTimeUs();
                        String path = span.getPath();
                        String url = span.getUrl();
                        LogUtil.log("TestActivity -> getSegments -> durationUs = " + durationUs + ", relativeStartTimeUs = " + relativeStartTimeUs + ", path = " + path + ", url = " + url);
                    }
                }
            }
        });


        // module_mediaplayer_subtitle_offset1
        findViewById(R.id.module_mediaplayer_subtitle_offset1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                boolean result = playerLayout.appendSubtitleOffsetMs(5000);
                LogUtil.log("TestActivity -> appendSubtitleOffsetMs -> result = " + result);
            }
        });

        // module_mediaplayer_subtitle_offset2
        findViewById(R.id.module_mediaplayer_subtitle_offset2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                boolean result = playerLayout.appendSubtitleOffsetMs(-5000);
                LogUtil.log("TestActivity -> appendSubtitleOffsetMs -> result = " + result);
            }
        });

        // module_mediaplayer_subtitle_add
        findViewById(R.id.module_mediaplayer_subtitle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                boolean result = playerLayout.addSubtitleTrack("");
                LogUtil.log("TestActivity -> addSubtitleTrack -> result = " + result);
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
        // seek
//        ComponentSeek seek = new ComponentSeek(getApplicationContext());
//        playerLayout.addComponent(seek);
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
            playerLayout.start(args);
        } catch (Exception e) {
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