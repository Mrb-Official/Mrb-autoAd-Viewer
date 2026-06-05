package com.mrb.autowatcher

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class AutoClickService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // System apps aur launcher ko block rakhenge taaki phone khud se pagal na ho
        val currentPackage = event.packageName?.toString() ?: ""
        if (currentPackage.contains("android.launcher") || 
            currentPackage.contains("com.google.android.googlequicksearchbox") || 
            currentPackage.contains("com.android.systemui") || 
            currentPackage.contains("com.android.settings")) {
            return 
        }

        val rootNode = rootInActiveWindow ?: return
        scanAndClick(rootNode)
    }

    private fun scanAndClick(node: AccessibilityNodeInfo?) {
        if (node == null) return

        val text = node.text?.toString()?.lowercase()?.trim() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase()?.trim() ?: ""
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        
        val targetWords = listOf("close", "skip", "skip ad", "watch ad", "claim", "reward", "dismiss", "✕", "dismiss")
        var shouldClick = false

        // Rule 1: Agar text, description ya view ID mein koi bhi ad-closing word match ho jaye
        for (target in targetWords) {
            if (text == target || desc == target || text.contains("skip") || viewId.contains("close") || viewId.contains("skip")) {
                shouldClick = true
                Log.d("AutoAdWatcher", "Pakka Ad Button pakda gaya: $target")
                break
            }
        }

        // Rule 2: AJEEB BUTTONS KA TOD (No name, no text, but small clickable corners)
        if (!shouldClick) {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            val w = bounds.width()
            val h = bounds.height()

            // Agar koi button chota sa hai (40px se 140px ke beech), aur screen ke top 20% area mein hai
            // Aur uspar koi text nahi hai (yaani sirf icon ya invisible button hai)
            if (w in 40..140 && h in 40..140 && bounds.top < 220) {
                if (node.isClickable || node.parent?.isClickable == true) {
                    shouldClick = true
                    Log.d("AutoAdWatcher", "Ajeeb Bina-Naam wala close button pakda coordinates se!")
                }
            }
        }

        // Click Action Trigger
        if (shouldClick) {
            var clickTarget = node
            // Agar main node clickable nahi hai, toh uske clickable parent (baap) tak jao
            while (clickTarget != null && !clickTarget.isClickable) {
                clickTarget = clickTarget.parent
            }
            
            if (clickTarget != null && clickTarget.isClickable) {
                clickTarget.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("AutoAdWatcher", "Ajeeb button par Successfully auto-click maara!")
                return 
            }
        }

        // Poore UI tree ke baki bachon ko check karo
        for (i in 0 until node.childCount) {
            scanAndClick(node.getChild(i))
        }
    }

    override fun onInterrupt() {
        Log.e("AutoAdWatcher", "Service interrupt ho gayi!")
    }
}