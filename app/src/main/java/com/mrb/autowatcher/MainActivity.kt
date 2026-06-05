package com.mrb.autowatcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Dynamic layout generation
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(50, 50, 50, 50) // Galti yahan thi, ab sahi kar di hai
        }

        val statusText = android.widget.TextView(this).apply {
            text = "Auto Ad Watcher Status"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 50)
        }

        val btnToggle = Button(this).apply {
            text = if (isServiceRunning()) "STOP SERVICE" else "START / ENABLE SERVICE"
            setBackgroundColor(if (isServiceRunning()) 0xFFFF2C2C.toInt() else 0xFF2CBB2C.toInt())
            setTextColor(0xFFFFFFFF.toInt())
        }

        btnToggle.setOnClickListener {
            Toast.makeText(this, "List me se 'Mrb-autoAd-Viewer' select karke ON/OFF karein", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

        layout.addView(statusText)
        layout.addView(btnToggle)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        // Fresh state reload
        val serviceRunning = isServiceRunning()
        setContentView(null)
        onCreate(null)
    }

    private fun isServiceRunning(): Boolean {
        val serviceName = "${packageName}/${AutoClickService::class.java.name}"
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains(serviceName) == true
    }
}