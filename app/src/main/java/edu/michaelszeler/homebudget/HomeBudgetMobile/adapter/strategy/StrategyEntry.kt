package edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy

import android.content.res.Resources
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.math.BigDecimal
import java.util.*

class StrategyEntry(val name: String, val description: String, val category: String, val goal: BigDecimal, val startDate: Date, val months: Int) {

    companion object {

        fun convertToStrategyList(resources: Resources): List<StrategyEntry> {
            val inputStream = resources.openRawResource(R.raw.products)
            val jsonProductsString = inputStream.bufferedReader().use(BufferedReader::readText)
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<StrategyEntry>>() {}.type
            return gson.fromJson(jsonProductsString, productListType)
        }

        fun convertToStrategyList(content: String): List<StrategyEntry> {
            val gson = Gson()
            val productListType = object : TypeToken<ArrayList<StrategyEntry>>() {}.type
            return gson.fromJson(content, productListType)
        }
    }
}