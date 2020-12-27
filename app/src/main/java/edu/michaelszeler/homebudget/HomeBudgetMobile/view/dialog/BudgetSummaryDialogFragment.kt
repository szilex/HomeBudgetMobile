package edu.michaelszeler.homebudget.HomeBudgetMobile.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget.BudgetSummaryEntry
import kotlinx.android.synthetic.main.dialog_fragment_budget_summary.view.*
import java.math.BigDecimal

class BudgetSummaryDialogFragment : DialogFragment() {

    companion object {

        const val NAME = "Budget summary chart"

        private const val KEY_INCOME = "income"
        private const val KEY_CUSTOM_EXPENSES = "custom expenses"
        private const val KEY_REGULAR_EXPENSES = "regular expenses"
        private const val KEY_STRATEGIES = "strategies"

        fun newInstance(budget: BudgetSummaryEntry) : BudgetSummaryDialogFragment {
            val args = Bundle()
            args.putSerializable(KEY_INCOME, budget.income)
            args.putSerializable(KEY_CUSTOM_EXPENSES, budget.customExpenses)
            args.putSerializable(KEY_REGULAR_EXPENSES, budget.regularExpenses)
            args.putSerializable(KEY_STRATEGIES, budget.strategies)
            val dialog = BudgetSummaryDialogFragment()
            dialog.arguments = args
            dialog.onSaveInstanceState(args)
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_budget_summary, container, false)

        val income = arguments?.getSerializable(KEY_INCOME) as BigDecimal
        val customExpenses = arguments?.getSerializable(KEY_CUSTOM_EXPENSES) as BigDecimal
        val regularExpenses = arguments?.getSerializable(KEY_REGULAR_EXPENSES) as BigDecimal
        val strategies = arguments?.getSerializable(KEY_STRATEGIES) as BigDecimal

        val sum = customExpenses.plus(regularExpenses).plus(strategies)
        val angles = listOf(customExpenses, regularExpenses, strategies)
        val labels = listOf(KEY_CUSTOM_EXPENSES, KEY_REGULAR_EXPENSES, KEY_STRATEGIES)
        val colors = listOf(R.color.red, R.color.purple_200, R.color.teal_700)

        val pieEntries = ArrayList<PieEntry>()
        for (i in 0..2) {
            pieEntries.add(PieEntry(angles[i].setScale(2, BigDecimal.ROUND_HALF_UP).toFloat(), labels[i]))
        }
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.setColors(colors.toIntArray(), context)

        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(16F)

        val pieChart = view.findViewById<PieChart>(R.id.chart_dialog_budget_summary)
        pieChart.data = pieData
        pieChart.invalidate()
        pieChart.centerText = String.format("Income: %10.2f\nSpent: %10.2f", income.setScale(2, BigDecimal.ROUND_HALF_UP).toFloat(), sum.setScale(2, BigDecimal.ROUND_HALF_UP).toFloat())
        pieChart.setCenterTextSize(16F)
        pieChart.setDrawEntryLabels(false)
        pieChart.contentDescription= ""
        pieChart.description.isEnabled = false
        pieChart.setEntryLabelTextSize(12F)
        pieChart.holeRadius = 60F
        pieChart.animateXY(1000, 1000)

        val legend = pieChart.legend
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 12F
        legend.formSize = 20F
        legend.formToTextSpace = 2F
        legend.yEntrySpace = 10F
        legend.isWordWrapEnabled = true
        legend.setDrawInside(false)
        legend.isEnabled = true

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.button_dialog_budget_summary_close.setOnClickListener {
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