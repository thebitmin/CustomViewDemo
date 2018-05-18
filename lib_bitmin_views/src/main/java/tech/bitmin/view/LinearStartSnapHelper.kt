package tech.bitmin.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Bitmin on 2018/5/14.
 * Email: thebititmin@outlook.com
 * Blog: Bitmin.tech
 *
 * 左对齐的 Snap
 */
class LinearStartSnapHelper : LinearSnapHelper() {

    private var mHorizontalHelper: OrientationHelper? = null
    private var mVerticalHelper: OrientationHelper? = null

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper): Int {
        val decoratedStart = helper.getDecoratedStart(targetView)
        val startAfterPadding = helper.startAfterPadding
        return decoratedStart - startAfterPadding
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        @Suppress("SENSELESS_COMPARISON")
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        @Suppress("SENSELESS_COMPARISON")
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper!!
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager is LinearLayoutManager) {
            val helper = if (layoutManager.canScrollHorizontally()) getHorizontalHelper(layoutManager)
            else getVerticalHelper(layoutManager)
            return findStartView(layoutManager, helper)
        }
        return super.findSnapView(layoutManager)
    }

    private fun findStartView(layoutManager: RecyclerView.LayoutManager, helper: OrientationHelper): View? {
        if (layoutManager !is LinearLayoutManager) {
            return super.findSnapView(layoutManager)
        }
        val firstChildPosition = layoutManager.findFirstVisibleItemPosition()
        val isLastItem = layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1
        //没有 Item 或者滑动到最后一个 item
        if (firstChildPosition == RecyclerView.NO_POSITION || isLastItem) {
            return null
        }
        val firstChildView = layoutManager.findViewByPosition(firstChildPosition)
        val firstChildDecoratedEnd = helper.getDecoratedEnd(firstChildView)
        val halfDecoratedMeasurement = helper.getDecoratedMeasurement(firstChildView) / 2
        val isHalfVisiable = firstChildDecoratedEnd >= halfDecoratedMeasurement
        val isFirstChildVisiable = firstChildDecoratedEnd > 0
        return if (isFirstChildVisiable && isHalfVisiable) {
            //如果第一个 item 可见，并且可见宽度大于一半，则返回第一个 item
            firstChildView
        } else {
            //如果第一个 item 一半宽度已滑出屏幕，返回它后面一个 item，这个 item 不会是最后一个 item，前面已经判断
            layoutManager.findViewByPosition(firstChildPosition + 1)
        }
    }
}