package lib.kalu.mediaplayer.compose.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lib.kalu.mediaplayer.bean.args.StartArgs
import lib.kalu.mediaplayer.bean.type.PlayerType
import lib.kalu.mediaplayer.compose.*
import lib.kalu.mediaplayer.listener.OnPlayerEventListener
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url")
            ?: "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        setContent {
            MaterialTheme {
                val player = rememberPlayerController()
                LaunchedEffect(player, url) {
                    player.setOnPlayerEventListener(object : OnPlayerEventListener {
                        override fun onEvent(event: Int) { Log.i("ComposeSample", "event=$event") }
                        override fun onPrepare() { Log.i("ComposeSample", "prepared") }
                        override fun onStart() { Log.i("ComposeSample", "started") }
                        override fun onComplete() { Log.i("ComposeSample", "completed") }
                        override fun onError(code: Int) { Log.e("ComposeSample", "error=$code") }
                    })
                    player.setOnPlayerProgressListener(object : OnPlayerProgressListener {
                        override fun onProgress(trial: Long, position: Long, duration: Long) {
                            Log.d("ComposeSample", "position=$position duration=$duration")
                        }
                    })
                    player.start(StartArgs.newBuilder().setKernelType(PlayerType.KernelType.MPV)
                        .setUrl(url).setPlayWhenReady(true).build())
                }
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    Text("Compose / MPV")
                    PlayerLayout(player, Modifier.fillMaxWidth().height(240.dp))
                    Text("${player.state.videoWidth} × ${player.state.videoHeight}  ${player.state.position} / ${player.state.duration} ms")
                    Row {
                        Button(onClick = { player.seekTo(60_000) }) { Text("跳到 60 秒") }
                        Button(onClick = player::startFloat) { Text("小窗") }
                    }
                    Button(onClick = { player.release(); player.restart() }) { Text("释放后重播") }
                }
            }
        }
    }
}
