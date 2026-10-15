package br.com.aspas.contingencia.data.bootstrap

import android.content.Context
import androidx.room.withTransaction
import br.com.aspas.contingencia.data.catalog.CatalogImporter
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.TableEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProvisioningProgress {
    data object Idle : ProvisioningProgress
    data object LoadingCatalog : ProvisioningProgress
    data object CreatingTables : ProvisioningProgress
    data class Complete(val catalogAlreadyPresent: Boolean, val tablesCreated: Int) : ProvisioningProgress
    data class Failed(val message: String) : ProvisioningProgress
}

data class ProvisioningSnapshot(
    val catalogPresent: Boolean,
    val tablesPresent: Boolean
) {
    val isReady: Boolean get() = catalogPresent && tablesPresent
}

/** Safe, repeatable first-run setup. It only creates missing catalog records and table numbers. */
@Singleton
class FirstRunProvisioner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AspasDatabase,
    private val catalogImporter: CatalogImporter
) {
    suspend fun snapshot(): ProvisioningSnapshot = db.withTransaction {
        ProvisioningSnapshot(
            // A fully disabled catalog is still configured; do not re-import over it.
            catalogPresent = db.catalogDao().observeCategories().first().isNotEmpty(),
            tablesPresent = db.tableDao().listAll().isNotEmpty()
        )
    }

    suspend fun initializeCatalogIfMissing(onProgress: (ProvisioningProgress) -> Unit = {}) {
        val catalogPresent = db.catalogDao().observeCategories().first().isNotEmpty()
        if (!catalogPresent) {
            onProgress(ProvisioningProgress.LoadingCatalog)
            // The importer is transactional and create-only unless an explicit refresh requests otherwise.
            catalogImporter.importFromAssets(context, replace = false)
        }
    }

    /**
     * Creates only numbers that do not exist. The caller must select the proposed range; no
     * restaurant-specific table layout is assumed by this service.
     */
    suspend fun provision(tableNumbers: IntRange, onProgress: (ProvisioningProgress) -> Unit = {}): ProvisioningProgress.Complete {
        require(!tableNumbers.isEmpty()) { "Informe ao menos um número de mesa." }
        val catalogAlreadyPresent = db.catalogDao().observeCategories().first().isNotEmpty()
        initializeCatalogIfMissing(onProgress)
        onProgress(ProvisioningProgress.CreatingTables)
        val created = db.withTransaction {
            val occupied = db.tableDao().listAll().mapTo(mutableSetOf()) { it.number }
            var createdCount = 0
            for (number in tableNumbers) {
                if (!occupied.add(number)) continue
                db.tableDao().upsert(TableEntity(
                    tableId = UUID.randomUUID().toString(),
                    number = number,
                    label = "Mesa $number",
                    status = "free",
                    openedAt = null,
                    closedAt = null,
                    currentLeaseId = null
                ))
                createdCount++
            }
            createdCount
        }
        return ProvisioningProgress.Complete(
            catalogAlreadyPresent = catalogAlreadyPresent,
            tablesCreated = created
        ).also(onProgress)
    }
}
