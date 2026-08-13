package lib.kalu.mediaplayer.core.kernel.video.mediax.hls.adaptive;

import androidx.media3.common.Timeline;
import androidx.media3.common.util.NullableType;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.exoplayer.upstream.BandwidthMeter;

@UnstableApi
public class InitialAdaptiveThenFixedTrackSelectionFactory
        implements ExoTrackSelection.Factory {

    private final BandwidthMeter bandwidthMeter;

    public InitialAdaptiveThenFixedTrackSelectionFactory(
            BandwidthMeter bandwidthMeter) {
        this.bandwidthMeter = bandwidthMeter;
    }

    @Override
    public @NullableType ExoTrackSelection[] createTrackSelections(
            ExoTrackSelection.@NullableType Definition[] definitions,
            BandwidthMeter bandwidthMeter,
            MediaSource.MediaPeriodId mediaPeriodId,
            Timeline timeline) {

        ExoTrackSelection[] selections =
                new ExoTrackSelection[definitions.length];

        for (int i = 0; i < definitions.length; i++) {

            ExoTrackSelection.@NullableType Definition definition =
                    definitions[i];

            if (definition == null) {
                selections[i] = null;
                continue;
            }

            selections[i] =
                    new InitialAdaptiveThenFixedTrackSelection(
                            definition.group,
                            definition.tracks,
                            bandwidthMeter
                    );
        }

        return selections;
    }
}