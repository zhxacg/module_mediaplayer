package lib.kalu.mediaplayer.compose

import androidx.compose.foundation.AndroidEmbeddedExternalSurface
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/** Owns and releases the controller with this composition, not each recomposition. */
@Composable
fun rememberPlayerController(): PlayerController {
    val context = LocalContext.current.applicationContext
    val controller = remember(context) { PlayerController(context) }
    DisposableEffect(controller) {
        onDispose { controller.release(false); controller.removeAllPlayerListener() }
    }
    return controller
}

/**
 * Compose video and controls. Call controller.start(args) in LaunchedEffect, never in composition.
 * A controller belongs to one PlayerLayout at a time. The host controls its own Activity orientation.
 */
@Composable
fun PlayerLayout(
    controller: PlayerController,
    modifier: Modifier = Modifier,
    manageLifecycle: Boolean = true,
    controls: @Composable BoxScope.(PlayerController) -> Unit = { PlayerControls(it) },
) {
    val context = LocalContext.current
    val owner = remember { Any() }
    DisposableEffect(controller, context) {
        controller.attachHost(owner, lib.kalu.mediaplayer.util.ContextUtil.getActivitySafely(context))
        onDispose { controller.detachHost(owner) }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(controller, lifecycle, manageLifecycle) {
        if (manageLifecycle) controller.setForeground(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        val observer = LifecycleEventObserver { _, _ ->
            if (manageLifecycle) controller.setForeground(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            if (manageLifecycle) controller.setForeground(false)
        }
    }
    // Leave a stable placeholder when the single video surface moves into a window.
    Box(modifier.background(Color(controller.state.backgroundColor))) {
        if (controller.state.window == PlayerWindow.INLINE) {
            PlayerContent(controller, Modifier.fillMaxSize(), controls)
        }
    }
    when (controller.state.window) {
        PlayerWindow.FULL -> Dialog(
            onDismissRequest = controller::stopFull,
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) { PlayerContent(controller, Modifier.fillMaxSize(), controls) }
        PlayerWindow.FLOAT -> Popup(
            alignment = Alignment.BottomEnd,
            onDismissRequest = controller::stopFloat,
            properties = PopupProperties(focusable = true),
        ) { PlayerContent(controller, Modifier.padding(16.dp).width(320.dp).height(180.dp), controls) }
        PlayerWindow.INLINE -> Unit
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlayerContent(
    controller: PlayerController,
    modifier: Modifier,
    controls: @Composable BoxScope.(PlayerController) -> Unit,
) {
    val state = controller.state
    val view = LocalView.current
    DisposableEffect(view, state.playing) {
        val previous = view.keepScreenOn
        if (state.playing) view.keepScreenOn = true
        onDispose { view.keepScreenOn = previous }
    }
    BoxWithConstraints(modifier.background(Color(state.backgroundColor)).clipToBounds(), contentAlignment = Alignment.Center) {
        val size = videoSize(constraints.maxWidth, constraints.maxHeight,
            state.videoWidth, state.videoHeight, state.scaleType, state.rotation)
        val density = LocalDensity.current
        AndroidEmbeddedExternalSurface(
            modifier = with(density) { Modifier.requiredSize(size.first.toDp(), size.second.toDp()) }
                .graphicsLayer { rotationZ = state.rotation.toFloat() },
            onInit = {
                onSurface { surface, width, height ->
                    controller.attachSurface(surface, width, height)
                    surface.onChanged { w, h -> controller.resizeSurface(surface, w, h) }
                    surface.onDestroyed { controller.detachSurface(surface) }
                }
            },
        )
        if (state.subtitle.isNotEmpty()) {
            Text(state.subtitle, color = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp)
                    .background(Color.Black.copy(alpha = .6f)).padding(8.dp))
        }
        controls(controller)
    }
}

/** Replace through PlayerLayout.controls to build application-specific Compose components. */
@Composable
fun BoxScope.PlayerControls(controller: PlayerController) {
    val state = controller.state
    var dragging by remember { mutableStateOf(false) }
    var target by remember { mutableFloatStateOf(0f) }
    if (state.buffering || (state.event != 0 && !state.prepared && state.error == null &&
                state.event != lib.kalu.mediaplayer.bean.type.PlayerType.EventType.STOP &&
                state.event != lib.kalu.mediaplayer.bean.type.PlayerType.EventType.RELEASE)) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
    state.error?.let { code ->
        Text("播放失败 ($code)", color = Color.White, modifier = Modifier.align(Alignment.Center))
    }
    Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color.Black.copy(alpha = .6f))
        .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = controller::toggle) { Text(if (state.playing) "暂停" else "播放", color = Color.White) }
        if (!controller.isLiveStream()) {
            Slider(value = if (dragging) target else if (state.duration > 0) state.position.toFloat() / state.duration else 0f,
                onValueChange = { dragging = true; target = it },
                onValueChangeFinished = { controller.seekTo((target * state.duration).toLong()); dragging = false },
                enabled = state.prepared && state.duration > 0,
                modifier = Modifier.weight(1f))
        } else { Text("直播", color = Color.White, modifier = Modifier.weight(1f)) }
        TextButton(onClick = { if (controller.isFull()) controller.stopFull() else controller.startFull() }) {
            Text(if (controller.isFull()) "退出全屏" else "全屏", color = Color.White)
        }
    }
}
