// SharedPreferencesHelper.kt

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("schedule_viewer_prefs", Context.MODE_PRIVATE)

    var token: String?
        get() = sharedPreferences.getString("token", null)
        set(value) {
            sharedPreferences.edit().putString("token", value).apply()
        }

    fun clearToken() {
        sharedPreferences.edit().remove("token").apply()
    }
}
