package lib.kalu.mediaplayer.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

/**
 * 状态栏文字显示/隐藏工具类（纯原生 Java 版本，无 androidx）
 * 适配 Android 6.0+（文字颜色控制）、Android 11+（全屏隐藏文字）
 */
public class StatusBarUtil {

    /**
     * 切换状态栏文字显示/隐藏（核心方法）
     * @param activity 当前Activity
     * @param showText true=显示文字，false=隐藏文字（视觉/全屏）
     * @param isDarkText 显示文字时，文字是否为深色（true=黑字，false=白字）
     */
    public static void toggleStatusBarText(Activity activity, boolean showText, boolean isDarkText) {
        Window window = activity.getWindow();
        // 第一步：清除全屏标记，确保状态栏基础可见
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (showText) {
            // 场景1：显示状态栏文字
            showStatusBarText(window, activity, isDarkText);
        } else {
            // 场景2：隐藏状态栏文字（两种方式，按需选择）
            // 方式1：视觉隐藏（兼容所有6.0+版本）
            hideStatusBarTextByColor(window, activity);
            // 方式2：全屏隐藏（仅 Android 11+ 生效）
            // hideStatusBarTextByFullScreen(window);
        }
    }

    /**
     * 重载方法：默认显示深色文字
     */
    public static void toggleStatusBarText(Activity activity, boolean showText) {
        toggleStatusBarText(activity, showText, true);
    }

    /**
     * 显示状态栏文字，并设置文字颜色
     */
    private static void showStatusBarText(Window window, Activity activity, boolean isDarkText) {
        // 显示状态栏（确保系统UI可见）
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // 适配 Android 6.0+ 文字颜色控制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 原生方式设置状态栏文字颜色
            int vis = window.getDecorView().getSystemUiVisibility();
            if (isDarkText) {
                // 设置深色文字（清除浅色文字标记）
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                // 设置浅色文字（移除深色文字标记）
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(vis);
        }

        // 恢复状态栏背景色（白色，避免沉浸式）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.WHITE);
        }
    }

    /**
     * 视觉上隐藏状态栏文字（文字颜色=状态栏背景色）
     */
    private static void hideStatusBarTextByColor(Window window, Activity activity) {
        // 1. 设置状态栏背景色（白色，与文字颜色一致）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.WHITE);
        }

        // 2. 让文字颜色和背景色一致（6.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置深色文字（白色背景+白色文字=视觉隐藏）
            int vis = window.getDecorView().getSystemUiVisibility();
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(vis);
        }

        // 3. 延伸布局到状态栏（沉浸式，增强隐藏效果）
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    /**
     * Android 11+ 系统级隐藏状态栏文字（全屏模式）
     */
    @SuppressLint("WrongConstant")
    private static void hideStatusBarTextByFullScreen(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 延伸布局到状态栏/导航栏
            window.setDecorFitsSystemWindows(false);
            // 获取原生 WindowInsetsController
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                // 隐藏状态栏和导航栏
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                // 设置滑动恢复行为
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE);
            }
        } else {
            // 低版本降级为视觉隐藏
            hideStatusBarTextByColor(window, (Activity) window.getContext());
        }
    }

    /**
     * 恢复状态栏默认显示（文字+背景）
     */
    public static void restoreStatusBarDefault(Activity activity) {
        Window window = activity.getWindow();

        // 恢复布局适配系统栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true);
        }

        // 显示状态栏
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // 恢复默认文字颜色（深色）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int vis = window.getDecorView().getSystemUiVisibility();
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(vis);
        }

        // 恢复默认背景色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.WHITE);
        }

        // 恢复系统栏显示（11+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        }
    }
}