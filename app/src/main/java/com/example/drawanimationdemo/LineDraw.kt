package com.example.drawanimationdemo

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class LineDraw(ctx: Context, attr: AttributeSet? = null): View(ctx, attr) {

    var editMode: Boolean = true
    var currentLine: Line? = null
    var lineArrayList = arrayListOf<Line>()
    val lineColor = Paint().apply {
        color = 0x22000003.toInt()
        strokeWidth = 5f
    }
    val bgCanvas = Paint().apply {
        color = 0xfff9faed.toInt()
    }
    val range: Float = 10.0F
    val ctx: Context = ctx

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(editMode) {
            val current = PointF(event.x, event.y)
            var currentValidatedRange = current
            var action = ""
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    action = "ACTION_DOWN"
                    currentValidatedRange = checkDistanceWithinRange(current)
                    currentLine = Line(currentValidatedRange).also {
                        lineArrayList.add(it)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    action = "ACTION_MOVE"
                    updateCurrentLineInformation(current)
                }
                MotionEvent.ACTION_UP -> {
                    action = "ACTION_UP"
                    currentValidatedRange = checkDistanceWithinRange(current)
                    updateCurrentLineInformation(currentValidatedRange)
                    currentLine = null
                }
            }

            Log.i(TAG, "$action at x=${current.x}, y=${current.y}")
            return true
        } else {
            return false
        }
    }

    /**
     * Method to find the distance between Current Point to Any Point Within the Range
     * If within the range then return the Near by point Else return current point
     */
    private fun checkDistanceWithinRange(current: PointF): PointF {
        lineArrayList.forEach {linePoint ->
            // Check Line Start Point
            var distance = calculateDistance(current, linePoint.startCoordinate)
            // If distance within the range - then new line start from line start point
            if(null != distance && distance <= range) {
                Toast.makeText(ctx, "START POINT DISTANCE -> " + distance, Toast.LENGTH_SHORT).show()
                return linePoint.startCoordinate
            } else {
                // Check line end point if it is in range
                distance = calculateDistance(current, linePoint.endCoordinate)
                if(null != distance && distance <= range) {
                    Toast.makeText(ctx, "END POINT DISTANCE -> " + distance, Toast.LENGTH_SHORT).show()
                    return linePoint.endCoordinate
                }
            }
        }
        return current
    }

    fun calculateDistance(current: PointF, linePointCoord: PointF): Double {
        var distance: Double? = null
        distance = Math.sqrt(
            Math.pow((current.x - linePointCoord.x).toDouble(), 2.0) + Math.pow(
                (current.y - linePointCoord.y).toDouble(),
                2.0
            )
        )
        return distance
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(bgCanvas)
        lineArrayList.forEach { line ->
            canvas.drawLine(line.startCoordinate!!.x, line.startCoordinate!!.y, line.endCoordinate!!.x, line.endCoordinate!!.y, lineColor)
        }
    }

    private fun updateCurrentLineInformation(current: PointF) {
        currentLine?.let {
            it.endCoordinate = current
            invalidate()
        }
    }

    public fun clearDrawn() {
        lineArrayList.clear()
        invalidate()
    }
}