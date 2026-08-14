package lib.kalu.mediaplayer.listener;

/**
 * 卡顿监测
 */
public interface OnPlayerStuckListener {

    void onStuck(int position, int count);
}
