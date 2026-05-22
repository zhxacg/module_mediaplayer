package lib.kalu.mediaplayer.core.player.video;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiComponent extends VideoPlayerApiBase {

    default boolean showOnlyComponent(java.lang.Class<?> cls) {
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                if (childAt.getClass() != cls) {
                    ((ComponentApi) childAt).hide();
                }
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("PlayerLayout -> showOnlyComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    default int getComponentCount() {
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            return viewGroup.getChildCount();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> getComponentCount -> " + e.getMessage());
            }
            return 0;
        }
    }

    default Boolean isEmptyComponent() {
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            return childCount <= 0;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> isEmptyComponent -> " + e.getMessage());
            }
            return false;
        }
    }

    default void clearAllComponent() {
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            viewGroup.removeAllViews();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> clearAllComponent -> " + e.getMessage());
            }
        }
    }

    default void clearComponent(java.lang.Class<?> cls) {
        try {
            LinkedList<View> views = new LinkedList<>();
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                if (childAt.getClass() == cls) {
                    views.add(childAt);
                }
            }
            for (View v : views) {
                viewGroup.removeView(v);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> clearComponent -> " + e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    default void addComponent(ComponentApi component) {
        try {
            if (null == component)
                throw new Exception("componentApi error: null");
            ViewGroup viewGroup = getBaseComponentViewGroup();
            if (null == viewGroup)
                throw new Exception("viewGroup error: null");
            viewGroup.addView((View) component, 0, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> addComponent -> " + e.getMessage());
            }
        }
    }

    default void addAllComponent(List<ComponentApi> components) {
        try {
            if (null == components || components.size() <= 0)
                throw new Exception("components error: null");
            ViewGroup viewGroup = getBaseComponentViewGroup();
            if (null == viewGroup)
                throw new Exception("viewGroup error: null");
            viewGroup.removeAllViews();
            for (ComponentApi componentApi : components) {
                if (null == componentApi)
                    continue;
                addComponent(componentApi);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> addAllComponent -> " + e.getMessage());
            }
        }
    }

    default <T extends ComponentApi> T findComponent(java.lang.Class<?> interClass) {
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                Class<? extends View> childClass = childAt.getClass();
                boolean assignableFromComponentApi = ComponentApi.class.isAssignableFrom(childClass);
                if (!assignableFromComponentApi)
                    continue;
                if (childClass == interClass) {
                    return (T) childAt;
                } else {
                    boolean assignableFromInterApi = interClass.isAssignableFrom(childClass);
                    if (assignableFromInterApi) {
                        return (T) childAt;
                    }
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent -> findComponent -> " + e.getMessage());
            }
            return null;
        }
    }
}
