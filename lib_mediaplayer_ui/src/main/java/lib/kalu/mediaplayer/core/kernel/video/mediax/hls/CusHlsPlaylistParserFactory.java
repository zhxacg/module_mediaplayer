package lib.kalu.mediaplayer.core.kernel.video.mediax.hls;

import androidx.annotation.Nullable;
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParserFactory;
import androidx.media3.exoplayer.upstream.ParsingLoadable;

import lib.kalu.mediaplayer.proxy.ProxyUrl;

public final class CusHlsPlaylistParserFactory implements HlsPlaylistParserFactory {

    private final ProxyUrl proxyUrl;

    public CusHlsPlaylistParserFactory(ProxyUrl p) {
        proxyUrl = p;
    }

    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser() {
        return new CusHlsPlaylistParser(proxyUrl);
    }

    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser(HlsMultivariantPlaylist hlsMultivariantPlaylist, @Nullable HlsMediaPlaylist hlsMediaPlaylist) {
        return new CusHlsPlaylistParser(proxyUrl);
    }
}
