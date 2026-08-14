package lib.kalu.mediaplayer.core.kernel.video.mediax.hls;


import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy;

import java.io.IOException;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public final class CusHlsLoadErrorHandlingPolicy extends DefaultLoadErrorHandlingPolicy {

    private String TAG = "CustomHlsLoadErrorHandlingPolicy22";

    private CusHlsLoadErrorHandlingPolicy() {
        super();
    }

    /**
     * 自定义重试次数（比如传1）
     *
     * @param retryCount
     */
    public CusHlsLoadErrorHandlingPolicy(int retryCount) {
        super(retryCount);
    }

    @Nullable
    @Override
    public FallbackSelection getFallbackSelectionFor(FallbackOptions fallbackOptions, LoadErrorInfo loadErrorInfo) {
        FallbackSelection fallbackSelectionFor = super.getFallbackSelectionFor(fallbackOptions, loadErrorInfo);
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "getFallbackSelectionFor -> fallbackSelectionFor = " + fallbackSelectionFor);
        }

        return fallbackSelectionFor;
    }

    // 决定是否重试 + 重试延迟
    @Override
    public long getRetryDelayMsFor(LoadErrorInfo loadErrorInfo) {

        try {

            String dataUrl = loadErrorInfo.loadEventInfo.dataSpec.uri.toString();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getRetryDelayMsFor -> loadErrorInfo.errorCount = " + loadErrorInfo.errorCount + ", loadErrorInfo.loadEventInfo.dataSpec.uri = " + dataUrl);
            }

            if (!dataUrl.contains(PlayerType.SchemeType._M3U8)) {
                return super.getRetryDelayMsFor(loadErrorInfo);
            }
            return C.TIME_UNSET;
        } catch (Exception e) {
            return super.getRetryDelayMsFor(loadErrorInfo);
        }
    }


    /**
     * 返回最终的最小重试次数
     *
     * @param i 7 是「渐进式直播」类型，返回6次；其他（HLS直播）返回3次
     * @return
     */
    @Override
    public int getMinimumLoadableRetryCount(int i) {
        int minimumLoadableRetryCount = super.getMinimumLoadableRetryCount(i);
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "getMinimumLoadableRetryCount -> i = " + i + ", minimumLoadableRetryCount = " + minimumLoadableRetryCount);
        }

        return minimumLoadableRetryCount;
    }

    // 判断是否触发降级播放 Dash
    @Override
    protected boolean isEligibleForFallback(IOException e) {
        boolean eligibleForFallback = super.isEligibleForFallback(e);
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "isEligibleForFallback -> eligibleForFallback = " + eligibleForFallback);
        }

        return eligibleForFallback;
    }
}
