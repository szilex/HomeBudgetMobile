package edu.michaelszeler.homebudget.mobile.view.listener

import android.view.View
import androidx.fragment.app.FragmentManager
import edu.michaelszeler.homebudget.mobile.view.dialog.StrategyDialogFragment

class ShowStrategyChartOnClickListener(private var fragmentManager: FragmentManager?, private var goal : Double, private var months: Int, private var currentMonth: Int) : View.OnClickListener {

    override fun onClick(v: View?) {
        StrategyDialogFragment.newInstance(goal, months, currentMonth).show(fragmentManager!!, StrategyDialogFragment.NAME)
    }
}