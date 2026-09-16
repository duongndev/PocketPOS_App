package com.duongnd.pocketposapp.core.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Extension modifier tự động ẩn bàn phím (clear focus) khi chạm vào bất kỳ vùng trống nào ngoài ô nhập liệu.
 */
fun Modifier.clearFocusOnTap(focusManager: FocusManager): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }
}
