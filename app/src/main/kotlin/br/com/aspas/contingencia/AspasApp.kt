package br.com.aspas.contingencia
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import br.com.aspas.contingencia.data.projection.ProjectionEngine
import br.com.aspas.contingencia.print.PrintScheduler
import br.com.aspas.contingencia.session.SessionHolder
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
@HiltAndroidApp
class AspasApp : Application(), Configuration.Provider {
@Inject lateinit var workerFactory: HiltWorkerFactory
@Inject lateinit var projectionEngine: ProjectionEngine
@Inject lateinit var sessionManager: SessionManager
@Inject lateinit var printScheduler: PrintScheduler
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
override val workManagerConfiguration: Configuration
get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
override fun onCreate() {
super.onCreate()
if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
SessionHolder.session = sessionManager
projectionEngine.start()
printScheduler.start()
}
}
