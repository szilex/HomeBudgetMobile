package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.listener

import android.view.View
import androidx.fragment.app.FragmentManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.dialog.StrategyDialogFragment

class ShowStrategyChartOnClickListener(private var fragmentManager: FragmentManager?, private var goal : Double, private var months: Int, private var currentMonth: Int) : View.OnClickListener {

    override fun onClick(v: View?) {
        StrategyDialogFragment.newInstance(goal, months, currentMonth).show(fragmentManager!!, StrategyDialogFragment.NAME)
    }
}