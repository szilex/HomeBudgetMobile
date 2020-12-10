package edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StrategyGridItemDecoration (private val largePadding: Int, private val smallPadding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = smallPadding
        outRect.bottom = smallPadding
    }
}