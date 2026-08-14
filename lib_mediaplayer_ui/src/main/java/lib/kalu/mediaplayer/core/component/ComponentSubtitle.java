package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSubtitle extends RelativeLayout implements ComponentApi {

    public ComponentSubtitle(Context context) {
        super(context);
        inflate();

//        // 创建 CaptionStyleCompat 对象来定义字幕样式
//        lib.kalu.mediaplayer.widget.subtitle.exo.CaptionStyleCompat captionStyle = new lib.kalu.mediaplayer.widget.subtitle.exo.CaptionStyleCompat(
//                Color.WHITE, // 文字颜色
//                Color.BLACK, // 背景颜色
//                Color.TRANSPARENT, // 窗口颜色
//                lib.kalu.mediaplayer.widget.subtitle.exo.CaptionStyleCompat.EDGE_TYPE_OUTLINE, // 边缘类型
//                Color.WHITE, // 边缘颜色
//                null // 字体
//        );
//
//        lib.kalu.mediaplayer.widget.subtitle.exo.SubtitleView subtitleView = findViewById(R.id.module_mediaplayer_component_subtitle_root);
//        // 设置字幕样式到 SubtitleView
//        subtitleView.setStyle(captionStyle);
//        // 设置字幕字体大小
////        subtitleView.setFixedTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION, SubtitleView.DEFAULT_TEXT_SIZE_FRACTION);
    }

    @Override
    public int initLayoutId() {
        return R.layout.lib_mp_component_subtitle;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_subtitle_root;
    }

    @Override
    public void onUpdateSubtitle(int kernel, CharSequence result) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentSubtitle -> onUpdateSubtitle -> kernel = " + kernel + ", result = " + result);
        }

        try {
            if (null == result || result.length() == 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentSubtitle -> onUpdateSubtitle -> warning: result null or empty");
                }
                TextView textView = findViewById(R.id.module_mediaplayer_component_subtitle_text);
                textView.setText("");
                return;
            }
            TextView textView = findViewById(R.id.module_mediaplayer_component_subtitle_text);
            textView.setText(result);
        } catch (Exception e) {
            TextView textView = findViewById(R.id.module_mediaplayer_component_subtitle_text);
            textView.setText("");
        }
    }

    @Override
    public void onUpdateEvent(int state) {

        if (state == PlayerType.EventType.INIT) {
//
////            // 定制字幕样式
////            androidx.media3.ui.CaptionStyleCompat captionStyle = new androidx.media3.ui.CaptionStyleCompat()
////                    .setTextColor(Color.WHITE)
////                    .setBackgroundColor(Color.BLACK)
////                    .setTextSize(20)
////                    .setTextAlignment(C.TEXT_ALIGNMENT_CENTER)
////                    .build();
////
////            // 应用字幕样式
////            subtitleView.setDefaultStyle(captionStyle);
//            subtitleView.set
        }
    }
}
