package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiBase {

    String TAG = "VideoPlayerApiBase22";

    default StartArgs getStartArgs() {
        try {
            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getStartArgs -> error: playerLayout null");
                }
                return null;
            }
            return playerLayout.getStartArgs();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getStartArgs -> " + e.getMessage());
            }
            return null;
        }
    }

    default PlayerLayout getPlayerLayout() {
        try {
            return (PlayerLayout) ((View) this).getParent();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase -> getPlayerLayout -> " + e.getMessage());
            }
            return null;
        }
    }

    default ViewGroup findDecorView(View view) {
        try {
            View current = view;
            while (current.getParent() instanceof View) {
                current = (View) current.getParent();
            }
            return (ViewGroup) current;
        } catch (Exception e) {
            return (ViewGroup) view;
        }
    }

    default Context getBaseContext() {
        return ((View) this).getContext().getApplicationContext();
    }

    default ViewGroup getBaseViewGroup() {
        return (ViewGroup) this;
    }

    default boolean isFull() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFull -> decorView error: null");
                }
                return false;
            }
            View focus = decorView.findFocus();
            if (null == focus) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFull -> error: focus null");
                }
                return false;
            }
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_id_player) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFull -> error: focusId != R.id.module_mediaplayer_id_player");
                }
                return false;
            }
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
            if (null == decorView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFloat -> decorView error: null");
                }
                return false;
            }
            View rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFloat -> error: rootView null");
                }
                return false;
            }
            ViewParent parentView = rootView.getParent();
            if (null == parentView) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFloat -> error: parentView null");
                }
                return false;
            }
            if (parentView instanceof PlayerLayout) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiBase", "isFloat -> warning: parentView is PlayerLayout");
                }
                return false;
            }
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

    default ViewGroup getBaseVideoViewGroup() {
        try {
            ViewGroup playerGroup = getBaseViewGroup();
            return playerGroup.findViewById(R.id.module_mediaplayer_video);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase -> getBaseVideoGroup -> " + e.getMessage());
            }
            return null;
        }
    }

    default ViewGroup getBaseComponentViewGroup() {

        try {
            ViewGroup playerGroup = getBaseViewGroup();
            return playerGroup.findViewById(R.id.module_mediaplayer_component);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase -> getBaseComponentViewGroup -> " + e.getMessage());
            }
            return null;
        }
    }

    VideoKernelApi getVideoKernel();

    void setVideoKernel(VideoKernelApi kernel);

    void start(StartArgs builder);
}
