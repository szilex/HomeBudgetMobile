package edu.michaelszeler.homebudget.HomeBudgetMobile.session

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.widget.Toast
import edu.michaelszeler.homebudget.HomeBudgetMobile.view.fragment.user.LoginFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost


class SessionManager(context: Context?) {
    var preferences: SharedPreferences? = null
    var editor: Editor? = null

    private var _context: Context? = context
    private var _privateMode = 0

    private val PREF_NAME = "AndroidPreferences"
    private val IS_LOGIN = "IsLoggedIn"
    private val KEY_LOGIN = "login"
    private val KEY_PASSWORD = "password"
    private val KEY_TOKEN = "token"

    init {
        preferences = _context?.getSharedPreferences(PREF_NAME, _privateMode)
        editor = preferences!!.edit()
    }

    fun createSession(username: String, password: String, token: String) {
        editor?.putBoolean(IS_LOGIN, true)
        editor?.putString(KEY_LOGIN, username)
        editor?.putString(KEY_PASSWORD, password)
        editor?.putString(KEY_TOKEN, token)
        editor?.commit()
    }

    fun getUserDetails(): HashMap<String, String?>? {
        val user = HashMap<String, String?>()
        user[KEY_LOGIN] = preferences!!.getString(KEY_LOGIN, null)
        user[KEY_PASSWORD] = preferences!!.getString(KEY_PASSWORD, null)
        return user
    }

    fun getToken(): String? {
        return preferences!!.getString(KEY_TOKEN, null)
    }

    fun checkLogin() {
        if (!isLoggedIn()!!) {
            Toast.makeText(_context, "User is not logged in!", Toast.LENGTH_SHORT)
        }
    }

    fun logoutUser(navigationHost: NavigationHost) {
        editor!!.clear()
        editor!!.commit()
        navigationHost.navigateTo(LoginFragment(), false)
    }

    private fun isLoggedIn(): Boolean? {
        return preferences?.getBoolean(IS_LOGIN, false)
    }

}