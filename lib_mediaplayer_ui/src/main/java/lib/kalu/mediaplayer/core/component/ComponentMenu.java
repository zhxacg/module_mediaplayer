package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.menu.Menu;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentMenu extends RelativeLayout implements ComponentApi {

    private int TYPE_EPISODE = 1000;
    private int TYPE_SCALE = 2000;
    private int TYPE_SPEED = 3000;

    public ComponentMenu(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_menu;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_menu_root;
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        if (isFromUser && trySeeDuration == -1 && position == -1 && duration == -1) {
            long millis = System.currentTimeMillis();
            setTag(millis);
        } else if (!isFromUser) {
            try {
                long timeMillis = (long) getTag();
                if (timeMillis <= 0L)
                    throw new Exception();
                long currentTimeMillis = System.currentTimeMillis();
                long cast = currentTimeMillis - timeMillis;
                if (cast <= 4000L)
                    throw new Exception();
                hide();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (LogUtil.DEBUG) {
            LogUtil.log("ComponentMenu -> dispatchKeyEvent -> action =  " + event.getAction() + ", keyCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());
        }

        // keycode_dpad_center
        if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            try {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_item_content_txt)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_content_txt");
                Object _tag = focus.getTag();
                if (null == _tag)
                    throw new Exception("warning: _tag null");
                int _type = ((int[]) _tag)[0];

                // 倍速
                if (_type == TYPE_SPEED) {
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    //
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int childCount = viewGroup.getChildCount();
                    int indexedOfChild = viewGroup.indexOfChild(focus);
                    for (int i = 0; i < childCount; i++) {
                        View childAt = viewGroup.getChildAt(i);
                        childAt.setSelected(i == indexedOfChild);
                        // childAt.setActivated(i == indexedOfChild);
                    }
                    //
                    setVideoSpeed(((int[]) _tag)[1]);
//                    hide();
                }
                // 画面比例
                else if (_type == TYPE_SCALE) {
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    //
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int childCount = viewGroup.getChildCount();
                    int indexedOfChild = viewGroup.indexOfChild(focus);
                    for (int i = 0; i < childCount; i++) {
                        View childAt = viewGroup.getChildAt(i);
                        childAt.setSelected(i == indexedOfChild);
                        // childAt.setActivated(i == indexedOfChild);
                    }
                    //
                    setVideoScaleType(((int[]) _tag)[1]);
//                    hide();
                }
                // 选集
                else if (_type == TYPE_EPISODE) {

                    StartArgs startArgs = getStartArgs();
                    if (null != startArgs) {
                        Menu menu = startArgs.getMenu();
                        List<String> playUrls = menu.getPlayUrls();
                        if (null != playUrls) {
                            int select = ((int[]) _tag)[2];

                            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                            int childCount = viewGroup.getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                View childAt = viewGroup.getChildAt(i);
                                View viewById = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                                Object tag = viewById.getTag();
                                int cur = ((int[]) tag)[2];
                                ((int[]) tag)[1] = select;
                                viewById.setSelected(cur == select);
                                // childAt.setActivated(i == indexedOfChild);
                            }
                            //
                            OnPlayerEpisodeListener listener = getPlayerView().getOnPlayerEpisodeListener();
                            if (null != listener) {
                                listener.onEpisode(select);
                            }
                            //
                            String url = playUrls.get(select);

                            List<? extends Menu.Item> list = menu.getData();
                            for (Menu.Item item : list) {
                                if (item instanceof Menu.Episode) {
                                    ((Menu.Episode) item).setPlayPos(select);
                                }
                            }
                            //
                            StartArgs args = startArgs.newBuilder()
                                    .setUrl(url)
                                    .setMenu(new Menu.Builder()
                                            .setData(list).build()).build();
                            getPlayerView().stop();
                            getPlayerView().release();
                            getPlayerView().start(args);
                            //
                            hide();
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentMenu -> keycodeCenter -> keycode_dpad_center -> " + e.getMessage());
                }
            }

            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                return true;
        }
        // action_down keycode_dpad_down
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {

                View focus = findFocus();

                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentMenu -> dispatchKeyEvent[action_down&keycode_dpad_down] -> focus = " + focus);
                }

                if (null == focus) {
                    addTabMenu(0);
                    selectedTabMenu(0);
                    updateTabContent(0);
                    show();
                    superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_SHOW);
                }
                //
                requestTabMenu();
                //
                onUpdateProgress(true, -1, -1, -1);
                return true;
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentMenu -> dispatchKeyEvent[action_down&keycode_dpad_down] -> Exception " + e.getMessage());
                }
            }
        }
        // action_down -> keycode_dpad_up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            try {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_tab_group_item)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_tab_group_item");
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    View viewById = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                    boolean selected = viewById.isSelected();
                    if (!selected)
                        continue;
                    viewById.requestFocus();
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    return true;
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentMenu -> keycodeUp -> Exception " + e.getMessage());
                }
            }
        }
        // action_down -> keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {

            try {

                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_tab_group_item) {

                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                    int indexOfChild = viewGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {
                        //
                        onUpdateProgress(true, -1, -1, -1);
                        return true;
                    } else {

                        try {
                            //
                            onUpdateProgress(true, -1, -1, -1);
                            //
                            int nextIndex = --indexOfChild;
                            selectedTabMenu(nextIndex);
                            updateTabContent(nextIndex);
                            //
                            return viewGroup.dispatchKeyEvent(event);
                        } catch (Exception e) {

                        }
                    }
                }
                // 内容
                else if (focusId == R.id.module_mediaplayer_component_menu_item_content_txt) {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int indexOfChild = viewGroup.indexOfChild((View) focus.getParent());
                    if (LogUtil.DEBUG) {
                        LogUtil.log("ComponentMenu -> keycodeUp -> indexOfChild = " + indexOfChild);
                    }
                    if (indexOfChild <= 0) {
                        try {
                            Object _tag = focus.getTag();
                            if (!(_tag instanceof int[]))
                                throw new Exception("warning: _tag not instanceof int[]");
                            int type = ((int[]) _tag)[0];
                            if (type != TYPE_EPISODE)
                                throw new Exception("warning: type != TYPE_EPISODE");
                            int _cur = ((int[]) _tag)[2];
                            if (LogUtil.DEBUG) {
                                LogUtil.log("ComponentMenu -> keycodeUp -> _cur = " + _cur);
                            }
                            if (_cur <= 0)
                                throw new Exception("warning: _cur <= 0");
                            for (int i = 0; i < 10; i++) {
                                //
                                View childAt = viewGroup.getChildAt(i);
                                TextView viewById = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                                int[] tag = (int[]) viewById.getTag();
                                int cur = tag[2];
                                int select = tag[1];
                                int newCur = cur - 1;
                                if (LogUtil.DEBUG) {
                                    LogUtil.log("ComponentMenu -> keycodeUp -> i = " + i + ", select = " + select + ", newCur = " + newCur);
                                }
                                tag[2] = newCur;

                                //
                                viewById.setSelected(select == newCur);
                                viewById.setText(String.valueOf(newCur + 1));
                            }
                        } catch (Exception e) {
                        }
                        //
                        onUpdateProgress(true, -1, -1, -1);
                        return true;
                    } else {
                        //
                        onUpdateProgress(true, -1, -1, -1);
                        return viewGroup.dispatchKeyEvent(event);
                    }
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentMenu -> dispatchKeyEvent[action_down&keycode_dpad_left] -> Exception " + e.getMessage());
                }
            }
        }
        // action_down -> keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {

            try {

                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_tab_group_item) {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                    int childCount = viewGroup.getChildCount();
                    int indexOfChild = viewGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= childCount) {

                        //
                        onUpdateProgress(true, -1, -1, -1);

                        return true;
                    } else {

                        try {
                            //
                            onUpdateProgress(true, -1, -1, -1);
                            //
                            int nextIndex = ++indexOfChild;
                            selectedTabMenu(nextIndex);
                            updateTabContent(nextIndex);
                            //
                            return viewGroup.dispatchKeyEvent(event);
                        } catch (Exception e) {
                        }
                    }
                }
                // 内容
                else if (focusId == R.id.module_mediaplayer_component_menu_item_content_txt) {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int childCount = viewGroup.getChildCount();
                    int indexOfChild = viewGroup.indexOfChild((View) focus.getParent());
                    if (indexOfChild + 1 >= childCount) {

                        try {
                            Object _tag = focus.getTag();
                            if (!(_tag instanceof int[]))
                                throw new Exception("warning: _tag not instanceof int[]");
                            int type = ((int[]) _tag)[0];
                            if (type != TYPE_EPISODE)
                                throw new Exception("warning: type != TYPE_EPISODE");
                            int _cur = ((int[]) _tag)[2];
                            if (LogUtil.DEBUG) {
                                LogUtil.log("ComponentMenu -> keycodeUp -> _cur = " + _cur);
                            }
                            int _length = ((int[]) _tag)[3];
                            if (LogUtil.DEBUG) {
                                LogUtil.log("ComponentMenu -> keycodeUp -> _length = " + _length);
                            }
                            if (_cur + 1 >= _length)
                                throw new Exception("warning: _cur + 1 > _length");
                            for (int i = 0; i < 10; i++) {
                                //
                                View childAt = viewGroup.getChildAt(i);
                                TextView viewById = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                                int[] tag = (int[]) viewById.getTag();
                                int cur = tag[2];
                                int select = tag[1];
                                int newCur = cur + 1;
                                if (LogUtil.DEBUG) {
                                    LogUtil.log("ComponentMenu -> keycodeUp -> i = " + i + ", select = " + select + ", newCur = " + newCur);
                                }
                                tag[2] = newCur;

                                //
                                viewById.setSelected(select == newCur);
                                viewById.setText(String.valueOf(newCur + 1));
                            }
                        } catch (Exception e) {

                        }

                        //
                        onUpdateProgress(true, -1, -1, -1);

                        return true;
                    } else {

                        // selectedTabGroup(--indexOfChild);
                        //  requestFocusedTabGroup();

                        //
                        onUpdateProgress(true, -1, -1, -1);

                        return viewGroup.dispatchKeyEvent(event);
                    }
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentMenu -> dispatchKeyEvent[action_down&keycode_dpad_right] -> Exception " + e.getMessage());
                }
            }
        }

        return false;
    }

    private void addTabMenu(int index) throws Exception {

        try {
            if (index < 0)
                throw new Exception("warning: index < 0");

            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("warning: startArgs null");

            Menu argsMenu = startArgs.getMenu();
            if (null == argsMenu)
                throw new Exception("warning: argsMenu null");

            List<? extends Menu.Item> list = praseData();
            if (null == list)
                throw new Exception("error: list null");

            int size = list.size();
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentMenu -> addTabMenu -> size =  " + size);
            }
            if (index >= size)
                throw new Exception("error: index >= size");

            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            if (childCount == 0) {

                // 填充数据
                for (int i = 0; i < size; i++) {
                    LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_tab, viewGroup, true);
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    ((TextView) childAt).setText(list.get(i).getName());
                }
            }

        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentMenu -> addTabMenu -> Exception " + e.getMessage());
            }
            throw e;
        }
    }

    private void requestTabMenu() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("warning: childCount <= 0");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                boolean selected = childAt.isSelected();
                if (!selected)
                    continue;
                childAt.requestFocus();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentMenu -> requestTabMenu -> Exception " + e.getMessage());
            }
        }
    }

    private void selectedTabMenu(int index) throws Exception {

        try {
            if (index < 0)
                throw new Exception("warning: index < 0");

            List<? extends Menu.Item> list = praseData();
            if (null == list)
                throw new Exception("error: list null");

            int size = list.size();
            if (index >= size)
                throw new Exception("error: index >= size");

            // 选中
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                childAt.setSelected(i == index);
            }

        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentMenu -> selectedTabMenu -> Exception " + e.getMessage());
            }
            throw e;
        }
    }

    private void updateTabContent(int index) throws Exception {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
            viewGroup.removeAllViews();
            //
            if (index < 0)
                throw new Exception("error: index <0");
            List<? extends Menu.Item> list = praseData();
            if (null == list)
                throw new Exception("error: list null");
            int size = list.size();
            if (index >= size)
                throw new Exception("error: index >= size");

            Menu.Item item = list.get(index);

            // 选集
            if (item instanceof Menu.Episode) {

                int playPos = ((Menu.Episode) item).getPlayPos();
                int num = playPos / 10;
                int start = num * 10;
                int end = start + 10;
                int playCount = ((Menu.Episode) item).getPlayCount();

                int freeCount = ((Menu.Episode) item).getFreeCount();
                int freeRes = ((Menu.Episode) item).getFreeRes();
                int vipRes = ((Menu.Episode) item).getVipRes();

                if (end > playCount) {
                    int cast = end - playCount;
                    start = start - cast;
                }

                for (int i = 0; i < 10; i++) {
                    //
                    LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_content, viewGroup, true);
                    View childAt = viewGroup.getChildAt(i);
                    //
                    childAt.setVisibility(i >= playCount ? View.INVISIBLE : View.VISIBLE);
                    if (i >= playCount)
                        continue;
                    int cur = i + start;
                    //
                    TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                    textView.setTag(new int[]{TYPE_EPISODE, playPos, cur, playCount});
                    textView.setSelected(cur == playPos);
                    textView.setText(String.valueOf(cur + 1));

                    //
                    ImageView imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_img);
                    if (cur >= freeCount) {
                        imageView.setImageResource(vipRes);
                    } else {
                        imageView.setImageResource(freeRes);
                    }
                }
            }
            // 其他
            else if (item instanceof Menu.Default) {
                int[] contentData = ((Menu.Default) item).getData();
                if (null == contentData)
                    throw new Exception("error: contentData null");
                if (contentData.length == 0)
                    throw new Exception("error: contentData.length == 0");

                int scaleType = getVideoScale();
                int speedType = getVideoSpeed();

                List<Integer> listScale = Arrays.asList(PlayerType.ScaleType._1_1,
                        PlayerType.ScaleType._4_3,
                        PlayerType.ScaleType._5_4,
                        PlayerType.ScaleType._16_10,
                        PlayerType.ScaleType._16_9,
                        PlayerType.ScaleType.REAL,
                        PlayerType.ScaleType.FULL,
                        PlayerType.ScaleType.AUTO);

                List<Integer> listSpeed = Arrays.asList(PlayerType.SpeedType._0_5,
                        PlayerType.SpeedType._1_0,
                        PlayerType.SpeedType._1_5,
                        PlayerType.SpeedType._2_0,
                        PlayerType.SpeedType._2_5,
                        PlayerType.SpeedType._3_0,
                        PlayerType.SpeedType._3_5,
                        PlayerType.SpeedType._4_0,
                        PlayerType.SpeedType._4_5,
                        PlayerType.SpeedType._5_0);

                int length = Math.min(contentData.length, 10);
                for (int i = 0; i < length; i++) {
                    //
                    LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_content, viewGroup, true);
                    View childAt = viewGroup.getChildAt(i);

                    //
                    int value = contentData[i];

                    // 倍速
                    if (listSpeed.contains(value)) {
                        TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                        textView.setTag(new int[]{TYPE_SPEED, value});
                        textView.setSelected(speedType == value);
                        if (value == PlayerType.SpeedType._0_5) {
                            textView.setText("0.5");
                        } else if (value == PlayerType.SpeedType._1_5) {
                            textView.setText("1.5");
                        } else if (value == PlayerType.SpeedType._2_0) {
                            textView.setText("2.0");
                        } else if (value == PlayerType.SpeedType._2_5) {
                            textView.setText("2.5");
                        } else if (value == PlayerType.SpeedType._3_0) {
                            textView.setText("3.0");
                        } else if (value == PlayerType.SpeedType._3_5) {
                            textView.setText("3.5");
                        } else if (value == PlayerType.SpeedType._4_0) {
                            textView.setText("4.0");
                        } else if (value == PlayerType.SpeedType._4_5) {
                            textView.setText("4.5");
                        } else if (value == PlayerType.SpeedType._5_0) {
                            textView.setText("5.0");
                        } else {
                            textView.setText("1.0");
                        }
                    }
                    // 画面比例
                    else if (listScale.contains(value)) {
                        TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_content_txt);
                        textView.setTag(new int[]{TYPE_SCALE, value});
                        textView.setSelected(scaleType == value);
                        if (value == PlayerType.ScaleType.REAL) {
                            textView.setText("原始");
                        } else if (value == PlayerType.ScaleType.FULL) {
                            textView.setText("全屏");
                        } else if (value == PlayerType.ScaleType._1_1) {
                            textView.setText("1:1");
                        } else if (value == PlayerType.ScaleType._4_3) {
                            textView.setText("4:3");
                        } else if (value == PlayerType.ScaleType._5_4) {
                            textView.setText("5:4");
                        } else if (value == PlayerType.ScaleType._16_9) {
                            textView.setText("16:9");
                        } else if (value == PlayerType.ScaleType._16_10) {
                            textView.setText("16:10");
                        } else {
                            textView.setText("自动");
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("ComponentMenu -> updateTabContent -> Exception " + e.getMessage());
            }
            throw e;
        }
    }

    private List<? extends Menu.Item> praseData() {

        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("warning: startArgs null");
            Menu menu = startArgs.getMenu();
            if (null == menu)
                throw new Exception("warning: menu null");
            List<? extends Menu.Item> data = menu.getData();
            if (null == data)
                throw new Exception("warning: data null");
            if (data.isEmpty())
                throw new Exception("warning: data isEmpty");
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
