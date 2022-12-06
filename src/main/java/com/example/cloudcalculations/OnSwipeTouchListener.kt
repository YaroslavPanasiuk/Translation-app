package com.example.cloudcalculations

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

open class OnSwipeTouchListener : OnTouchListener {
    private val gestureDetector = GestureDetector(GestureListener())
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > Companion.SWIPE_THRESHOLD && Math.abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        result = if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else {
                    if (Math.abs(diffY) > Companion.SWIPE_THRESHOLD && Math.abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        result = if (diffY > 0) {
                            onSwipeBottom()
                        } else {
                            onSwipeTop()
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

    open fun onSwipeRight(): Boolean {
        return false
    }

    open fun onSwipeLeft(): Boolean {
        return false
    }

    fun onSwipeTop(): Boolean {
        return false
    }

    fun onSwipeBottom(): Boolean {
        return false
    }
}