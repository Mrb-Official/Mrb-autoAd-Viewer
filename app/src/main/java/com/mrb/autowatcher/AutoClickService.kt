package com.mrb.autowatcher

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class AutoClickService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return
        
        // Pure UI tree ko scan karo
        scanAndClick(rootNode)
    }

    private fun scanAndClick(node: AccessibilityNodeInfo?) {
        if (node == null) return

        // 1. Text ya Description se check karo (Case Insensitive + Contains match)
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        
        val targets = listOf("close", "skip", "watch ad", "claim", "reward", "start", "dismiss", "✕", "x")
        var shouldClick = false

        for (target in targets) {
            if (text.contains(target) || desc.contains(target)) {
                shouldClick = true
                Log.d("AutoAdWatcher", "Target word mil gaya: $target (Text: $text, Desc: $desc)")
                break
            }
        }

        // 2. Bada Tod: Agar text nahi mila par Screen ke Top-Right/Top-Left mein chota sa button hai (Spoofing Bypass)
        if (!shouldClick) {
            val bounds = Rect()
            node.getBoundsInScreen(bounds)
            val width = bounds.width()
            val height = bounds.height()
            
            // Agar element chota hai (jaise cross icon) aur screen ke top corners mein hai
            if (width in 40..150 && height in 40..150 && bounds.top < 200) {
                // Kisi invisible ya hidden square close button ko pakadne ke liye
                if (node.isClickable || node.parent?.isClickable == true) {
                    shouldClick = true
                    Log.d("AutoAdWatcher", "Corner ad button pakda gaya coordinates se!")
                }
            }
        }

        // Click Action Fire Karo
        if (shouldClick) {
            // Agar node khud clickable nahi hai toh uske baap (parent) ko click karo
            var clickTarget = node
            while (clickTarget != null && !clickTarget.isClickable) {
                clickTarget = clickTarget.parent
            }
            
            if (clickTarget != null && clickTarget.isClickable) {
                clickTarget.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("AutoAdWatcher", "Successfully clicked on target!")
                return // Ek baar click ho gaya toh is event ka kaam khatam
            }
        }

        // Bachon ko scan karo (Recursive Tree Walk)
        for (i in 0 until node.childCount) {
            scanAndClick(node.getChild(i))
        }
    }

    override fun onInterrupt() {
        Log.e("AutoAdWatcher", "Service interrupt ho gayi!")
    }
}