package com.example.drawanimationdemo

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object{
        val GO = "GO"
        val STOP = "STOP"
    }

    var action: String = "GO"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearButton.setOnClickListener {
            LineDraw.clearDrawn()
        }

        actionButton.setOnClickListener {
            when (action) {
                GO -> {
                    startButtonRotate()
                    action = STOP
                    actionButton.text = action
                    LineDraw.editMode = false
                    Toast.makeText(this, "ANIMATION MODE", Toast.LENGTH_SHORT).show()
                }
                STOP -> {
                    action = GO
                    actionButton.text = action
                    LineDraw.editMode = true
                    Toast.makeText(this, "EDIT MODE", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Button to rotate 360 Degree clockwise on click of GO button
     */
    fun startButtonRotate () {
        var valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 360f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration
        valueAnimator.addUpdateListener {
            val value : Float = it.animatedValue as Float
            actionButton.rotation = value
        }
        valueAnimator.start()
    }
}
