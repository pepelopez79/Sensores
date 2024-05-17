package com.daw.sensores

import androidx.compose.ui.tooling.preview.Preview
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.daw.sensores.ui.theme.SensoresTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensorProximidad: Sensor? = null
    private var sensorLuz: Sensor? = null
    private var sensorAcelerometro: Sensor? = null
    private var valorProximidad by mutableStateOf(0f)
    private var valorLuz by mutableStateOf(0f)
    private var valoresAcelerometro by mutableStateOf(floatArrayOf(0f, 0f, 0f))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            SensoresTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaSensores(valorProximidad, valorLuz, valoresAcelerometro)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorProximidad?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorLuz?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorAcelerometro?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_PROXIMITY -> valorProximidad = event.values[0]
            Sensor.TYPE_LIGHT -> valorLuz = event.values[0]
            Sensor.TYPE_ACCELEROMETER -> valoresAcelerometro = event.values
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

@Composable
fun PantallaSensores(valorProximidad: Float, valorLuz: Float, valoresAcelerometro: FloatArray) {
    val proximidadTexto = if (valorProximidad < 5) "Cerca" else "Lejos"
    val luzTexto = when {
        valorLuz < 50 -> "Poca Luz"
        valorLuz < 200 -> "Luz Moderada"
        else -> "Mucha Luz"
    }
    val posicionTelefono = when {
        valoresAcelerometro[0] > 7 -> "Inclinado a la Izquierda"
        valoresAcelerometro[0] < -7 -> "Inclinado a la Derecha"
        valoresAcelerometro[1] > 7 -> "Vertical"
        valoresAcelerometro[1] < -7 -> "Invertido"
        valoresAcelerometro[2] > 7 -> "Horizontal (Pantalla Arriba)"
        valoresAcelerometro[2] < -7 -> "Horizontal (Pantalla Abajo)"
        else -> "Diagonal"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Valor de Proximidad", fontWeight = FontWeight.Bold)
            Text(text = "$valorProximidad ($proximidadTexto)")
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Valor de Luz", fontWeight = FontWeight.Bold)
            Text(text = "$valorLuz ($luzTexto)")
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Valores del Acelerómetro", fontWeight = FontWeight.Bold)
            Text(text = "X=${valoresAcelerometro[0]}")
            Text(text = "Y=${valoresAcelerometro[1]}")
            Text(text = "Z=${valoresAcelerometro[2]}")
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Posición del Teléfono", fontWeight = FontWeight.Bold)
            Text(text = posicionTelefono)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaSensoresPrevia() {
    SensoresTheme {
        PantallaSensores(valorProximidad = 0f, valorLuz = 0f, valoresAcelerometro = floatArrayOf(0f, 0f, 0f))
    }
}
