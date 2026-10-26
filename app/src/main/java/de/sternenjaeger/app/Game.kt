package de.sternenjaeger.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

class Obj(var x: Float, var y: Float, var vx: Float = 0f, var vy: Float = 0f, var t: Float = 0f)

/** Spiellogik, unabhängig von der Darstellung. Größen: 1 Einheit u = Bildschirmbreite / 360. */
class Game {
    var w = 0f
    var h = 0f
    var shipX = 0f
    var invuln = 0f
    val shots = mutableListOf<Obj>()
    val enemyShots = mutableListOf<Obj>()
    val aliens = mutableListOf<Obj>()
    val stars = List(70) { Obj(Random.nextFloat(), Random.nextFloat(), 0f, 0.04f + Random.nextFloat() * 0.15f) }

    var score by mutableIntStateOf(0)
    var lives by mutableIntStateOf(3)
    var kills by mutableIntStateOf(0)
    var over by mutableStateOf(false)

    private var fireCd = 0f
    private var spawnCd = 1f

    val u: Float get() = if (w > 0f) w / 360f else 1f
    val level: Int get() = kills / 10 + 1
    val shipY: Float get() = h - 110f * u

    fun resize(width: Float, height: Float) {
        if (w == 0f) shipX = width / 2f
        w = width
        h = height
    }

    fun reset() {
        shots.clear(); enemyShots.clear(); aliens.clear()
        score = 0; lives = 3; kills = 0
        invuln = 0f; fireCd = 0f; spawnCd = 1f
        shipX = w / 2f
        over = false
    }

    private fun loseLife() {
        if (invuln > 0f) return
        lives -= 1
        invuln = 1.5f
        if (lives <= 0) over = true
    }

    fun update(dt: Float) {
        if (w == 0f) return
        for (s in stars) { s.y += s.vy * dt; if (s.y > 1f) s.y -= 1f }
        if (over) return

        invuln = max(0f, invuln - dt)
        fireCd -= dt
        if (fireCd <= 0f) {
            shots.add(Obj(shipX, shipY - 30f * u, 0f, -720f * u))
            fireCd = 0.22f
        }
        spawnCd -= dt
        if (spawnCd <= 0f) {
            aliens.add(Obj(30f * u + Random.nextFloat() * (w - 60f * u), -30f * u, 0f, (70f + level * 10f) * u, Random.nextFloat() * 6f))
            spawnCd = max(0.35f, 1.3f - level * 0.08f)
        }

        for (s in shots) s.y += s.vy * dt
        shots.removeAll { it.y < -20f * u }
        for (e in enemyShots) e.y += e.vy * dt

        for (a in aliens) {
            a.t += dt * 2f
            a.x = (a.x + sin(a.t) * 70f * u * dt).coerceIn(20f * u, w - 20f * u)
            a.y += a.vy * dt
            if (Random.nextFloat() < dt * (0.12f + level * 0.03f)) {
                enemyShots.add(Obj(a.x, a.y + 20f * u, 0f, 280f * u))
            }
        }

        val si = shots.iterator()
        while (si.hasNext()) {
            val s = si.next()
            val hit = aliens.firstOrNull { hypot(it.x - s.x, it.y - s.y) < 24f * u }
            if (hit != null) {
                aliens.remove(hit)
                si.remove()
                score += 10
                kills += 1
            }
        }

        aliens.removeAll { a ->
            val hitShip = hypot(a.x - shipX, a.y - shipY) < 40f * u
            val gone = a.y > h
            if (hitShip || gone) loseLife()
            hitShip || gone
        }
        enemyShots.removeAll { e ->
            val hit = hypot(e.x - shipX, e.y - shipY) < 22f * u
            if (hit) loseLife()
            hit || e.y > h
        }
    }
}
