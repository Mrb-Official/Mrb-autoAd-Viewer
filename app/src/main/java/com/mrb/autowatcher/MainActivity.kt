package com.mrb.autowatcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // App khulte hi user ko instruction do
        Toast.makeText(this, "Accessibility Settings me jake 'Auto Ad Watcher' ON karo", Toast.LENGTH_LONG).show()
        
        // Direct phone ki Accessibility Settings open kar do taaki seedha ON kar sake
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        finish() // Settings khulte hi app ka UI band kar do (background chalne do)
    }
}