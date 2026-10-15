package br.com.aspas.contingencia.di
import br.com.aspas.contingencia.data.repository.*
import br.com.aspas.contingencia.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
@Binds @Singleton abstract fun eventRepository(i: EventRepositoryImpl): EventRepository
@Binds @Singleton abstract fun tableRepository(i: TableRepositoryImpl): TableRepository
@Binds @Singleton abstract fun orderRepository(i: OrderRepositoryImpl): OrderRepository
@Binds @Singleton abstract fun printerRepository(i: PrinterRepositoryImpl): PrinterRepository
@Binds @Singleton abstract fun catalogRepository(i: CatalogRepositoryImpl): CatalogRepository
@Binds @Singleton abstract fun userRepository(i: UserRepositoryImpl): UserRepository
@Binds @Singleton abstract fun syncStateRepository(i: SyncStateRepositoryImpl): SyncStateRepository
}
