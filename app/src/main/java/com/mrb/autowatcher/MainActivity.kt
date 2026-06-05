package com.mrb.autowatcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Toast.makeText(this, "Accessibility Settings me jake 'Auto Ad Watcher' ya 'Mrb-autoAd-Viewer' ko ON karo", Toast.LENGTH_LONG).show()
        
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish() 
    }
}