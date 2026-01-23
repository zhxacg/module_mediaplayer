package lib.kalu.mediaplayer.core.kernel.video.exo2.proxy;

import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMultivariantPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.upstream.ParsingLoadable;

import lib.kalu.mediaplayer.proxy.ProxyUrl;

public final class CustomHlsPlaylistParserFactory implements HlsPlaylistParserFactory {

    private final ProxyUrl proxyUrl;

    public CustomHlsPlaylistParserFactory(ProxyUrl p) {
        proxyUrl = p;
    }
    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser() {
        return new CustomHlsPlaylistParser(proxyUrl);
    }

    @Override
    public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser(HlsMultivariantPlaylist multivariantPlaylist, HlsMediaPlaylist previousMediaPlaylist) {
        return new CustomHlsPlaylistParser(proxyUrl);
    }
}
