package lib.kalu.mediaplayer.test;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import java.util.List;

import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.util.LogUtil;

public class TestDialog extends DialogFragment {

    public static String BUNDLE_TYPE = "BUNDLE_TYPE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.module_mediaplayer_test_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            int type = getArguments().getInt(BUNDLE_TYPE, 1);
            PlayerLayout playerLayout = getActivity().findViewById(R.id.module_mediaplayer_test_video);
            List<TrackInfo> list;
            if (type == 1) {
                list = playerLayout.getTrackInfoVideo();
            } else if (type == 2) {
                list = playerLayout.getTrackInfoAudio();
            } else {
                list = playerLayout.getTrackInfoSubtitle();
            }

            for (TrackInfo track : list) {

                if (LogUtil.DEBUG) {
                    LogUtil.log("TestDialog -> showTrackInfo -> type = " + track.toString());
                }

                String curName;
                // 视频轨道
                if (type == 1) {
                    int bitrate = track.getBitrate();
                    int width = track.getWidth();
                    int height = track.getHeight();
                    String sampleMimeType = track.getSampleMimeType();
                    int roleFlags = track.getRoleFlags();
                    String language = track.getLanguage();
                    curName = "roleFlags = " + roleFlags + ", language = " + language + ", sampleMimeType = " + sampleMimeType + ", bitrate = " + bitrate + ", width = " + width + ", height = " + height;
                }
                // 音频轨道
                else if (type == 2) {
                    String sampleMimeType = track.getSampleMimeType();
                    int roleFlags = track.getRoleFlags();
                    String language = track.getLanguage();
                    curName = "roleFlags = " + roleFlags + ", language = " + language + ", sampleMimeType = " + sampleMimeType;
                }
                // 字幕轨道
                else if (type == 3) {
                    int roleFlags = track.getRoleFlags();
                    String language = track.getLanguage();
                    curName = "roleFlags = " + roleFlags + ", language = " + language;
                } else {
                    continue;
                }

                RadioButton radioButton = new RadioButton(view.getContext());
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
                radioButton.setLayoutParams(layoutParams);
                radioButton.setText(curName);
                boolean isTrackSelected = track.isTrackSelected();
                radioButton.setChecked(isTrackSelected);
                boolean isTrackSupported = track.isTrackSupported();
                radioButton.setEnabled(isTrackSupported);

                //
                RadioGroup radioGroup = view.findViewById(R.id.test_dialog_group);
                radioGroup.addView(radioButton);

                //
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ((TestActivity) getActivity()).toggleTrack(track);
                        //
                        dismiss();
                    }
                });
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("TestDialog -> onViewCreated -> error: " + e.getMessage(), e);
            }
        }
    }
}
