package lib.kalu.mediaplayer.collect;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;

public final class HlsSpanList extends LinkedList<HlsSpanInfo> {
    // 按startTimeMs升序排列（若需按endTimeMs，替换为getEndTimeMs即可）
    private final Comparator<HlsSpanInfo> comparator = (o1, o2) -> {
        long startTime1 = o1.getStartTimeMs();
        long startTime2 = o2.getStartTimeMs();
        // 升序：o1 < o2返回-1，o1 > o2返回1，相等返回0
        return Long.compare(startTime1, startTime2);
    };

    public HlsSpanList() {
    }

    @Override
    public boolean add(HlsSpanInfo e) {
        // 二分查找插入位置
        int index = Collections.binarySearch(this, e, comparator);
        if (index < 0) {
            index = -index - 1;
        }
        super.add(index, e);
        return true;
    }

    @Override
    public HlsSpanInfo get(int index) {
        try {
            return super.get(index);
        }catch (Exception e){
            return null;
        }
    }
}