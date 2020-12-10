package edu.michaelszeler.homebudget.HomeBudgetMobile.navigation

import androidx.fragment.app.Fragment

interface NavigationHost {

    fun navigateTo(fragment: Fragment, addToBackstack: Boolean)
}
