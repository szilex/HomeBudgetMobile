package edu.michaelszeler.homebudget.mobile.view.listener

import android.view.View
import androidx.fragment.app.FragmentManager
import edu.michaelszeler.homebudget.mobile.view.dialog.RegularExpenseDialogFragment

class ShowRegularExpenseChartOnClickListener(private var fragmentManager: FragmentManager?, private var goal : Double, private var months: Int, private var currentMonth: Int) : View.OnClickListener {

    override fun onClick(v: View?) {
        RegularExpenseDialogFragment.newInstance(goal, months, currentMonth).show(fragmentManager!!, RegularExpenseDialogFragment.NAME)
    }
}