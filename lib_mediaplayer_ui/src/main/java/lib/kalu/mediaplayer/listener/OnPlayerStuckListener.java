package lib.kalu.mediaplayer.listener;

import lib.kalu.mediaplayer.bean.type.PlayerType;

/**
 * 卡顿监测
 */
public interface OnPlayerStuckListener {

    void onStuckPlay(@PlayerType.KernelType.Value int kernel, int position, int count);

    void onStuckNet(@PlayerType.KernelType.Value int kernel, long videoBitrate, long netBitrate);
}
