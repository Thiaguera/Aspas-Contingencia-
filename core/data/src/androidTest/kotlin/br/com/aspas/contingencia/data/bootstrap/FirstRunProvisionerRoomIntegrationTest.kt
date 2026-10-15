package br.com.aspas.contingencia.data.bootstrap

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.aspas.contingencia.data.catalog.CatalogFile
import br.com.aspas.contingencia.data.catalog.CatalogImporter
import br.com.aspas.contingencia.data.catalog.CategoryJson
import br.com.aspas.contingencia.data.catalog.ItemJson
import br.com.aspas.contingencia.data.catalog.PrinterJson
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.TableEntity
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Integration coverage for safe, repeatable first-run provisioning. */
class FirstRunProvisionerRoomIntegrationTest {
    private lateinit var db: AspasDatabase
    private lateinit var importer: CatalogImporter
    private lateinit var provisioner: FirstRunProvisioner

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AspasDatabase::class.java
        ).allowMainThreadQueries().build()
        val json = Json { encodeDefaults = true }
        importer = CatalogImporter(db, json)
        provisioner = FirstRunProvisioner(ApplicationProvider.getApplicationContext(), db, importer)
    }

    @After fun tearDown() = db.close()

    @Test fun initialCatalogImport_isIdempotentAndNeverOverwritesAvailabilityOrPrinterIp() = runBlocking {
        importer.importFile(catalog())
        val initialItem = db.catalogDao().getItem("coffee")!!
        db.catalogDao().upsertItem(initialItem.copy(available = false))
        val initialPrinter = db.printerDao().getById("bar")!!
        db.printerDao().upsert(initialPrinter.copy(ip = "10.0.0.44", enabled = false, lastStatus = "offline"))

        importer.importFile(catalog().copy(
            items = catalog().items.map { it.copy(name = "Changed remotely") },
            printers = catalog().printers.map { it.copy(ip = "192.168.9.9") }
        ))

        assertFalse(db.catalogDao().getItem("coffee")!!.available)
        assertEquals("Café", db.catalogDao().getItem("coffee")!!.name)
        db.printerDao().getById("bar")!!.also { printer ->
            assertEquals("10.0.0.44", printer.ip)
            assertFalse(printer.enabled)
            assertEquals("offline", printer.lastStatus)
        }
    }

    @Test fun provision_createsOnlyMissingNumbersAndLeavesExistingTablesUntouched() = runBlocking {
        importer.importFile(catalog())
        val existing = TableEntity("configured-table", 2, "Área externa", "open", 10L, null, "lease-1", 12.0)
        db.tableDao().upsert(existing)

        val first = provisioner.provision(1..3)
        val second = provisioner.provision(1..3)

        assertEquals(2, first.tablesCreated)
        assertEquals(0, second.tablesCreated)
        assertEquals(listOf(1, 2, 3), db.tableDao().listAll().map { it.number })
        assertEquals(existing, db.tableDao().getById("configured-table"))
        assertTrue(provisioner.snapshot().isReady)
    }

    private fun catalog() = CatalogFile(
        version = 1,
        categories = listOf(CategoryJson("drinks", "Bebidas", "bar", 1)),
        items = listOf(ItemJson("coffee", "Café", "drinks", 8.0, print_destinations = listOf("bar"))),
        printers = listOf(PrinterJson("bar", "Bar", "192.168.1.10", 9100, "BAR"))
    )
}
