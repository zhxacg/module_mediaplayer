
package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerScreenOrientationChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerPlaybackChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerVisibilityChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowAttachChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowVisibilityChangedListener;

public interface VideoPlayerApiListener extends VideoPlayerApiBase {

    default void clearPlayerListener() {
        setOnPlayerEventListener(null);
        setOnPlayerProgressListener(null);
        setOnPlayerPlaybackChangedListener(null);
        setOnPlayerWindowStateChangeListener(null);
        setOnPlayerVisibilityChangedListener(null);
        setOnPlayerWindowVisibilityChangedListener(null);
        setOnPlayerScreenOrientationChangeListener(null);
    }

    default OnPlayerWindowStateChangeListener getPlayerWindowStateChangeListener() {
        return null;
    }

    default void setOnPlayerWindowStateChangeListener(OnPlayerWindowStateChangeListener l) {
    }

    default OnPlayerEventListener getPlayerEventListener() {
        return null;
    }

    default void setOnPlayerEventListener(OnPlayerEventListener l) {
    }


    default OnPlayerProgressListener getPlayerProgressListener() {
        return null;
    }

    default void setOnPlayerProgressListener(OnPlayerProgressListener l) {
    }

    default OnPlayerWindowVisibilityChangedListener getPlayerWindowVisibilityChangedListener() {
        return null;
    }

    default void setOnPlayerWindowVisibilityChangedListener(OnPlayerWindowVisibilityChangedListener l) {
    }

    default OnPlayerEpisodeListener getPlayerEpisodeListener() {
        return null;
    }

    default void setOnPlayerEpisodeListener(OnPlayerEpisodeListener l) {
    }

    default OnPlayerVisibilityChangedListener getPlayerVisibilityChangedListener() {
        return null;
    }

    default void setOnPlayerVisibilityChangedListener(OnPlayerVisibilityChangedListener l) {
    }

    default OnPlayerPlaybackChangedListener getOnPlayerPlaybackChangedListener(){
        return null;
    }

    default void setOnPlayerPlaybackChangedListener(OnPlayerPlaybackChangedListener l){
    }

    default OnPlayerScreenOrientationChangeListener getPlayerScreenOrientationChangeListener(){
        return null;
    }

    default void setOnPlayerScreenOrientationChangeListener(OnPlayerScreenOrientationChangeListener l){
    }

    default OnPlayerWindowAttachChangedListener getPlayerWindowAttachChangedListener(){
        return null;
    }

    default void setOnPlayerWindowAttachChangedListener(OnPlayerWindowAttachChangedListener l){
    }
}
