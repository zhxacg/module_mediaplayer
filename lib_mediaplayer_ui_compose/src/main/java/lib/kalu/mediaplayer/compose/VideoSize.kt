package lib.kalu.mediaplayer.compose

import lib.kalu.mediaplayer.bean.type.PlayerType.ScaleType
import kotlin.math.roundToInt

/** Fit the rotated image into its bounds; only FULL intentionally stretches it. */
internal fun videoSize(boxWidth: Int, boxHeight: Int, videoWidth: Int, videoHeight: Int,
                       scaleType: Int, rotation: Int): Pair<Int, Int> {
    val swap = ((rotation % 360 + 360) % 360) in listOf(90, 270)
    val width = (if (swap) boxHeight else boxWidth).coerceAtLeast(1)
    val height = (if (swap) boxWidth else boxHeight).coerceAtLeast(1)
    if (scaleType == ScaleType.FULL || videoWidth <= 0 || videoHeight <= 0) return width to height
    val ratio = when (scaleType) {
        ScaleType._16_9 -> 16f / 9
        ScaleType._16_10 -> 16f / 10
        ScaleType._5_4 -> 5f / 4
        ScaleType._4_3 -> 4f / 3
        ScaleType._1_1 -> 1f
        else -> videoWidth.toFloat() / videoHeight
    }
    return if (width.toFloat() / height > ratio) (height * ratio).roundToInt().coerceAtLeast(1) to height
    else width to (width / ratio).roundToInt().coerceAtLeast(1)
}
