package lib.kalu.mediaplayer.listener;


public interface OnPlayerProgressListener {

    default void onProgress(long trySeeDuration, long position, long duration) {
    }
}
