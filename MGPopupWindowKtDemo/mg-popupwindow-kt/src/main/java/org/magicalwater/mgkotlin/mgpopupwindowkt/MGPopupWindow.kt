package org.magicalwater.mgkotlin.mgpopupwindowkt

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v4.widget.PopupWindowCompat
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow

/**
 * 參考 SmartPopupWindow
 * https://github.com/PopFisher/SmartPopupWindow
 *
 * 實際上只稍微修改
 * */
class MGPopupWindow: PopupWindow {

    private var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mAlpha = 1f //背景透明度 0-1 1表示全透明
    private var mContext: Context? = null
    private var mContentView: View? = null
    private var isTouchOutsideDismiss = true
    private var mAnimationStyle = -1

    //下面幾個變數用來處理 6.0 以上的外部點擊事件
    private var isOnlyGetWH = true
    private var mAnchorView: View? = null
    @MGVerticalPosition
    private var mVerticalGravity = MGVerticalPos.BELOW
    @MGHorizontalPosition
    private var mHorizontalGravity = MGHorizontalPos.LEFT
    private var mOffsetX: Int = 0
    private var mOffsetY: Int = 0

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        mContext = context
    }

    fun initAttr() {
        contentView = mContentView
        height = mHeight
        width = mWidth
        touchOutsideDismiss(isTouchOutsideDismiss)
        if (mAnimationStyle != -1) {
            animationStyle = mAnimationStyle
        }
    }

    private fun touchOutsideDismiss(touchOutsideDismiss: Boolean) {
        if (!touchOutsideDismiss) {
            isFocusable = true
            isOutsideTouchable = false
            this.setBackgroundDrawable(null)

            contentView.isFocusable = true
            contentView.isFocusableInTouchMode = true
            contentView.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return@OnKeyListener true
                }
                false
            })
            //在Android 6.0以上, 只能透過攔截事件
            setTouchInterceptor(View.OnTouchListener { v, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (event.action == MotionEvent.ACTION_DOWN && (x < 0 || x >= mWidth || y < 0 || y >= mHeight)) {
                    //outside
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    //outside
                    return@OnTouchListener true
                }
                false
            })
        } else {
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        isOnlyGetWH = true
        mAnchorView = parent
        mOffsetX = x
        mOffsetY = y
        addGlobalLayoutListener(contentView)
        super.showAtLocation(parent, gravity, x, y)
    }

    fun showAtAnchorView(anchorView: View, @MGVerticalPosition verticalPos: Int, @MGHorizontalPosition horizontalPos: Int) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, true)
    }

    fun showAtAnchorView(anchorView: View, @MGVerticalPosition verticalPos: Int, @MGHorizontalPosition horizontalPos: Int, fitInScreen: Boolean) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, 0, 0, fitInScreen)
    }

    fun showAtAnchorView(anchorView: View, @MGVerticalPosition verticalPos: Int, @MGHorizontalPosition horizontalPos: Int, x: Int, y: Int) {
        showAtAnchorView(anchorView, verticalPos, horizontalPos, x, y, true)
    }

    fun showAtAnchorView(anchorView: View, @MGVerticalPosition verticalPos: Int, @MGHorizontalPosition horizontalPos: Int, x: Int, y: Int, fitInScreen: Boolean) {
        var x = x
        var y = y
        isOnlyGetWH = false
        mAnchorView = anchorView
        mOffsetX = x
        mOffsetY = y
        mVerticalGravity = verticalPos
        mHorizontalGravity = horizontalPos
        showBackgroundAnimator()
        val contentView = contentView
        addGlobalLayoutListener(contentView)
        isClippingEnabled = fitInScreen
        contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
        val measuredW = contentView.measuredWidth
        val measuredH = contentView.measuredHeight
        if (!fitInScreen) {
            val anchorLocation = IntArray(2)
            anchorView.getLocationInWindow(anchorLocation)
            x += anchorLocation[0]
            y += anchorLocation[1] + anchorView.height
        }
        y = calculateY(anchorView, verticalPos, measuredH, y)
        x = calculateX(anchorView, horizontalPos, measuredW, x)
        if (fitInScreen) {
            PopupWindowCompat.showAsDropDown(this, anchorView, x, y, Gravity.NO_GRAVITY)
        } else {
            showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
        }
    }

    //根據 gravity 計算 y軸偏移
    private fun calculateY(anchor: View, verticalGravity: Int, measuredH: Int, y: Int): Int {
        var y = y
        when (verticalGravity) {
            MGVerticalPos.ABOVE -> y -= measuredH + anchor.height
            MGVerticalPos.ALIGN_BOTTOM -> y -= measuredH
            MGVerticalPos.CENTER -> y -= anchor.height / 2 + measuredH / 2
            MGVerticalPos.ALIGN_TOP -> y -= anchor.height
            MGVerticalPos.BELOW -> {
            }
        }// Default position.

        return y
    }

    //根據 gravity 計算 x軸偏移
    private fun calculateX(anchor: View, horizontalGravity: Int, measuredW: Int, x: Int): Int {
        var x = x
        when (horizontalGravity) {
            MGHorizontalPos.LEFT -> x -= measuredW
            MGHorizontalPos.ALIGN_RIGHT -> x -= measuredW - anchor.width
            MGHorizontalPos.CENTER -> x += anchor.width / 2 - measuredW / 2
            MGHorizontalPos.ALIGN_LEFT -> {}
            MGHorizontalPos.RIGHT -> x += anchor.width
        }// Default position.

        return x
    }

    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), getDropDownMeasureSpecMode(measureSpec))
    }

    private fun getDropDownMeasureSpecMode(measureSpec: Int): Int {
        return when (measureSpec) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.UNSPECIFIED
            else -> View.MeasureSpec.EXACTLY
        }
    }

    //獲取 popWindow 彈出後的寬高
    private val mOnGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        mWidth = contentView.width
        mHeight = contentView.height

        //只獲取寬高, 而不做畫面更新的動作
        if (!isOnlyGetWH) {
            updateLocation(mWidth, mHeight, mAnchorView!!, mVerticalGravity, mHorizontalGravity, mOffsetX, mOffsetY)
        }
        removeGlobalLayoutListener()
    }

    private fun updateLocation(width: Int, height: Int, anchor: View,
                               @MGVerticalPosition verticalGravity: Int,
                               @MGHorizontalPosition horizontalGravity: Int,
                               x: Int, y: Int) {
        var x = x
        var y = y
        x = calculateX(anchor, horizontalGravity, width, x)
        y = calculateY(anchor, verticalGravity, height, y)
        update(anchor, x, y, width, height)
    }

    private fun removeGlobalLayoutListener() {
        if (contentView != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                contentView.viewTreeObserver.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
            } else {
                contentView.viewTreeObserver.removeGlobalOnLayoutListener(mOnGlobalLayoutListener)
            }
        }
    }

    private fun addGlobalLayoutListener(contentView: View) {
        contentView.viewTreeObserver.addOnGlobalLayoutListener(mOnGlobalLayoutListener)
    }

    override fun dismiss() {
        super.dismiss()
        dismissBackgroundAnimator()
        removeGlobalLayoutListener()
    }

    //顯示時的漸變動畫
    private fun showBackgroundAnimator() {
        if (mAlpha >= 1f) return
        val animator = ValueAnimator.ofFloat(1.0f, mAlpha)
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            setWindowBackgroundAlpha(alpha)
        }
        animator.duration = 360
        animator.start()
    }

    //隱藏時的漸變動畫
    private fun dismissBackgroundAnimator() {
        if (mAlpha >= 1f) return
        val animator = ValueAnimator.ofFloat(mAlpha, 1.0f)
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            setWindowBackgroundAlpha(alpha)
        }
        animator.duration = 360
        animator.start()
    }

    //控制背景的不透明度
    private fun setWindowBackgroundAlpha(alpha: Float) {
        if (mContext == null) return
        if (mContext is Activity) {
            val window = (mContext as Activity).window
            val layoutParams = window.attributes
            layoutParams.alpha = alpha
            window.attributes = layoutParams
        }
    }

    class Builder {
        private var mWindow: MGPopupWindow? = null

        companion object {
            fun build(context: Context, view: View): Builder {
                return Builder(context, view)
            }
        }

        private constructor(context: Context, view: View) {
            mWindow = MGPopupWindow(context)
            mWindow!!.mContext = context
            mWindow!!.mContentView = view
        }

        fun setSize(width: Int, height: Int): Builder {
            mWindow!!.mWidth = width
            mWindow!!.mHeight = height
            return this
        }

        fun setWidth(width: Int): Builder {
            mWindow!!.mWidth = width
            return this
        }

        fun setHeight(height: Int): Builder {
            mWindow!!.mHeight = height
            return this
        }

        fun setAnimationStyle(animationStyle: Int): Builder {
            mWindow!!.mAnimationStyle = animationStyle
            return this
        }

        fun setAlpha(alpha: Float): Builder {
            mWindow!!.mAlpha = alpha
            return this
        }

        fun setOutsideTouchDismiss(dismiss: Boolean): Builder {
            mWindow!!.isTouchOutsideDismiss = dismiss
            return this
        }

        fun createPopupWindow(): MGPopupWindow {
            mWindow!!.initAttr()
            return mWindow!!
        }

    }
}