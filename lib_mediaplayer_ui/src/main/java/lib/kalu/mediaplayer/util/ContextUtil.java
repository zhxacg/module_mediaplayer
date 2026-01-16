package lib.kalu.mediaplayer.util;

import android.app.Activity;
import android.content.Context;

import android.content.ContextWrapper;

public class ContextUtil {

    public static Activity getActivityFromContext(Context context) {
        // 1. 空值校验，避免空指针
        if (context == null) {
            return null;
        }

        // 2. 如果直接是 Activity 类型，直接强转返回
        if (context instanceof Activity) {
            return (Activity) context;
        }

        // 3. 如果是 ContextWrapper 包装类，循环向上查找 baseContext
        ContextWrapper wrapper = (context instanceof ContextWrapper) ? (ContextWrapper) context : null;
        while (wrapper != null) {
            Context baseContext = wrapper.getBaseContext();
            // 找到 Activity 实例则返回
            if (baseContext instanceof Activity) {
                return (Activity) baseContext;
            }
            // 继续向上包装，直到找不到 ContextWrapper
            wrapper = (baseContext instanceof ContextWrapper) ? (ContextWrapper) baseContext : null;
        }

        // 4. 非 Activity 类型的 Context（如 Application/Service），返回 null
        return null;
    }

    public static Activity getActivitySafely(Context context) {
        try {
            return getActivityFromContext(context);
        } catch (Exception e) {
            // 捕获类型转换、空指针等异常，避免崩溃
            return null;
        }
    }
}