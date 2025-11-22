package lib.kalu.mediaplayer.core.kernel.video.mediax.hls;

import androidx.annotation.Nullable;
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParserFactory;
import androidx.media3.exoplayer.upstream.ParsingLoadable;

public final class CustomHlsPlaylistParserFactory implements HlsPlaylistParserFactory {
    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser() {
        return new CustomHlsPlaylistParser();
    }

    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser(HlsMultivariantPlaylist hlsMultivariantPlaylist, @Nullable HlsMediaPlaylist hlsMediaPlaylist) {
        return new CustomHlsPlaylistParser();
    }
}
