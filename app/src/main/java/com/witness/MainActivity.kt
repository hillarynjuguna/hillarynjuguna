package com.witness

import android.os.Bundle
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this)
        tv.textSize = 16f
        tv.setPadding(48, 48, 48, 48)

        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabled = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { it.resolveInfo.serviceInfo.packageName == packageName }

        tv.text = if (enabled) {
            "✓ Witness is running.\n\nPause signatures are being logged.\nCheck Logcat for WitnessDB entries."
        } else {
            "Witness is installed but not active.\n\nTo activate:\nSettings → Accessibility → Installed Apps → Witness → Enable\n\nThen open TikTok and scroll for 30 seconds."
        }

        setContentView(tv)
    }
}
