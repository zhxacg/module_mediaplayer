package lib.kalu.mediaplayer.compose

import lib.kalu.mediaplayer.PlayerLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiCompatibilityTest {
    @Test fun mpvArgumentsDoNotRequireMedia3AtRuntime() {
        val args = lib.kalu.mediaplayer.bean.args.StartArgs.newBuilder().build()
        org.junit.Assert.assertNotNull(args.liveConfiguration)
        assertEquals(500_000L, lib.kalu.mediaplayer.PlayerConst.LiveConfiguration.DEFAULT_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US)
        assertEquals(20_000L, lib.kalu.mediaplayer.PlayerConst.LiveConfiguration.DEFAULT_MAX_LIVE_OFFSET_ERROR_US_FOR_UNIT_SPEED)
    }
    @Test fun businessMethodsKeepJavaSignatures() {
        // View/XML/component contracts are deliberately replaced with Compose slots.
        val viewOnly = setOf("dispatchTouchEvent", "dispatchKeyEvent", "addComponent", "addAllComponent",
            "findComponent", "showComponent", "hideComponent", "getComponentCount", "showOnlyComponent",
            "isComponentEmpty", "isComponentNotEmpty", "hideAllComponent", "clearAllComponent")
        val business = PlayerLayout::class.java.declaredMethods.filter {
            java.lang.reflect.Modifier.isPublic(it.modifiers) && it.name !in viewOnly
        }
        for (method in business) {
            val replacement = PlayerController::class.java.getMethod(method.name, *method.parameterTypes)
            assertEquals("Return type for ${method.name}", method.returnType, replacement.returnType)
        }
    }
}
