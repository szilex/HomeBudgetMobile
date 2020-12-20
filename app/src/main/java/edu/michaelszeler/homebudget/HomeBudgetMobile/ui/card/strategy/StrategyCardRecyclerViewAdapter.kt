package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.card.strategy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.strategy.StrategyEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.listener.DeleteStrategyOnClickListener
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.listener.ShowStrategyChartOnClickListener
import java.util.*

class StrategyCardRecyclerViewAdapter(private val strategyList: List<StrategyEntry>, private val fragmentManager: FragmentManager?) : RecyclerView.Adapter<StrategyCardViewHolder>() {

    private lateinit var deleteStrategyOnClickListener: DeleteStrategyOnClickListener

    fun setDeleteStrategyOnClickListener (listener: DeleteStrategyOnClickListener) {
        this.deleteStrategyOnClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrategyCardViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.card_strategy, parent, false)
        return StrategyCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: StrategyCardViewHolder, position: Int) {
        if (position < strategyList.size) {
            val product = strategyList[position]
            val startCalendar = Calendar.getInstance()
            startCalendar.time = product.startDate
            holder.strategyName.text = product.name
            holder.strategyDescription.text = product.description
            holder.strategyCategory.text = product.category
            holder.strategyGoal.text = product.goal.toString()
            holder.strategyStartDate.text = String.format("%d-%d-%d", startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH))
            holder.strategyMonths.text = product.months.toString()
            holder.strategyButton.setOnClickListener(ShowStrategyChartOnClickListener(fragmentManager!!, product.goal.toDouble(), product.months, calculateCurrentMonth(startCalendar)))
            if (this::deleteStrategyOnClickListener.isInitialized) {
                holder.strategyDeleteButton.setOnClickListener {
                    deleteStrategyOnClickListener.setStrategy(strategyList[position])
                    deleteStrategyOnClickListener.onClick(it)
                }
            } else {
                holder.strategyDeleteButton.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return strategyList.size
    }

    private fun calculateCurrentMonth(startCalendar: Calendar) : Int {
        var i = 1
        val currentCalendar = Calendar.getInstance()
        while (!(currentCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR) && currentCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH))) {
            i++
            startCalendar.add(Calendar.MONTH, 1)
        }
        return i
    }

}