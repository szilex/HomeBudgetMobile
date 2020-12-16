package edu.michaelszeler.homebudget.HomeBudgetMobile.model.strategy

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.*

class StrategyEntry(val id: Int, val name: String, val description: String, val category: String, val goal: BigDecimal, val startDate: Date, val months: Int) {

    companion object {

        fun convertToStrategyList(content: String): List<StrategyEntry> {
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<StrategyEntry>>() {}.type
            return gson.fromJson(content, productListType)
        }
    }
}