package lib.kalu.mediaplayer.core.kernel.video.mediax.hls.adaptive;

import androidx.media3.common.TrackGroup;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.source.chunk.MediaChunk;
import androidx.media3.exoplayer.source.chunk.MediaChunkIterator;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.upstream.BandwidthMeter;

import java.util.List;

/**
 * 第一次允许 AdaptiveTrackSelection 根据网络选择码率。
 * <p>
 * 第一次选择完成以后，永久保持当前 Track。
 * <p>
 * 例如：
 * <p>
 * 1080p
 * 720p  <- 第一次 Adaptive 选择
 * 480p
 * <p>
 * 后续：
 * <p>
 * 网络变差 -> 仍然 720p
 * 网络变好 -> 仍然 720p
 */
@UnstableApi
public class InitialAdaptiveThenFixedTrackSelection
        extends AdaptiveTrackSelection {

    /**
     * 是否已经完成第一次自适应选择。
     */
    private boolean selectionLocked = false;

    public InitialAdaptiveThenFixedTrackSelection(
            TrackGroup group,
            int[] tracks,
            BandwidthMeter bandwidthMeter) {

        super(
                group,
                tracks,
                bandwidthMeter
        );
    }

    @Override
    public void updateSelectedTrack(
            long playbackPositionUs,
            long bufferedDurationUs,
            long availableDurationUs,
            List<? extends MediaChunk> queue,
            MediaChunkIterator[] mediaChunkIterators) {

        /*
         * 第一次：
         *
         * 完全交给 Media3 AdaptiveTrackSelection。
         *
         * 它会根据：
         *
         * - 当前带宽
         * - Buffer
         * - 各 Track bitrate
         *
         * 选择最合适的 Track。
         */
        if (!selectionLocked) {

            super.updateSelectedTrack(
                    playbackPositionUs,
                    bufferedDurationUs,
                    availableDurationUs,
                    queue,
                    mediaChunkIterators
            );

            /*
             * 第一次 Adaptive 完成以后立即锁定。
             */
            selectionLocked = true;

            return;
        }

        /*
         * 第二次开始：
         *
         * 什么都不做。
         *
         * selectedIndex 保持第一次 Adaptive
         * 选择出来的值。
         */
    }

    /**
     * 当前是否已经锁定。
     */
    public boolean isSelectionLocked() {
        return selectionLocked;
    }
}