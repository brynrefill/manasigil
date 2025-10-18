package com.brynrefill.manasigil.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/**
 * handles clipboard operations
 */
class ClipboardHelper(private val context: Context) {

    /**
     * copy text to clipboard
     */
    fun copy(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            context,
            "$label copied to clipboard!",
            Toast.LENGTH_SHORT
        ).show()
    }
}
