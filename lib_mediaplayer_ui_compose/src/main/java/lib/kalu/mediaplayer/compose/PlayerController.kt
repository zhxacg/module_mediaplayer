package lib.kalu.mediaplayer.compose

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import lib.kalu.mediaplayer.bean.args.StartArgs
import lib.kalu.mediaplayer.bean.args.UrlArgs
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo
import lib.kalu.mediaplayer.bean.info.TrackInfo
import lib.kalu.mediaplayer.bean.type.PlayerType
import lib.kalu.mediaplayer.bean.type.PlayerType.EventType as Event
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager
import lib.kalu.mediaplayer.listener.*
import lib.kalu.mediaplayer.util.PlayStateUtil

/** Playback API for Compose. Construct and call on the main thread. */
class PlayerController(context: Context) {
    private val context = context.applicationContext
    private var activity = WeakReference<Activity>(null)
    private val handler = Handler(Looper.getMainLooper())
    private val host = KernelHost(this.context, this)
    private val metadata = RenderMetadata()
    private var args: StartArgs? = null
    private var kernel: VideoKernelApi? = null
    private var generation = 0L
    private var surface: Surface? = null
    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private var pendingStart = false
    private var foreground = true
    private var userPaused = false
    private var startedAt = 0L
    private var bufferingAt = 0L
    private var eventListener: OnPlayerEventListener? = null
    private var progressListener: OnPlayerProgressListener? = null
    private var bandwidthListener: OnPlayerBandwidthListener? = null
    private var stuckListener: OnPlayerStuckListener? = null
    private var windowListener: OnPlayerWindowStateChangeListener? = null
    private var playbackListener: OnPlayerPlaybackChangedListener? = null
    private var episodeListener: OnPlayerEpisodeListener? = null
    private var visibilityListener: OnPlayerVisibilityChangedListener? = null
    private var windowVisibilityListener: OnPlayerWindowVisibilityChangedListener? = null
    private var attachListener: OnPlayerWindowAttachChangedListener? = null
    private var orientationListener: OnPlayerScreenOrientationChangeListener? = null
    private var owner: Any? = null

    var state by mutableStateOf(PlayerState())
        private set

    init { mainThread(); host.setVideoRender(metadata) }

    private fun mainThread() = check(Looper.myLooper() == Looper.getMainLooper()) {
        "PlayerController must be used on the main thread"
    }

    fun start(args: StartArgs) {
        mainThread()
        disposeKernel()
        this.args = args
        userPaused = !args.isPlayWhenReady
        state = PlayerState(window = state.window, backgroundColor = state.backgroundColor,
            rotation = args.rotation, scaleType = args.getscaleType())
        val token = generation
        emit(Event.INIT)
        if (generation != token) return
        if (!args.containsVideoUrl()) { emit(Event.ERROR_URL_EMPTY); return }
        pendingStart = true
        // Do not lose the initial video output when start() precedes composition/Surface creation.
        startWhenReady()
    }

    private fun startWhenReady() {
        if (!pendingStart || surface?.isValid != true || !foreground) return
        pendingStart = false
        val request = args ?: return
        val token = generation
        try {
            val player = VideoKernelFactoryManager.getKernel(request.kernelType)
            kernel = player
            host.setVideoKernel(player)
            metadata.setVideoKernel(player)
            player.setPlayerApi(host)
            player.setKernelApi(object : VideoKernelApiEvent {
                private fun dispatch(block: () -> Unit) {
                    handler.post { if (generation == token && kernel === player) block() }
                }
                override fun onEvent(kernel: Int, event: Int) = dispatch { onKernelEvent(event) }
                override fun onUpdateSubtitle(kernel: Int, result: CharSequence?) = dispatch {
                    state = state.copy(subtitle = result?.toString().orEmpty())
                }
                override fun onUpdateBandwidth(kernel: Int, total: Long, estimate: Long, average: Long) = dispatch {
                    bandwidthListener?.onBandwidth(request.kernelType, total, estimate, average)
                }
                override fun onUpdateStuckNet(kernel: Int, video: Long, network: Long) = dispatch {
                    stuckListener?.onStuckNet(request.kernelType, video, network)
                }
                override fun onVideoFormatChanged(kernel: Int, rotation: Int, scale: Int, width: Int, height: Int, bitrate: Int) = dispatch {
                    metadata.updateVideoWidth(width); metadata.updateVideoHeight(height)
                    metadata.updateVideoBitrate(bitrate)
                    state = state.copy(videoWidth = width, videoHeight = height)
                }
            })
            // The caller's StartArgs remains unchanged; UI preparation is always asynchronous.
            val decoderArgs = request.newBuilderSelf().setPrepareAsync(true).build()
            player.setStartArgs(decoderArgs)
            player.checkDecoder(context, decoderArgs)
            player.setSurface(surface, surfaceWidth, surfaceHeight)
            val delay = request.playWhenReadyDelayedTime.coerceAtLeast(0L)
            if (delay > 0) emit(Event.MEDIA_INFO_PLAY_WHEN_READY_DELAYED_TIME_START)
            handler.postDelayed({
                if (generation == token && kernel === player) {
                    startedAt = SystemClock.elapsedRealtime()
                    try {
                        player.initOptions(context, decoderArgs)
                        player.startDecoder(context, decoderArgs)
                        handler.post(poll)
                    } catch (error: Exception) { fail(error) }
                    catch (error: LinkageError) { fail(error) }
                }
            }, delay)
        } catch (e: Exception) {
            fail(e)
        } catch (e: LinkageError) {
            fail(e)
        }
    }

    private fun fail(error: Throwable) {
        Log.e("ComposePlayer", "Unable to initialize selected decoder", error)
        disposeKernel()
        emit(Event.ERROR_INIT)
    }

    private fun onKernelEvent(event: Int) {
        if (PlayStateUtil.isErrorNeedRetry(event)) {
            val type = args?.kernelType ?: return
            val token = generation
            val retry = host.formatRetryOtherUrl(type, event) ?: host.formatRetrySelfUrl(type, event)
            if (generation != token) return
            if (retry != null) { start(retry); return }
        }
        when (event) {
            Event.MEDIA_INFO_PREPARE -> state = state.copy(prepared = true)
            Event.MEDIA_INFO_BUFFERING_START -> bufferingAt = SystemClock.elapsedRealtime()
            Event.MEDIA_INFO_BUFFERING_STOP -> bufferingAt = 0
            Event.STOP, Event.END -> handler.removeCallbacks(poll)
        }
        if ((event == Event.START || event == Event.MEDIA_INFO_PREPARE) && (!foreground || userPaused)) {
            kernel?.pause()
        }
        val token = generation
        emit(event)
        if (PlayStateUtil.isError(event)) handler.removeCallbacks(poll)
        if (generation != token) return
        if (event == Event.END && args?.isLooping == true) restart()
    }

    private val poll = object : Runnable {
        override fun run() {
            val player = kernel ?: return
            val token = generation
            val position = player.position.coerceAtLeast(0)
            val duration = player.duration.coerceAtLeast(0)
            state = state.copy(position = position, duration = duration, playing = player.isPlaying)
            progressListener?.onProgress(getTrySeeDuration(), position, duration)
            if (token != generation) return
            val trial = getTrySeeDuration()
            if (trial > 0 && position >= trial) {
                emit(Event.TRY_SEE_END)
                if (token == generation) stop()
                return
            }
            val timeout = args?.timeoutConfiguration?.connectTimeoutMs?.toLong() ?: 0
            val now = SystemClock.elapsedRealtime()
            if (timeout > 0 && !state.prepared && now - startedAt >= timeout) {
                stop(false); emit(Event.ERROR_TIMEOUT_LOAD); return
            }
            if (timeout > 0 && bufferingAt > 0 && now - bufferingAt >= timeout) {
                stop(false); emit(Event.ERROR_TIMEOUT_BUFFER); return
            }
            if (token == generation && state.error == null && state.event != Event.END) handler.postDelayed(this, 500)
        }
    }

    private fun emit(event: Int) {
        val error = PlayStateUtil.isError(event)
        state = state.copy(event = event,
            playing = if (error || event in listOf(Event.PAUSE, Event.STOP, Event.RELEASE, Event.END)) false else kernel?.isPlaying ?: false,
            buffering = when (event) { Event.MEDIA_INFO_BUFFERING_START -> true; Event.MEDIA_INFO_BUFFERING_STOP, Event.STOP, Event.RELEASE, Event.END -> false; else -> state.buffering && !error },
            error = if (error) event else state.error)
        val listener = eventListener ?: return
        val token = generation
        listener.onEvent(event)
        if (generation != token || eventListener !== listener) return
        when {
            error -> listener.onError(event)
            event == Event.START -> listener.onStart()
            event == Event.END -> listener.onComplete()
            event == Event.PAUSE -> listener.onPause()
            event == Event.RESUME -> listener.onResume()
            event == Event.MEDIA_INFO_PREPARE -> listener.onPrepare()
            event == Event.MEDIA_INFO_BUFFERING_START -> listener.onBufferingStart()
            event == Event.MEDIA_INFO_BUFFERING_STOP -> listener.onBufferingStop()
        }
    }

    private fun disposeKernel() {
        generation++
        pendingStart = false
        bufferingAt = 0
        handler.removeCallbacksAndMessages(null)
        val old = kernel
        kernel = null
        host.setVideoKernel(null)
        metadata.setVideoKernel(null)
        old?.setKernelApi(null)
        old?.removeAllMessages()
        try { old?.setSurface(null, 0, 0) }
        finally {
            try { old?.releaseDecoder() }
            finally { old?.setPlayerApi(null) }
        }
    }

    internal fun attachSurface(value: Surface, width: Int, height: Int) {
        mainThread()
        surface = value; surfaceWidth = width; surfaceHeight = height
        kernel?.setSurface(value, width, height)
        startWhenReady()
    }
    internal fun resizeSurface(value: Surface, width: Int, height: Int) {
        if (surface === value) attachSurface(value, width, height)
    }
    internal fun detachSurface(value: Surface) {
        mainThread()
        if (surface !== value) return // A stale outgoing fullscreen surface must not detach the new one.
        kernel?.setSurface(null, 0, 0)
        surface = null
    }
    internal fun setForeground(value: Boolean) {
        mainThread()
        if (foreground == value) return
        foreground = value
        val visibility = if (value) android.view.View.VISIBLE else android.view.View.GONE
        visibilityListener?.onVisibilityChanged(visibility)
        windowVisibilityListener?.onWindowVisibilityChanged(visibility)
        if (!value) { kernel?.pause(); state = state.copy(playing = false) }
        else { startWhenReady(); if (!userPaused && state.prepared) resume(false) }
    }

    internal fun attachHost(token: Any, value: Activity?) {
        mainThread()
        check(owner == null || owner === token) { "A controller can only belong to one PlayerLayout" }
        owner = token
        activity = WeakReference(value)
        attachListener?.onAttachedToWindow()
    }
    internal fun detachHost(token: Any) {
        if (owner !== token) return
        owner = null
        activity.clear()
        attachListener?.onDetachedFromWindow()
    }

    fun getStartArgs(): StartArgs? = args
    fun updateStartArgs(value: StartArgs) { mainThread(); args = value; kernel?.setStartArgs(value) }
    fun newBuilderSelf(): StartArgs.Builder = args?.newBuilderSelf() ?: StartArgs.newBuilder()
    fun getUrl(): String? = args?.url
    fun getUrlArgs(): UrlArgs? = args?.urlArgs
    fun getPosition(): Long = kernel?.position ?: 0
    fun getDuration(): Long = kernel?.duration ?: 0
    fun getTrySeeDuration(): Long = args?.trySeeDuration ?: 0
    fun getPlayWhenReadySeekToPosition(): Long = args?.playWhenReadySeekToPosition ?: 0
    fun isPlaying(): Boolean = kernel?.isPlaying == true
    fun isPrepared(): Boolean = state.prepared
    fun isLiveStream(): Boolean = args?.isLiveStream == true
    fun toggle() { if (isPlaying()) pause() else resume() }
    @JvmOverloads fun pause(callEvent: Boolean = true) {
        mainThread(); userPaused = true; kernel?.pause(); state = state.copy(playing = false)
        if (callEvent) emit(Event.PAUSE)
    }
    @JvmOverloads fun resume(callEvent: Boolean = true) {
        mainThread(); userPaused = false
        if (foreground) kernel?.resume()
        state = state.copy(playing = isPlaying())
        if (callEvent) emit(Event.RESUME)
    }
    @JvmOverloads fun stop(callEvent: Boolean = true) {
        mainThread(); disposeKernel(); state = state.copy(playing = false, prepared = false, subtitle = "", buffering = false)
        if (callEvent) emit(Event.STOP)
    }
    @JvmOverloads fun release(callEvent: Boolean = true) {
        mainThread(); stop(false)
        if (callEvent) emit(Event.RELEASE)
    }
    fun restart() { args?.newBuilderSelf()?.setPlayWhenReadySeekToPosition(0)?.build()?.let(::start) }
    fun restartSeekToPosition() { args?.newBuilderSelf()?.setPlayWhenReadySeekToPosition(if (isLiveStream()) 0 else getPosition())?.build()?.let(::start) }
    fun seekTo(position: Long) { mainThread(); kernel?.seekTo(position.coerceAtLeast(0)) }
    fun seekToDefaultPosition() { mainThread(); kernel?.seekToDefaultPosition() }
    fun setVolume(left: Float, right: Float) { mainThread(); kernel?.setVolume(left.coerceIn(0f, 1f), right.coerceIn(0f, 1f)) }
    fun closeVolume() = setVolume(0f, 0f)
    fun openVolume() = setVolume(1f, 1f)
    fun setSpeed(speed: Float) { mainThread(); require(speed.isFinite() && speed > 0); kernel?.setSpeed(speed); playbackListener?.onSpeed(speed) }
    fun getSpeed(): Float = kernel?.speed ?: 1f
    fun getTrackInfo(): List<TrackInfo> = getTrackInfoAll()
    fun getTrackInfo(type: Int): List<TrackInfo> = kernel?.getTrackInfo(type) ?: emptyList()
    fun getTrackInfoAll(): List<TrackInfo> = getTrackInfo(-1)
    fun getTrackInfoVideo(): List<TrackInfo> = getTrackInfo(1)
    fun getTrackInfoAudio(): List<TrackInfo> = getTrackInfo(2)
    fun getTrackInfoSubtitle(): List<TrackInfo> = getTrackInfo(3)
    fun toggleTrack(trackInfo: TrackInfo): Boolean { mainThread(); return kernel?.toggleTrack(trackInfo) == true }
    fun getSegments(): List<HlsSpanInfo> = kernel?.segments ?: emptyList()
    fun setPlaybackSubtitleOffsetMs(offset: Int): Boolean { mainThread(); return kernel?.subtitleOffsetMs(offset) == true }
    fun addSubtitleTrack(url: String): Boolean { mainThread(); return kernel?.addSubtitleTrack(url) == true }
    fun setVideoScaleType(type: Int) { mainThread(); state = state.copy(scaleType = type) }
    fun setVideoRotation(rotation: Int) { mainThread(); state = state.copy(rotation = rotation) }
    fun setPlayerBackgroundColor(color: Int) { mainThread(); state = state.copy(backgroundColor = color) }
    /** Extracts the source frame at the current position. Synchronous, as in the legacy API. */
    fun screenshot(): String? {
        val source = getUrl() ?: return null
        val retriever = MediaMetadataRetriever()
        return try {
            if (source.startsWith("http://") || source.startsWith("https://")) {
                retriever.setDataSource(source, args?.urlArgs?.streamHead ?: emptyMap())
            } else retriever.setDataSource(context, Uri.parse(source))
            val bitmap = retriever.getFrameAtTime(getPosition().coerceAtLeast(0) * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST) ?: return null
            try {
                val directory = File(context.cacheDir, "compose-player-screenshots").apply { mkdirs() }
                val file = File.createTempFile("frame-", ".jpg", directory)
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                file.absolutePath
            } finally { bitmap.recycle() }
        } catch (error: Exception) {
            Log.w("ComposePlayer", "Frame extraction unavailable for this source", error)
            null
        } finally { retriever.release() }
    }
    @JvmOverloads fun requestScreenOrientation(value: Int, formatScreen: Boolean = false): Boolean {
        mainThread()
        if (value != PlayerType.ScreenOrientation.PORTRAIT && value != PlayerType.ScreenOrientation.LANDSPACE) return false
        val hostActivity = activity.get() ?: return false
        return host.requestScreenOrientation(hostActivity, value, formatScreen).also { success ->
            if (success) {
                if (value == PlayerType.ScreenOrientation.PORTRAIT) orientationListener?.onPortrait()
                else orientationListener?.onLandspace()
            }
        }
    }
    fun sendSelfEvent(event: Int) { mainThread(); emit(event) }
    fun isFull(): Boolean = state.window == PlayerWindow.FULL
    fun isFloat(): Boolean = state.window == PlayerWindow.FLOAT
    fun startFull() = window(PlayerWindow.FULL)
    fun stopFull() { if (isFull()) window(PlayerWindow.INLINE) }
    fun startFloat() = window(PlayerWindow.FLOAT)
    fun stopFloat() { if (isFloat()) window(PlayerWindow.INLINE) }
    private fun window(value: PlayerWindow) {
        mainThread(); if (state.window == value) return
        state = state.copy(window = value)
        windowListener?.onState(when(value) { PlayerWindow.INLINE -> PlayerType.WindowType.DEFAULT; PlayerWindow.FULL -> PlayerType.WindowType.FULL; PlayerWindow.FLOAT -> PlayerType.WindowType.FLOAT })
    }
    fun setOnPlayerEventListener(listener: OnPlayerEventListener?) { mainThread(); eventListener = listener }
    fun setOnPlayerProgressListener(listener: OnPlayerProgressListener?) { mainThread(); progressListener = listener }
    fun setOnPlayerBandwidthListener(listener: OnPlayerBandwidthListener?) { mainThread(); bandwidthListener = listener }
    fun setOnPlayerStuckListener(listener: OnPlayerStuckListener?) { mainThread(); stuckListener = listener }
    fun setOnPlayerWindowStateChangeListener(listener: OnPlayerWindowStateChangeListener?) { mainThread(); windowListener = listener }
    fun setOnPlayerPlaybackChangedListener(listener: OnPlayerPlaybackChangedListener?) { mainThread(); playbackListener = listener }
    fun setOnPlayerEpisodeListener(listener: OnPlayerEpisodeListener?) { mainThread(); episodeListener = listener }
    fun setOnPlayerVisibilityChangedListener(listener: OnPlayerVisibilityChangedListener?) { mainThread(); visibilityListener = listener }
    fun setOnPlayerWindowVisibilityChangedListener(listener: OnPlayerWindowVisibilityChangedListener?) { mainThread(); windowVisibilityListener = listener }
    fun setOnPlayerWindowAttachChangedListener(listener: OnPlayerWindowAttachChangedListener?) { mainThread(); attachListener = listener }
    fun setOnPlayerScreenOrientationChangeListener(listener: OnPlayerScreenOrientationChangeListener?) { mainThread(); orientationListener = listener }
    fun removeAllPlayerListener() {
        mainThread(); eventListener = null; progressListener = null; bandwidthListener = null
        stuckListener = null; windowListener = null; playbackListener = null
        episodeListener = null; visibilityListener = null; windowVisibilityListener = null
        attachListener = null; orientationListener = null
    }
}
