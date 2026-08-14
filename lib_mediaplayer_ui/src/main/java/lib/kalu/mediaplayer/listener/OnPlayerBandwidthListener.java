package lib.kalu.mediaplayer.listener;

/**
 * 网速变化监测
 */
public interface OnPlayerBandwidthListener {

    void onBandwidth(int kernel, long totalLoadTimeMs, long estimateKBs, long realAvgKBs);
}