package de.sternenjaeger.app

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private val Context.store by preferencesDataStore("sternenjaeger")
private val HIGH = intPreferencesKey("highscore")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { GameScreen(applicationContext) }
    }
}

@Composable
fun GameScreen(ctx: Context) {
    val game = remember { Game() }
    var tick by remember { mutableIntStateOf(0) }
    val prefs by ctx.store.data.collectAsState(initial = null)
    val high = maxOf(prefs?.get(HIGH) ?: 0, if (game.over) game.score else 0)

    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            androidx.compose.runtime.withFrameNanos { now ->
                if (last != 0L) game.update(((now - last) / 1_000_000_000f).coerceAtMost(0.05f))
                last = now
                tick++
            }
        }
    }

    LaunchedEffect(game.over) {
        if (game.over) {
            ctx.store.edit { if (game.score > (it[HIGH] ?: 0)) it[HIGH] = game.score }
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF05071A))) {
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { game.shipX = it.x.coerceIn(0f, game.w) },
                        onDrag = { change, _ ->
                            change.consume()
                            game.shipX = change.position.x.coerceIn(0f, game.w)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { if (game.over) game.reset() })
                }
        ) {
            val frame = tick // Neuzeichnen bei jedem Frame auslösen
            game.resize(size.width, size.height)
            drawGame(game, frame)
        }

        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Punkte ${game.score}", color = Color.White, fontSize = 18.sp)
            Text("Level ${game.level}", color = Color.White, fontSize = 18.sp)
            Text("♥".repeat(game.lives.coerceAtLeast(0)), color = Color(0xFFFF5470), fontSize = 18.sp)
        }

        if (game.over) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("GAME OVER", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Text("Punkte: ${game.score}", color = Color.White, fontSize = 22.sp)
                Text("Highscore: $high", color = Color(0xFFFFD166), fontSize = 22.sp)
                Text(
                    "Tippen für neues Spiel",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawGame(g: Game, @Suppress("UNUSED_PARAMETER") frame: Int) {
    val u = g.u
    for (s in g.stars) {
        drawCircle(Color.White.copy(alpha = 0.7f), radius = 1.5f * u, center = Offset(s.x * g.w, s.y * g.h))
    }
    for (s in g.shots) {
        drawRect(Color(0xFF4DE1FF), topLeft = Offset(s.x - 2f * u, s.y - 8f * u), size = Size(4f * u, 16f * u))
    }
    for (e in g.enemyShots) {
        drawCircle(Color(0xFFFF4D4D), radius = 5f * u, center = Offset(e.x, e.y))
    }
    for (a in g.aliens) {
        drawOval(Color(0xFF55E07A), topLeft = Offset(a.x - 20f * u, a.y - 12f * u), size = Size(40f * u, 24f * u))
        drawCircle(Color(0xFF55E07A), radius = 10f * u, center = Offset(a.x, a.y - 10f * u))
        drawCircle(Color.Black, radius = 3f * u, center = Offset(a.x - 5f * u, a.y - 10f * u))
        drawCircle(Color.Black, radius = 3f * u, center = Offset(a.x + 5f * u, a.y - 10f * u))
    }
    if (!g.over) {
        val blink = g.invuln > 0f && (g.invuln * 10f).toInt() % 2 == 0
        if (!blink) {
            val x = g.shipX
            val y = g.shipY
            val ship = Path().apply {
                moveTo(x, y - 28f * u)
                lineTo(x - 22f * u, y + 20f * u)
                lineTo(x, y + 10f * u)
                lineTo(x + 22f * u, y + 20f * u)
                close()
            }
            drawPath(ship, Color(0xFFE8ECFF))
            drawCircle(Color(0xFF4DE1FF), radius = 5f * u, center = Offset(x, y - 4f * u))
        }
    }
}
