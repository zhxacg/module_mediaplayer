package lib.kalu.mediaplayer.core.player.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
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

    default int getNavigationBarHeight(Context context) {
        try {

            Activity activity = ContextUtil.getActivitySafely(context);
            if (null == activity)
                throw new Exception("error: activity null");

            Window window = activity.getWindow();
            int height = 0;

            // 方案1：系统资源兜底（所有版本）
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = context.getResources().getDimensionPixelSize(resourceId);
            }

            // 方案2：API 30+ 从WindowInsets获取真实显示的高度（关键适配）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && window != null) {
                try {
                    WindowInsets insets = window.getDecorView().getRootWindowInsets();
                    if (insets != null) {
                        // 获取导航栏底部高度（横屏时是right/left）
                        height = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 方案3：API 19-29 备用
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && height == 0 && window != null) {
                Rect rect = new Rect();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                // 屏幕总高度 - 可见区域高度 = 导航栏高度
                int screenHeight = window.getDecorView().getHeight();
                height = screenHeight - rect.bottom;
            }

//            // 关键：判断导航栏是否真的显示（比如全面屏手势导航时高度为0）
//            if (!isNavigationBarVisible(window)) {
//                height = 0;
//            }

            return height;
        } catch (Exception e) {
            return 0;
        }
    }

    default int getStatusBarHeight(Context context) {
        try {

            Activity activity = ContextUtil.getActivitySafely(context);
            if (null == activity)
                throw new Exception("error: activity null");

            Window window = activity.getWindow();
            int height = 0;

            // 方案1：优先从系统资源获取（所有版本通用，基础兜底）
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = context.getResources().getDimensionPixelSize(resourceId);
            }

            // 方案2：API 30+ 补充WindowInsets获取（适配异形屏、动态调整的情况）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && window != null) {
                try {
                    // 新版API：从WindowInsets获取真实显示的状态栏高度
                    WindowInsets insets = window.getDecorView().getRootWindowInsets();
                    if (insets != null) {
                        height = insets.getInsets(WindowInsets.Type.statusBars()).top;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 方案3：API 28-29 兼容（备用）
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && window != null) {
                // 适配刘海屏：获取包含刘海的状态栏高度
                DisplayCutout cutout = window.getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    height = cutout.getSafeInsetTop();
                }
            }
            // 方案4：API 19-27 备用（旧版本兜底）
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && height == 0) {
                Rect rect = new Rect();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                height = rect.top;
            }

            return height;
        } catch (Exception e) {
            return 0;
        }
    }
}
