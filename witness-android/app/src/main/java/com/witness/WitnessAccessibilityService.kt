package com.witness

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import java.text.SimpleDateFormat
import java.util.*

class WitnessAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "WitnessService"
        private const val PAUSE_THRESHOLD_MS = 1500L
        private const val SESSION_GAP_MS = 300_000L
        private const val PKG_TIKTOK = "com.zhiliaoapp.musically"
        private const val PKG_INSTAGRAM = "com.instagram.android"
        private const val PKG_YOUTUBE = "com.google.android.youtube"

        val WATCHED_PACKAGES = setOf(PKG_TIKTOK, PKG_INSTAGRAM, PKG_YOUTUBE)

        fun platformName(pkg: String): String = when (pkg) {
            PKG_TIKTOK -> "tiktok"
            PKG_INSTAGRAM -> "instagram"
            PKG_YOUTUBE -> "youtube"
            else -> "unknown"
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var db: WitnessDatabase

    private var currentPackage: String = ""
    private var scrollStartTime: Long = 0L
    private var lastScrollTime: Long = 0L
    private var currentSessionId: String = ""
    private var lastSessionTime: Long = 0L

    private val pauseDetector = Runnable {
        val pauseDuration = System.currentTimeMillis() - lastScrollTime
        if (pauseDuration >= PAUSE_THRESHOLD_MS && currentPackage in WATCHED_PACKAGES) {
            logPause(currentPackage, pauseDuration)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            packageNames = WATCHED_PACKAGES.toTypedArray()
            notificationTimeout = 100
        }
        db = WitnessDatabase(applicationContext)
        Log.i(TAG, "Witness service connected — watching TikTok + Instagram")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val pkg = event.packageName?.toString() ?: return
                if (pkg != currentPackage) {
                    currentPackage = pkg
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val pkg = event.packageName?.toString() ?: return
                if (pkg !in WATCHED_PACKAGES) return
                currentPackage = pkg
                val now = System.currentTimeMillis()
                if (now - lastSessionTime > SESSION_GAP_MS || currentSessionId.isEmpty()) {
                    currentSessionId = "sess_${now / 1000}"
                }
                lastSessionTime = now
                handler.removeCallbacks(pauseDetector)
                lastScrollTime = now
                if (scrollStartTime == 0L) scrollStartTime = now
                handler.postDelayed(pauseDetector, PAUSE_THRESHOLD_MS)
            }
        }
    }

    private fun logPause(pkg: String, durationMs: Long) {
        val ts = System.currentTimeMillis()
        val platform = platformName(pkg)
        db.insertPause(
            appPackage = pkg,
            platform = platform,
            pauseDurationMs = durationMs,
            timestamp = ts,
            sessionId = currentSessionId
        )
        Log.i("WitnessDB", "pause logged | app=$platform | duration=${durationMs}ms | ts=$ts")
        scrollStartTime = 0L
    }

    override fun onInterrupt() {
        handler.removeCallbacks(pauseDetector)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pauseDetector)
        db.close()
    }
}
