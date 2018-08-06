package org.magicalwater.mgkotlin.mgpopupwindowktdemo

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import org.jetbrains.anko.backgroundColor
import org.magicalwater.mgkotlin.mgviewskt.layout.MGBaseFrameLayout
import org.magicalwater.mgkotlin.mgviewskt.layout.MGBaseLinearLayout

class MainWidget: MGBaseFrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun contentLayout(): Int? = R.layout.popwindow_main

    override fun setupWidget(style: TypedArray?) {
        super.setupWidget(style)
    }

    init {
        setupWidget(mStyleArray)
        backgroundColor = Color.parseColor("#88ccaa")
    }
}