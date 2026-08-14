package lib.kalu.mediaplayer.core.kernel.video;

import java.util.List;

import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.collect.HlsSpanList;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoKernelApiHls {
    default HlsSpanList getSegments() {
        return null;
    }

    default long[] getSegmentsMs() {
        try {
            HlsSpanList segments = getSegments();
            if (null == segments) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiHls -> getSegmentsMs -> error: segments null");
                }
                return null;
            }
            if (segments.isEmpty()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoKernelApiHls -> getSegmentsMs -> error: segments isEmpty");
                }
                return null;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHls -> getSegmentsMs -> segments.size = " + segments.size());
            }
            int index = -1;
            int size = segments.size();
            long[] longs = new long[size * 2];
            for (int i = 0; i < size; i++) {
                HlsSpanInfo hlsSpanInfo = segments.get(i);
                if (null == hlsSpanInfo)
                    continue;
                long startTimeMs = hlsSpanInfo.getStartTimeMs();
                long endTimeMs = hlsSpanInfo.getEndTimeMs();
                if (LogUtil.DEBUG) {
                    LogUtil.log("ComponentSeek2 -> onUpdateProgress -> i = " + i + ", startTimeMs = " + startTimeMs + ", endTimeMs = " + endTimeMs);
                }
                longs[++index] = startTimeMs;
                longs[++index] = endTimeMs;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHls -> getSegmentsMs -> longs.length = " + longs.length);
            }
            return longs;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoKernelApiHls -> getSegmentsMs -> Exception: " + e.getMessage());
            }
            return null;
        }
    }
}