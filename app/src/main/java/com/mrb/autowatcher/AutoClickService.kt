package com.mrb.autowatcher

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class AutoClickService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Current screen ki UI details uthao ur commit karo
        val rootNode = rootInActiveWindow ?: return

        // Wo words jo ads/videos band ya start karne ke liye aate hain
        val targetWords = listOf("Close", "Skip", "Watch Ad", "Claim Reward", "X", "Start")

        for (word in targetWords) {
            val nodes = rootNode.findAccessibilityNodeInfosByText(word)
            
            for (node in nodes) {
                // Check karo ki node clickable hai ya nahi
                if (node.isClickable || node.parent?.isClickable == true) {
                    val clickTarget = if (node.isClickable) node else node.parent
                    
                    // Jese hi element mile, uspar auto-click maar do
                    clickTarget?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.d("AutoAdWatcher", "Auto-click fired on: $word")
                    
                    // Ek click ke baad memory free karke loop se bahar aao
                    rootNode.recycle()
                    return 
                }
            }
        }
        rootNode.recycle()
    }

    override fun onInterrupt() {
        Log.e("AutoAdWatcher", "Service interrupt ho gayi!")
    }
}