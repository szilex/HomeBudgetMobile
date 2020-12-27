package edu.michaelszeler.homebudget.HomeBudgetMobile.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import kotlinx.android.synthetic.main.dialog_fragment_regular_expense.view.*

class RegularExpenseDialogFragment private constructor() : DialogFragment() {

    companion object {

        const val NAME = "Regular expense chart"

        private const val KEY_AMOUNT = "amount"
        private const val KEY_MONTHS = "months"
        private const val KEY_CURRENT_MONTH = "current_month"

        fun newInstance(amount: Double, months: Int, currentMonth: Int): RegularExpenseDialogFragment {
            val args = Bundle()
            args.putDouble(KEY_AMOUNT, amount)
            args.putInt(KEY_MONTHS, months)
            args.putInt(KEY_CURRENT_MONTH, currentMonth)
            val dialog = RegularExpenseDialogFragment()
            dialog.arguments = args
            dialog.onSaveInstanceState(args)
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_regular_expense, container, false)

        val amount = arguments?.get("amount") as Double
        val months = arguments?.get("months") as Int
        val currentMonth = arguments?.get("current_month") as Int
        var values = ArrayList<BarEntry>()
        var barEntry : BarEntry
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()
        var hasPrevious = false
        var hasCurrent = false
        var hasFollowing = false

        for (i in 1..months) {
            barEntry = BarEntry(i.toFloat(), amount.toFloat())
            values.add(barEntry)
            labels.add(i.toString())
            when {
                i == currentMonth -> {
                    colors.add(R.color.red)
                    hasCurrent = true
                }
                i < currentMonth -> {
                    colors.add(R.color.purple_200)
                    hasPrevious = true
                }
                else -> {
                    colors.add(R.color.teal_700)
                    hasFollowing = true
                }
            }
        }

        val barDataSet = BarDataSet(values, "Spent money")
        barDataSet.setColors(colors.toIntArray(), context)
        barDataSet.stackLabels = labels.toTypedArray()

        val barChart = view.findViewById<BarChart>(R.id.chart_dialog_regular_expense)
        barChart.data = BarData(barDataSet)
        barChart.description.isEnabled = false

        val legend = barChart.legend
        val legendEntries = ArrayList<LegendEntry>()
        if (hasPrevious) {
            legendEntries.add(LegendEntry("Previous months", Legend.LegendForm.SQUARE, 10f, 2f, null, ResourcesCompat.getColor(resources, R.color.purple_200, null)))
        }
        if (hasCurrent) {
            legendEntries.add(LegendEntry("Current month", Legend.LegendForm.SQUARE, 10f, 2f, null, ResourcesCompat.getColor(resources, R.color.red, null)))
        }
        if (hasFollowing) {
            legendEntries.add(LegendEntry("Following months", Legend.LegendForm.SQUARE, 10f, 2f, null, ResourcesCompat.getColor(resources, R.color.teal_700, null)))
        }
        legend.yEntrySpace = 10f
        legend.isWordWrapEnabled = true
        legend.setCustom(legendEntries)
        legend.setDrawInside(false)
        legend.isEnabled = true
        barChart.invalidate()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.button_dialog_regular_expense_close.setOnClickListener {
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}