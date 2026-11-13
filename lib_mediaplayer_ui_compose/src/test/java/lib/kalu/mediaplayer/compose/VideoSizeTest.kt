package lib.kalu.mediaplayer.compose

import lib.kalu.mediaplayer.bean.type.PlayerType.ScaleType
import org.junit.Assert.assertEquals
import org.junit.Test

class VideoSizeTest {
    @Test fun landscapeFitsPortraitWithoutStretching() {
        assertEquals(1080 to 608, videoSize(1080, 1920, 1920, 1080, ScaleType.DEFAULT, 0))
    }
    @Test fun portraitFitsLandscape() {
        assertEquals(608 to 1080, videoSize(1920, 1080, 1080, 1920, ScaleType.DEFAULT, 0))
    }
    @Test fun quarterTurnUsesUnrotatedSurfaceSize() {
        assertEquals(1920 to 1080, videoSize(1080, 1920, 1920, 1080, ScaleType.DEFAULT, 90))
        assertEquals(1920 to 1080, videoSize(1080, 1920, 1920, 1080, ScaleType.DEFAULT, -90))
    }
    @Test fun fullUsesBoundsAndExplicitRatiosOverrideSource() {
        assertEquals(1000 to 500, videoSize(1000, 500, 100, 100, ScaleType.FULL, 0))
        assertEquals(667 to 500, videoSize(1000, 500, 100, 100, ScaleType._4_3, 0))
    }
    @Test fun unknownVideoHasUsableSurface() {
        assertEquals(1000 to 500, videoSize(1000, 500, 0, 0, ScaleType.DEFAULT, 0))
        assertEquals(1 to 1, videoSize(0, 0, 0, 0, ScaleType.DEFAULT, 0))
    }
}
