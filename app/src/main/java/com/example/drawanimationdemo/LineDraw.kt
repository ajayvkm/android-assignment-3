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

class LineDraw(ctx: Context, attr: AttributeSet? = null) : View(ctx, attr) {
    /**
     * Edit Mode - To maintain the state of mode (Edit or Animation)
     */
    var editMode: Boolean = true

    /**
     *
     */
    var currentLine: Line? = null

    /**
     * Holds lines array - will be used when onDraw event trigger
     */
    var lineArrayList = ArrayList<Line>(100)

    /**
     * Set Line Style using Paint
     */
    val lineColor = Paint()

    /**
     * Set Canvas style using Paint
     */
    val bgCanvas = Paint()
    /**
     * Define the range to check the line start or end point within the range then
     * apply the close point so that it can make some shapes
     */
    val range: Float = 10.0F

    val ctx: Context = ctx

    init {
        lineColor.apply {
            color = Constants.LINE_COLOR.toInt()
            strokeWidth = 7.0f
            strokeJoin = Paint.Join.ROUND
        }
        bgCanvas.apply {
            color = Constants.BG_COLOR.toInt()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        /**
         * If it is in edit mode then only allow to calculate point
         */
        if (editMode) {
            val current = PointF(event.x, event.y)
            var currentValidatedRange = current
            var action = ""
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    action = Constants.ACTION_DOWN
                    /**
                     * Check the distance before start the line
                     * if within the range then join the line with either start point helpful in drawing shapes
                     */
                    currentValidatedRange = checkDistanceWithinRange(current)
                    currentLine = Line(currentValidatedRange).also {
                        lineArrayList.add(it)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    action = Constants.ACTION_MOVE
                    updateCurrentLineInformation(current)
                }
                MotionEvent.ACTION_UP -> {
                    action = Constants.ACTION_UP
                    /**
                     * Check the distance before start the line
                     * if within the range then join the line with either end point helpful in drawing shapes
                     */
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
        lineArrayList.forEach { linePoint ->
            // Check Line Start Point
            var distance = calculateDistance(current, linePoint.startCoordinate)
            // If distance within the range - then new line start from line start point
            if (null != distance && distance <= range) {
                Toast.makeText(
                    ctx,
                    Constants.START_POINT_DISTANCE + distance,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return linePoint.startCoordinate
            } else {
                // Check line end point if it is in range
                distance = calculateDistance(current, linePoint.endCoordinate)
                if (null != distance && distance <= range) {
                    Toast.makeText(
                        ctx,
                        Constants.END_POINT_DISTANCE + distance,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return linePoint.endCoordinate
                }
            }
        }
        return current
    }

    /**
     * Method to calculate the distance between 2 points
     */
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

    /**
     * Method to handle onDraw event
     * Responsible for drawing the lines from ArrayList
     */
    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(bgCanvas)
        lineArrayList.forEach { line ->
            canvas.drawLine(
                line.startCoordinate.x,
                line.startCoordinate.y,
                line.endCoordinate.x,
                line.endCoordinate.y,
                lineColor
            )
        }
    }

    /**
     * Method to update the current point as line end point when motion event action up
     */
    private fun updateCurrentLineInformation(current: PointF) {
        currentLine?.endCoordinate = current
        invalidate()
    }

    /**
     * Method to clear the drawn lines from canvas
     */
    public fun clearDrawn() {
        lineArrayList.clear()
        invalidate()
    }
}