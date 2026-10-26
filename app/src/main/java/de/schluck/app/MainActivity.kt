package de.schluck.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.launch
import java.time.LocalDate

private val Context.store by preferencesDataStore("schluck")
private val GOAL = intPreferencesKey("goal")
private val AMOUNT = intPreferencesKey("amount")
private val LAST = intPreferencesKey("last")
private val DAY = stringPreferencesKey("day")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { Screen(applicationContext) } } }
    }
}

@Composable
fun Screen(ctx: Context) {
    val scope = rememberCoroutineScope()
    val today = LocalDate.now().toString()
    val prefs by ctx.store.data.collectAsState(initial = null)
    val goal = prefs?.get(GOAL) ?: 2000
    val sameDay = prefs?.get(DAY) == today
    val amount = if (sameDay) prefs?.get(AMOUNT) ?: 0 else 0
    var showGoal by remember { mutableStateOf(false) }

    fun add(d: Int) = scope.launch {
        ctx.store.edit {
            val cur = if (it[DAY] == today) it[AMOUNT] ?: 0 else 0
            it[DAY] = today
            it[AMOUNT] = (cur + d).coerceAtLeast(0)
            it[LAST] = d
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Schluck", fontSize = 32.sp, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { (amount / goal.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.size(220.dp),
                strokeWidth = 16.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$amount ml", fontSize = 36.sp)
                Text("von $goal ml (${(amount * 100 / goal)} %)")
            }
        }
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(150, 250, 500).forEach { ml -> Button(onClick = { add(ml) }) { Text("+$ml") } }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { add(-(prefs?.get(LAST) ?: 0)) }) { Text("Rückgängig") }
            OutlinedButton(onClick = { scope.launch { ctx.store.edit { it[AMOUNT] = 0; it[DAY] = today; it[LAST] = 0 } } }) { Text("Reset") }
        }
        TextButton(onClick = { showGoal = true }) { Text("Tagesziel ändern") }
        if (amount >= goal) Text("Ziel erreicht! 🎉", textAlign = TextAlign.Center)
    }

    if (showGoal) {
        var text by remember { mutableStateOf(goal.toString()) }
        AlertDialog(
            onDismissRequest = { showGoal = false },
            title = { Text("Tagesziel (ml)") },
            text = { OutlinedTextField(text, { text = it.filter(Char::isDigit) }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = {
                    val v = text.toIntOrNull()?.coerceIn(500, 10000) ?: goal
                    scope.launch { ctx.store.edit { it[GOAL] = v } }
                    showGoal = false
                }) { Text("Speichern") }
            },
            dismissButton = { TextButton(onClick = { showGoal = false }) { Text("Abbrechen") } }
        )
    }
}
