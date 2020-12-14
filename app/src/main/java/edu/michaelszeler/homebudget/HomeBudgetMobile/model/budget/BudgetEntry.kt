package edu.michaelszeler.homebudget.HomeBudgetMobile.model.budget

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.CustomExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.RegularExpenseEntry
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.strategy.StrategyEntry
import java.math.BigDecimal
import java.util.*

class BudgetEntry (val id: Int, val date: Date, val income: BigDecimal, val customExpenses: List<CustomExpenseEntry>, val regularExpenses: List<RegularExpenseEntry>, val strategies: List<StrategyEntry>) {
    companion object {
        fun convertToBudget(content: String): BudgetEntry {
            val gson = Gson()
            val productType = object : TypeToken<BudgetEntry>() {}.type
            return gson.fromJson(content, productType)
        }

        fun convertToBudgetList(content: String): List<BudgetEntry> {
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<BudgetEntry>>() {}.type
            return gson.fromJson(content, productListType)
        }
    }

    override fun toString(): String {
        val builder: StringBuilder = java.lang.StringBuilder()
        builder.append("BudgetEntry(id=$id, date=$date, income=$income, customExpenses=[")
        for (customExpense in customExpenses) {
            builder.append(customExpense.name)
            builder.append(", ")
        }
        builder.append("], regularExpenses=[")
        for (regularExpense in regularExpenses) {
            builder.append(regularExpense.name)
            builder.append(", ")
        }
        builder.append("], strategies=[")
        for (strategy in strategies) {
            builder.append(strategy.name)
            builder.append(", ")
        }
        builder.append("])")
        return builder.toString()
    }


}