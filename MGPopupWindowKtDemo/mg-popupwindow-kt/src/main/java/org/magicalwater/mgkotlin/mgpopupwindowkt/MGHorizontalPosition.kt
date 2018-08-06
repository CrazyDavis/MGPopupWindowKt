package org.magicalwater.mgkotlin.mgpopupwindowkt

import android.support.annotation.IntDef

@IntDef(MGHorizontalPos.CENTER, MGHorizontalPos.LEFT, MGHorizontalPos.RIGHT, MGHorizontalPos.ALIGN_LEFT, MGHorizontalPos.ALIGN_RIGHT)
@Retention(AnnotationRetention.SOURCE)
annotation class MGHorizontalPosition

class MGHorizontalPos {
    companion object {
        const val CENTER = 0
        const val LEFT = 1
        const val RIGHT = 2
        const val ALIGN_LEFT = 3
        const val ALIGN_RIGHT = 4
    }
}