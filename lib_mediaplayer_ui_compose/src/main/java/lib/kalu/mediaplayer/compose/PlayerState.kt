package lib.kalu.mediaplayer.compose

import androidx.compose.runtime.Immutable
import lib.kalu.mediaplayer.bean.type.PlayerType

@Immutable
data class PlayerState(
    val event: Int = 0,
    val prepared: Boolean = false,
    val playing: Boolean = false,
    val buffering: Boolean = false,
    val position: Long = 0,
    val duration: Long = 0,
    val subtitle: String = "",
    val videoWidth: Int = 0,
    val videoHeight: Int = 0,
    val rotation: Int = 0,
    val scaleType: Int = PlayerType.ScaleType.DEFAULT,
    val backgroundColor: Int = android.graphics.Color.BLACK,
    val window: PlayerWindow = PlayerWindow.INLINE,
    val error: Int? = null,
)

enum class PlayerWindow { INLINE, FULL, FLOAT }
