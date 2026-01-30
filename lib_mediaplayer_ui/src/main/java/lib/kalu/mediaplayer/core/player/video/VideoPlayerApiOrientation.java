package lib.kalu.mediaplayer.core.player.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import lib.kalu.mediaplayer.PlayerLayout;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.ContextUtil;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiOrientation extends VideoPlayerApiBase, VideoPlayerApiRender, VideoPlayerApiListener, VideoPlayerApiCall {

    String TAG = "VideoPlayerApiOrientation";

    int[] Layout_Params = new int[]{-100, -100};

    default boolean canBackPress(Context context) {
        return isPortrait(context);
    }

    default boolean isLandscape(Context context) {
        try {
            Activity activity = ContextUtil.getActivitySafely(context);
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

            Activity activity = ContextUtil.getActivitySafely(context);
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

//    default boolean isScreenOrientationPortrait() {
//        try {
//
//            Activity activity = PlayerInitProvider.getCurrentActivity();
//            if (null == activity)
//                throw new Exception("error: activity null");
//
//            return activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "isScreenOrientationPortrait -> Exception: " + e.getMessage());
//            }
//            return false;
//        }
//    }
//
//    default boolean isScreenOrientationLandspace() {
//        try {
//
//            Activity activity = PlayerInitProvider.getCurrentActivity();
//            if (null == activity)
//                throw new Exception("error: activity null");
//
//            return activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "isScreenOrientationLandspace -> Exception: " + e.getMessage());
//            }
//            return false;
//        }
//    }

//    default boolean requestScreenOrientation(
//            Context context,
//            @PlayerType.ScreenOrientation.Value int value,
//            boolean formatScreen) {
//
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "requestScreenOrientation -> value = " + value);
//        }
//
//        try {
//
//            Activity activity = ContextUtil.getActivitySafely(context);
//            if (null == activity)
//                throw new Exception("error: activity null");
//
//            // 状态栏 导航栏
//            if (formatScreen) {
//                Window window = activity.getWindow();
//                WindowInsetsController windowInsetsController = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    windowInsetsController = window.getInsetsController();
//                }
//                // 退出全屏：显示状态栏和导航栏
//                if (value == PlayerType.ScreenOrientation.PORTRAIT) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        if (windowInsetsController != null) {
//                            windowInsetsController.show(android.view.WindowInsets.Type.systemBars());
//                        }
//                        window.setDecorFitsSystemWindows(true);
//                    } else {
//                        // 低版本恢复系统 UI 显示
//                        window.getDecorView().setSystemUiVisibility(
//                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        );
//                    }
//                }
//                // 全屏模式：隐藏状态栏和导航栏，设置沉浸式
//                else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        if (windowInsetsController != null) {
//                            windowInsetsController.hide(android.view.WindowInsets.Type.systemBars());
//                        }
//                        window.setDecorFitsSystemWindows(false);
//                    } else {
//                        // 低版本直接设置 systemUiVisibility
//                        window.getDecorView().setSystemUiVisibility(
//                                View.SYSTEM_UI_FLAG_FULLSCREEN
//                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        );
//                    }
//                }
//            }
//
//            // 屏幕切换
//            if (value == PlayerType.ScreenOrientation.PORTRAIT) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                callScreenOrientation(true, true, PlayerType.ScreenOrientation.PORTRAIT);
//            } else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                callScreenOrientation(true, true, PlayerType.ScreenOrientation.LANDSPACE);
//            }
//            return true;
//        } catch (Exception e) {
//            if (LogUtil.DEBUG) {
//                LogUtil.log(TAG, "requestScreenOrientation -> Exception: " + e.getMessage());
//            }
//            return false;
//        }
//    }

    default boolean requestScreenOrientation(
            Context context,
            @PlayerType.ScreenOrientation.Value int value,
            boolean formatScreen) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "requestScreenOrientation -> value = " + value);
        }

        try {

            Activity activity = ContextUtil.getActivitySafely(context);
            if (null == activity)
                throw new Exception("error: activity null");

            // 状态栏 导航栏
            if (formatScreen) {
                Window window = activity.getWindow();
                WindowInsetsController windowInsetsController = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    windowInsetsController = window.getInsetsController();
                }
                // 退出全屏：显示状态栏和导航栏
                if (value == PlayerType.ScreenOrientation.PORTRAIT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (windowInsetsController != null) {
                            windowInsetsController.show(android.view.WindowInsets.Type.systemBars());
                            // 新增：恢复默认行为
                            windowInsetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
                        }
                        window.setDecorFitsSystemWindows(true);
                    } else {
                        // 新增：完全恢复系统UI默认状态（原代码布局参数会导致UI异常）
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
                // 全屏模式：隐藏状态栏和导航栏，设置沉浸式
                else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (windowInsetsController != null) {
                            windowInsetsController.hide(android.view.WindowInsets.Type.systemBars());
                            // 核心新增：设置滑动显示后自动隐藏的行为（解决高版本不自动消失）
                            windowInsetsController.setSystemBarsBehavior(
                                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            );
                        }
                        window.setDecorFitsSystemWindows(false);
                        // 新增：高版本监听系统UI变化，确保自动隐藏
                        if (windowInsetsController != null) {
                            windowInsetsController.addOnControllableInsetsChangedListener((insetsController, typeMask) -> {
                                if ((typeMask & (android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars())) != 0) {
                                    activity.getWindow().getDecorView().postDelayed(() -> {
                                        if (!activity.isFinishing()) {
                                            insetsController.hide(android.view.WindowInsets.Type.systemBars());
                                        }
                                    }, 2000);
                                }
                            });
                        }
                    } else {
                        // 低版本保持原有参数，新增监听确保自动隐藏
                        window.getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        );
                        // 核心新增：低版本监听系统UI变化，拉出后自动重置
                        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                activity.getWindow().getDecorView().postDelayed(() -> {
                                    if (!activity.isFinishing()) {
                                        window.getDecorView().setSystemUiVisibility(
                                                View.SYSTEM_UI_FLAG_FULLSCREEN
                                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        );
                                    }
                                }, 2000);
                            }
                        });
                    }
                    // 新增：全屏时保持屏幕常亮（可选，提升播放体验）
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }

            // 屏幕切换
            if (value == PlayerType.ScreenOrientation.PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                callScreenOrientation(true, true, PlayerType.ScreenOrientation.PORTRAIT);
                // 新增：竖屏时清除屏幕常亮
                if (formatScreen) {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
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
