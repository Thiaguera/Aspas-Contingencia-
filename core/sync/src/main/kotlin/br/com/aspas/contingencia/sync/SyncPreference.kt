package br.com.aspas.contingencia.sync
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SyncPreference @Inject constructor(@ApplicationContext ctx: Context) {
private val prefs = ctx.getSharedPreferences("sync", Context.MODE_PRIVATE)
var enabled: Boolean
get() = prefs.getBoolean("enabled", false)
set(v) = prefs.edit().putBoolean("enabled", v).apply()
var deviceName: String
get() = prefs.getString("name", android.os.Build.MODEL) ?: "aspas"
set(v) = prefs.edit().putString("name", v).apply()
}
