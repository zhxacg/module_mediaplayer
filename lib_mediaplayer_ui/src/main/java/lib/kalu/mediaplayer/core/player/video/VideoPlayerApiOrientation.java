package lib.kalu.mediaplayer.core.player.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.PlayerInitProvider;
import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiOrientation extends VideoPlayerApiBase, VideoPlayerApiRender, VideoPlayerApiListener {

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

    @SuppressLint("SourceLockedOrientationActivity")
    default boolean setScreenOrientationPortrait() {
        try {

            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            callScreenPortrait(false, true);

//            PlayerLayout playerLayout = getPlayerLayout();
//            if (null == playerLayout)
//                throw new Exception("error: screenRestore null");
//
//            View decorView = activity.getWindow().getDecorView();
//            Rect visibleRect = new Rect();
//            decorView.getWindowVisibleDisplayFrame(visibleRect);
//            int screenWidth = decorView.getRootView().getWidth();
//            int screenHeight = decorView.getRootView().getHeight();
//
//            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
//            int viewWidth = layoutParams.width;
//            int viewHeight = layoutParams.height;
//
//            if (LogUtil.DEBUG) {
//                int navBarHeight = screenHeight - visibleRect.bottom;
//                LogUtil.log(TAG, "setRequestedOrientation -> isVt = " + isVt + ", viewWidth = " + viewWidth + ", screenWidth = " + screenWidth + ", viewHeight = " + viewHeight + ", screenHeight = " + screenHeight + ", navBarHeight = " + navBarHeight);
//            }
//
//            if (isVt) {
//                layoutParams.width = Layout_Params[0];
//                layoutParams.height = Layout_Params[1];
//            } else {
//                layoutParams.width = Math.max(screenWidth, screenHeight);
//                layoutParams.height = Math.min(screenWidth, screenHeight);
//            }
//            playerLayout.setLayoutParams(layoutParams);
//
//            if (isVt) {
//                Layout_Params[0] = -100;
//                Layout_Params[1] = -100;
//            } else {
//                Layout_Params[0] = viewWidth;
//                Layout_Params[1] = viewHeight;
//            }

//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "setRequestedOrientation -> Layout_Params[0] = " + Layout_Params[0] + ", Layout_Params[1] = " + Layout_Params[1]+", layoutParams.width = "+playerLayout.getLayoutParams().width+", layoutParams.height = "+playerLayout.getLayoutParams().height);
//            }

            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setScreenOrientationPortrait -> Exception: " + e.getMessage());
            }
            return false;
        }
    }

    default boolean setScreenOrientationLandspace() {

        try {

            Activity activity = PlayerInitProvider.getCurrentActivity();
            if (null == activity)
                throw new Exception("error: activity null");

            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            callScreenLandspace(false, true);

//            PlayerLayout playerLayout = getPlayerLayout();
//            if (null == playerLayout)
//                throw new Exception("error: screenRestore null");
//
//            View decorView = activity.getWindow().getDecorView();
//            Rect visibleRect = new Rect();
//            decorView.getWindowVisibleDisplayFrame(visibleRect);
//            int screenWidth = decorView.getRootView().getWidth();
//            int screenHeight = decorView.getRootView().getHeight();
//
//            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
//            int viewWidth = layoutParams.width;
//            int viewHeight = layoutParams.height;
//
//            if (LogUtil.DEBUG) {
//                int navBarHeight = screenHeight - visibleRect.bottom;
//                LogUtil.log(TAG, "setRequestedOrientation -> isVt = " + isVt + ", viewWidth = " + viewWidth + ", screenWidth = " + screenWidth + ", viewHeight = " + viewHeight + ", screenHeight = " + screenHeight + ", navBarHeight = " + navBarHeight);
//            }
//
//            if (isVt) {
//                layoutParams.width = Layout_Params[0];
//                layoutParams.height = Layout_Params[1];
//            } else {
//                layoutParams.width = Math.max(screenWidth, screenHeight);
//                layoutParams.height = Math.min(screenWidth, screenHeight);
//            }
//            playerLayout.setLayoutParams(layoutParams);
//
//            if (isVt) {
//                Layout_Params[0] = -100;
//                Layout_Params[1] = -100;
//            } else {
//                Layout_Params[0] = viewWidth;
//                Layout_Params[1] = viewHeight;
//            }

//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "setRequestedOrientation -> Layout_Params[0] = " + Layout_Params[0] + ", Layout_Params[1] = " + Layout_Params[1]+", layoutParams.width = "+playerLayout.getLayoutParams().width+", layoutParams.height = "+playerLayout.getLayoutParams().height);
//            }

            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setScreenOrientationLandspace -> Exception: " + e.getMessage());
            }
            return false;
        }
    }

//    default boolean screenFull(Context context) {
//        try {
//
//            PlayerLayout playerLayout = getPlayerLayout();
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "screenFull -> playerLayout = " + playerLayout);
//            }
//            if (null == playerLayout)
//                throw new Exception("error: playerLayout null");
//
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//            int widthPixels = metrics.widthPixels;
//            int heightPixels = metrics.heightPixels;
//
//            ViewGroup.LayoutParams layoutParams = playerLayout.getLayoutParams();
//            int width = layoutParams.width;
//            int height = layoutParams.height;
//
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "screenFull -> width = " + width + ", widthPixels = " + widthPixels + ", height = " + height + ", heightPixels = " + heightPixels);
//            }
//
//
//            layoutParams.width = Math.max(widthPixels, heightPixels);
//            layoutParams.height = Math.min(widthPixels, heightPixels);
//            playerLayout.setLayoutParams(layoutParams);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
