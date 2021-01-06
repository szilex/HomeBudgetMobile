package edu.michaelszeler.homebudget.mobile.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import edu.michaelszeler.homebudget.mobile.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.dialog_fragment_strategy.view.*

class StrategyDialogFragment : DialogFragment() {

    companion object {

        const val NAME = "Strategy chart"

        private const val KEY_GOAL = "goal"
        private const val KEY_MOTHS = "months"
        private const val KEY_CURRENT_MONTH = "current_month"

        fun newInstance(goal: Double, months: Int, currentMonth: Int): StrategyDialogFragment {
            val args = Bundle()
            args.putDouble(KEY_GOAL, goal)
            args.putInt(KEY_MOTHS, months)
            args.putInt(KEY_CURRENT_MONTH, currentMonth)
            val dialog = StrategyDialogFragment()
            dialog.arguments = args
            dialog.onSaveInstanceState(args)
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_strategy, container, false)

        val goal = arguments?.get("goal") as Double
        val months = arguments?.get("months") as Int
        val currentMonth = arguments?.get("current_month") as Int
        val step = goal.div(months)
        var currentValue = step
        var values = ArrayList<BarEntry>()
        var barEntry : BarEntry
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()
        var hasPrevious = false
        var hasCurrent = false
        var hasFollowing = false

        for (i in 1..months) {
            barEntry = BarEntry(i.toFloat(), currentValue.toFloat())
            values.add(barEntry)
            currentValue = currentValue.plus(step)
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

        val barDataSet = BarDataSet(values, "Saved money")
        barDataSet.setColors(colors.toIntArray(), context)
        barDataSet.stackLabels = labels.toTypedArray()

        val barChart = view.findViewById<BarChart>(R.id.chart_dialog_strategy)
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

        view.button_dialog_strategy_close.setOnClickListener {
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