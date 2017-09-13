package com.juniperphoton.myersplash.widget

import android.content.Context
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myersplash.R
import com.juniperphoton.myersplash.activity.AboutActivity
import com.juniperphoton.myersplash.activity.ManageDownloadActivity
import com.juniperphoton.myersplash.activity.SettingsActivity
import com.juniperphoton.myersplash.model.UnsplashCategory

@Suppress("UNUSED")
class PivotTitleBar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    companion object {
        private const val DEFAULT_SELECTED = 1
    }

    @BindView(R.id.more_btn)
    lateinit var moreBtn: View

    @BindView(R.id.pivot_item_0)
    lateinit var item0: TextView

    @BindView(R.id.pivot_item_1)
    lateinit var item1: TextView

    @BindView(R.id.pivot_item_2)
    lateinit var item2: TextView

    /**
     * Invoked when a single tap is performed on an item.
     */
    var onSingleTap: ((Int) -> Unit)? = null

    /**
     * Invoked when a double tap is performed on an item.
     */
    var onDoubleTap: ((Int) -> Unit)? = null

    /**
     * Which item is current selected.
     */
    var selectedItem = DEFAULT_SELECTED
        set(value) {
            toggleAnimation(selectedItem, value)
            field = value
        }

    /**
     * Which item name is current selected.
     */
    val selectedString: String
        get() = when (selectedItem) {
            0 -> UnsplashCategory.FEATURE_S.toUpperCase()
            1 -> UnsplashCategory.NEW_S.toUpperCase()
            2 -> UnsplashCategory.RANDOM_S.toUpperCase()
            else -> UnsplashCategory.NEW_S.toUpperCase()
        }

    private var touchingViewIndex: Int = 0

    private lateinit var gestureDetector: GestureDetector

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onSingleTap?.invoke(touchingViewIndex)
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleTap?.invoke(touchingViewIndex)
            return super.onDoubleTap(e)
        }
    }

    private val onTouchListener = View.OnTouchListener { v, event ->
        when {
            v === item0 -> touchingViewIndex = 0
            v === item1 -> touchingViewIndex = 1
            v === item2 -> touchingViewIndex = 2
        }
        gestureDetector.onTouchEvent(event)
        true
    }

    private val menuMap: Map<Int, Class<out Any>> = mapOf(
            R.id.menu_settings to SettingsActivity::class.java,
            R.id.menu_downloads to ManageDownloadActivity::class.java,
            R.id.menu_about to AboutActivity::class.java
    )

    private var itemsMap: Map<Int, View>

    init {
        LayoutInflater.from(context).inflate(R.layout.pivot_layout, this, true)
        ButterKnife.bind(this)

        itemsMap = mapOf(
                0 to item0,
                1 to item1,
                2 to item2
        )

        gestureDetector = GestureDetector(context, gestureListener)
        item0.setOnTouchListener(onTouchListener)
        item1.setOnTouchListener(onTouchListener)
        item2.setOnTouchListener(onTouchListener)
    }

    private fun toggleAnimation(prevIndex: Int, newIndex: Int) {
        val prevView = itemsMap[prevIndex]
        val nextView = itemsMap[newIndex]

        prevView?.animate()?.alpha(0.3f)?.setDuration(300)?.start()
        nextView?.animate()?.alpha(1f)?.setDuration(300)?.start()
    }

    @OnClick(R.id.pivot_item_0)
    fun onClickItem0() {
        onSingleTap?.invoke(0)
    }

    @OnClick(R.id.pivot_item_1)
    fun onClickItem1() {
        onSingleTap?.invoke(1)
    }

    @OnClick(R.id.pivot_item_2)
    fun onClickItem2() {
        onSingleTap?.invoke(2)
    }

    @OnClick(R.id.more_btn)
    fun onClickMore() {
        val popupMenu = PopupMenu(context, moreBtn)
        popupMenu.inflate(R.menu.main)
        popupMenu.gravity = Gravity.END
        popupMenu.setOnMenuItemClickListener { item ->
            val intent: Intent?
            intent = Intent(context, menuMap[item.itemId])
            context.startActivity(intent)
            true
        }
        popupMenu.show()
    }
}
