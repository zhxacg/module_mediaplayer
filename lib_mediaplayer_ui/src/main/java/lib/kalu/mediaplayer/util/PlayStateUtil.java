package lib.kalu.mediaplayer.util;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayStateUtil {

    /**
     * case PlayerType.EventType.ERROR_NETWORK:
     * case PlayerType.EventType.ERROR_URL_EMPTY:
     * case PlayerType.EventType.ERROR_STREAM_SOURCE:
     * case PlayerType.EventType.ERROR_PLAY:
     * case PlayerType.EventType.ERROR_TIMEOUT_LOAD:
     * case PlayerType.EventType.ERROR_TIMEOUT_BUFFER:
     * case PlayerType.EventType.ERROR_INIT:
     * case PlayerType.EventType.ERROR_DECODE:
     *
     * @return
     */
    public static final boolean isError(@PlayerType.EventType.Value int playState) {

        if (playState == PlayerType.EventType.ERROR_NETWORK) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_URL_EMPTY) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_STREAM_SOURCE) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_PLAY) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_TIMEOUT_LOAD) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_TIMEOUT_BUFFER) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_INIT) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_DECODE) {
            return true;
        } else {
            return false;
        }
    }

    public static final boolean isErrorNeedRetry(@PlayerType.EventType.Value int playState) {

        if (playState == PlayerType.EventType.ERROR_URL_EMPTY) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_STREAM_SOURCE) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_PLAY) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_TIMEOUT_LOAD) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_TIMEOUT_BUFFER) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_INIT) {
            return true;
        } else if (playState == PlayerType.EventType.ERROR_DECODE) {
            return true;
        } else {
            return false;
        }
    }
}
