package lib.kalu.mediaplayer.util;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示刷新率工具类（Java版）
 */
public class DisplayRefreshRateUtils {

    /**
     * 获取当前显示刷新率（Hz）
     */
    public static float getCurrentRefreshRate(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getRefreshRate();
    }

    /**
     * 切换显示刷新率（需系统权限，部分盒子需root/厂商适配）
     * @param context 上下文
     * @param targetFps 目标帧率
     * @return 是否切换成功
     */
    public static boolean setRefreshRate(Context context, float targetFps) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        try {
            // 反射调用系统刷新率设置接口（不同厂商可能有差异）
            Method method = Display.class.getMethod("setRefreshRate", float.class);
            method.invoke(display, targetFps);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取设备支持的所有刷新率列表
     */
    public static List<Float> getSupportedRefreshRates(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Display.Mode[] modes = display.getSupportedModes();
        List<Float> rates = new ArrayList<>();
        for (Display.Mode mode : modes) {
            float rate = mode.getRefreshRate();
            if (!rates.contains(rate)) {
                rates.add(rate);
            }
        }
        return rates;
    }
}