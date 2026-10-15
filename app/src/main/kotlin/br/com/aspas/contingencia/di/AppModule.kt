package br.com.aspas.contingencia.di
import android.content.Context
import br.com.aspas.contingencia.common.HlcGenerator
import br.com.aspas.contingencia.data.repository.SessionProvider
import br.com.aspas.contingencia.session.SessionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton
import javax.inject.Named
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
@Provides @Singleton
@Named("deviceId")
fun provideDeviceId(@ApplicationContext ctx: Context): String {
val p = ctx.getSharedPreferences("device", Context.MODE_PRIVATE)
return p.getString("deviceId", null)
?: UUID.randomUUID().toString().also { p.edit().putString("deviceId", it).apply() }
}
@Provides @Singleton
@Named("deviceName")
fun provideDeviceName(@ApplicationContext ctx: Context): String {
val p = ctx.getSharedPreferences("device", Context.MODE_PRIVATE)
return p.getString("deviceName", null)
?: android.os.Build.MODEL.also { p.edit().putString("deviceName", it).apply() }
}
@Provides @Singleton
fun provideHlcGenerator(@Named("deviceId") deviceId: String): HlcGenerator = HlcGenerator(deviceId.takeLast(4))
}
@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
@Binds @Singleton
abstract fun sessionProvider(impl: SessionManager): SessionProvider
}
