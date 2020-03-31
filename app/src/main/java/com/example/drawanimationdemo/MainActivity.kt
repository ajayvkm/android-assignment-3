package com.example.drawanimationdemo

import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.ViewPropertyAnimator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null

    var action: String = "GO"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Button code base
         */
        clearButton.setOnClickListener {
            LineDraw.clearDrawn()
        }

        actionButton.setOnClickListener {
            when (action) {
                Constants.GO -> {
                    startButtonRotate(Constants.GO)
                    //Change action from GO to STOP
                    action = Constants.STOP
                    actionButton.text = action
                    LineDraw.editMode = false
                    // Accelerometer initialization
                    initializeAccelerometer()
                    Toast.makeText(this, "ANIMATION MODE", Toast.LENGTH_SHORT).show()
                }
                Constants.STOP -> {
                    startButtonRotate(Constants.STOP)
                    //Change action from STOP to GO
                    action = Constants.GO
                    actionButton.text = action
                    LineDraw.editMode = true
                    Toast.makeText(this, "EDIT MODE", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeAccelerometer() {
        /**
         * Censor Accelerometer Initialization code
         * Initialize when it is in developer mode
         */
        if(!LineDraw.editMode) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(event: SensorEvent) {
        var alpha: Float = 0.8f
        var gravityV = FloatArray(3)

        //gravity is calculated here
        gravityV[0] = alpha * gravityV[0] + (1 - alpha) * event.values[0]
        gravityV[1] = alpha * gravityV[1] + (1 - alpha)* event.values[1]
        gravityV[2] = alpha * gravityV[2] + (1 - alpha) * event.values[2]

        //acceleration retrieved from the event and the gravity is removed
        var x = event.values[0] - gravityV[0];
        var y = event.values[1] - gravityV[1];
        var z = event.values[2] - gravityV[2];

        Log.i("rew", "x: ${event!!.values[0]} y: ${event.values[1]} z: ${event.values[2]}")

        Log.i(ContentValues.TAG, "Accelerometer -> x=$x and y=$y, z=$z")
    }

    /**
     * Button to rotate 360 Degree clockwise on click of GO button
     */
    fun startButtonRotate(action: String) {
        var actionButtonAnimateObject : ViewPropertyAnimator = actionButton.animate()
        actionButtonAnimateObject.duration = 1200
        when (action) {
            Constants.GO -> actionButtonAnimateObject.rotationBy(360f) // Clockwise
            Constants.STOP -> actionButtonAnimateObject.rotationBy(-360f) // Anti Clockwise
        }
        actionButtonAnimateObject.start()
    }
}
