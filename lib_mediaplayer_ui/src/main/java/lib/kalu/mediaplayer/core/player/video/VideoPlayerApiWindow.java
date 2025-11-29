package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.PlayerView;

public interface VideoPlayerApiWindow extends VideoPlayerApiBase, VideoPlayerApiRender, VideoPlayerApiListener {

    default boolean isFull() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            View focus = decorView.findFocus();
            if (null == focus)
                throw new Exception("error: focus null");
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_id_player)
                throw new Exception("error: focusId != R.id.module_mediaplayer_id_player");
//            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
//            if (null == viewRoot)
//                throw new Exception("viewRoot error: null");
//            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
//            if (null == tag)
//                throw new Exception("warning: tagId null");
//            int id = ((View) viewRoot.getParent()).getId();
//            if (id == (int) tag)
//                throw new Exception("warning: current not full");
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase -> isFull -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isFloat() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            View rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView)
                throw new Exception("error: rootView null");
            ViewParent parentView = rootView.getParent();
            if (null == parentView)
                throw new Exception("error: parentView null");
            if (parentView instanceof PlayerLayout)
                throw new Exception("warning: parentView is PlayerLayout");
            ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            return width != ViewGroup.LayoutParams.MATCH_PARENT && height != ViewGroup.LayoutParams.MATCH_PARENT;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase -> isFloat -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean startFull() {
        try {
            callEvent(PlayerType.EventType.WINDOW_FULL_START);
            boolean full = isFull();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> full = " + full);
            }
            if (full)
                throw new Exception("warning: full true");
            ViewGroup decorView = findDecorView((View) this);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> decorView = " + decorView);
            }
            if (null == decorView)
                throw new Exception("error: decorView null");
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> rootView = " + rootView);
            }
            if (null == rootView)
                throw new Exception("error: rootView null");
            ViewParent rootViewParent = rootView.getParent();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> rootViewParent = " + rootViewParent);
            }
            if (null == rootViewParent)
                throw new Exception("error: rootViewParent null");
            // 保存全屏之前的focusId, 退出全屏时需要恢复focusId
            View focusView = decorView.findFocus();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> focusView = " + focusView);
            }
            if (null == focusView)
                throw new Exception("error: focusView null");
            int parentId = ((View) rootViewParent).getId();
            int focusId = focusView.getId();
            rootView.setTag(R.id.module_mediaplayer_id_player_focus, focusId);
            rootView.setTag(R.id.module_mediaplayer_id_player_parent, parentId);
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            ((ViewGroup) rootViewParent).removeView(rootView);
            // 添加View
            int childCount = decorView.getChildCount();
            decorView.addView(rootView, childCount);
            // 切换焦点
            for (int i = 0; i < childCount; i++) {
                View childAt = decorView.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ViewGroup))
                    continue;
                childAt.setFocusable(false);
                ((ViewGroup) childAt).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
            // 强制获焦
            rootView.setFocusable(true);
            rootView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            rootView.requestFocus();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> requestFocus");
            }
            // ...
            initRenderView();
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.EventType.WINDOW_FULL_SUCC);
            callWindow(PlayerType.WindowType.FULL);
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFull -> " + e.getMessage());
            }
            callEvent(PlayerType.EventType.WINDOW_FULL_FAIL);
            return false;
        }
    }

    default boolean stopFull() {
        try {
            callEvent(PlayerType.EventType.WINDOW_FULL_START);
            boolean full = isFull();
            if (!full)
                throw new Exception("warning: full false");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("error: decorView null");
            // 查找View
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView)
                throw new Exception("error: rootView null");
            Object tagFocus = rootView.getTag(R.id.module_mediaplayer_id_player_focus);
            if (null == tagFocus)
                throw new Exception("error: tagFocus null");
            Object tagParent = rootView.getTag(R.id.module_mediaplayer_id_player_parent);
            if (null == tagParent)
                throw new Exception("error: tagParent null");
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            decorView.removeView(rootView);
            // 添加View
            ViewGroup parentView = decorView.findViewById((int) tagParent);
            parentView.addView(rootView, 0);
            // 切换焦点
            int childCount = decorView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = decorView.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ViewGroup))
                    continue;
                childAt.setFocusable(true);
                ((ViewGroup) childAt).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }
            rootView.setFocusable(false);
            rootView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            // 强制获焦
            View focusView = decorView.findViewById((int) tagFocus);
            focusView.requestFocus();
            // 4
            initRenderView();
            // 5
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.EventType.WINDOW_FULL_SUCC);
            callWindow(PlayerType.WindowType.FULL);
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> stopFull -> " + e.getMessage());
            }
            callEvent(PlayerType.EventType.WINDOW_FULL_FAIL);
            return false;
        }
    }

    default boolean startFloat() {
        try {
            callEvent(PlayerType.EventType.WINDOW_FLOAT_START);
            boolean aFloat = isFloat();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFloat -> aFloat = " + aFloat);
            }
            if (aFloat)
                throw new Exception("warning: aFloat true");
            ViewGroup decorView = findDecorView((View) this);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFloat -> decorView = " + decorView);
            }
            if (null == decorView)
                throw new Exception("error: decorView null");
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFloat -> rootView = " + rootView);
            }
            if (null == rootView)
                throw new Exception("error: rootView null");
            ViewParent rootViewParent = rootView.getParent();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFloat -> rootViewParent = " + rootViewParent);
            }
            if (null == rootViewParent)
                throw new Exception("error: rootViewParent null");
            int parentId = ((View) rootViewParent).getId();
            rootView.setTag(R.id.module_mediaplayer_id_player_parent, parentId);
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            ((ViewGroup) rootViewParent).removeView(rootView);
            // 切换大小
            int width = getBaseContext().getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_float_width);
            int height = width * 9 / 16;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.setLayoutParams(layoutParams);
            // 添加View
            int childCount = decorView.getChildCount();
            decorView.addView(rootView, childCount);
            // ...
            initRenderView();
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.EventType.WINDOW_FLOAT_SUCC);
            callWindow(PlayerType.WindowType.FLOAT);
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> startFloat -> " + e.getMessage());
            }
            callEvent(PlayerType.EventType.WINDOW_FLOAT_FAIL);
            return false;
        }
    }

    default boolean stopFloat() {
        try {
            callEvent(PlayerType.EventType.WINDOW_FLOAT_START);
            boolean aFloat = isFloat();
            if (!aFloat)
                throw new Exception("warning: aFloat false");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("error: decorView null");
            // 查找View
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView)
                throw new Exception("error: rootView null");
            Object tagParent = rootView.getTag(R.id.module_mediaplayer_id_player_parent);
            if (null == tagParent)
                throw new Exception("error: tagParent null");
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            decorView.removeView(rootView);
            // 切换大小
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.setLayoutParams(layoutParams);
            // 添加View
            ViewGroup parentView = decorView.findViewById((int) tagParent);
            parentView.addView(rootView, 0);
            // 4
            initRenderView();
            // 5
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.EventType.WINDOW_FLOAT_SUCC);
            callWindow(PlayerType.WindowType.DEFAULT);
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> stopFloat -> " + e.getMessage());
            }
            callEvent(PlayerType.EventType.WINDOW_FLOAT_FAIL);
            return false;
        }
    }
}
