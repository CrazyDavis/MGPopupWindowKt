package org.magicalwater.mgkotlin.mgpopupwindowkt

import android.support.annotation.IntDef

@IntDef(MGVerticalPos.CENTER, MGVerticalPos.ABOVE, MGVerticalPos.BELOW, MGVerticalPos.ALIGN_TOP, MGVerticalPos.ALIGN_BOTTOM)
@Retention(AnnotationRetention.SOURCE)
annotation class MGVerticalPosition

class MGVerticalPos {
    companion object {
        const val CENTER = 0
        const val ABOVE = 1
        const val BELOW = 2
        const val ALIGN_TOP = 3
        const val ALIGN_BOTTOM = 4
    }
}