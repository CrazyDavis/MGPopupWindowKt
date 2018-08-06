package org.magicalwater.mgkotlin.mgpopupwindowktdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.magicalwater.mgkotlin.mgpopupwindowkt.MGHorizontalPos
import org.magicalwater.mgkotlin.mgpopupwindowkt.MGPopupWindow
import org.magicalwater.mgkotlin.mgpopupwindowkt.MGVerticalPos

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextView.setOnClickListener {
            val popWindow = MGPopupWindow.Builder
                    .build(this, MainWidget(this))
                    .setAlpha(0.5f)
                    .createPopupWindow()
            popWindow.showAtAnchorView(mTextView, MGVerticalPos.ABOVE, MGHorizontalPos.ALIGN_RIGHT)
        }
    }
}
