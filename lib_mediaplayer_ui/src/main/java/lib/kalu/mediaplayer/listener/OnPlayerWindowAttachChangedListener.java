package lib.kalu.mediaplayer.listener;

public interface OnPlayerWindowAttachChangedListener {

    default void onDetachedFromWindow() {
    }

    default void onAttachedToWindow() {
    }
}
