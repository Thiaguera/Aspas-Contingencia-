package br.com.aspas.contingencia.di
import br.com.aspas.contingencia.core.lease.LeaseManagerImpl
import br.com.aspas.contingencia.domain.service.*
import br.com.aspas.contingencia.print.PrintDispatcherImpl
import br.com.aspas.contingencia.sync.SyncEngineImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
@Binds @Singleton abstract fun leaseManager(i: LeaseManagerImpl): LeaseManager
@Binds @Singleton abstract fun printDispatcher(i: PrintDispatcherImpl): PrintDispatcher
@Binds @Singleton abstract fun syncEngine(i: SyncEngineImpl): SyncEngine
}
