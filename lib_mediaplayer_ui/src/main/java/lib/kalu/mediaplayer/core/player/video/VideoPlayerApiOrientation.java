package lib.kalu.mediaplayer.core.player.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.PlayerInitProvider;
import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.StatusBarUtil;

public interface VideoPlayerApiOrientation extends VideoPlayerApiBase, VideoPlayerApiRender, VideoPlayerApiListener, VideoPlayerApiCall {

    String TAG = "VideoPlayerApiOrientation";

    int[] Layout_Params = new int[]{-100, -100};

    default boolean canBackPress(Context context) {
        return isPortrait(context);
    }

    default boolean isLandscape(Context context) {
        try {
            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("error: screenRestore null");

            View decorView = activity.getWindow().getDecorView();
            Rect visibleRect = new Rect();
            decorView.getWindowVisibleDisplayFrame(visibleRect);
            int screenWidth = decorView.getRootView().getWidth();
            int screenHeight = decorView.getRootView().getHeight();

            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
            int viewWidth = layoutParams.width;
            int viewHeight = layoutParams.height;

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "isPortrait -> viewWidth = " + viewWidth + ", screenWidth = " + screenWidth + ", viewHeight = " + viewHeight + ", screenHeight = " + screenHeight);
            }

            return viewWidth == Math.max(screenWidth, screenHeight) && viewHeight == Math.min(screenWidth, screenHeight);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPortrait(Context context) {

        try {

            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("error: screenRestore null");

            View decorView = activity.getWindow().getDecorView();
            Rect visibleRect = new Rect();
            decorView.getWindowVisibleDisplayFrame(visibleRect);
            int screenWidth = decorView.getRootView().getWidth();
            int screenHeight = decorView.getRootView().getHeight();

            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
            int viewWidth = layoutParams.width;
            int viewHeight = layoutParams.height;

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "isPortrait -> viewWidth = " + viewWidth + ", screenWidth = " + screenWidth + ", viewHeight = " + viewHeight + ", screenHeight = " + screenHeight);
            }

            return viewWidth != Math.max(screenWidth, screenHeight) && viewHeight != Math.min(screenWidth, screenHeight);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isScreenOrientationPortrait() {
        try {

            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            return activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "isScreenOrientationPortrait -> Exception: " + e.getMessage());
            }
            return false;
        }
    }

    default boolean isScreenOrientationLandspace() {
        try {

            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            return activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "isScreenOrientationLandspace -> Exception: " + e.getMessage());
            }
            return false;
        }
    }

    default boolean requestScreenOrientation(@PlayerType.ScreenOrientation.Value int value) {

        try {

            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            // 状态栏隐藏
            if (value == PlayerType.ScreenOrientation.PORTRAIT) {
                StatusBarUtil.toggleStatusBarText(activity, true);
            } else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
                StatusBarUtil.toggleStatusBarText(activity, false);
            }

            // 屏幕切换
            if (value == PlayerType.ScreenOrientation.PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                callScreenOrientation(true, true, PlayerType.ScreenOrientation.PORTRAIT);
            } else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                callScreenOrientation(true, true, PlayerType.ScreenOrientation.LANDSPACE);
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "requestScreenOrientation -> Exception: " + e.getMessage());
            }
            return false;
        }
    }
}
