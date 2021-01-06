package edu.michaelszeler.homebudget.mobile.utils.navigation

import androidx.fragment.app.Fragment

interface NavigationHost {

    fun navigateTo(fragment: Fragment, addToBackstack: Boolean)
}
