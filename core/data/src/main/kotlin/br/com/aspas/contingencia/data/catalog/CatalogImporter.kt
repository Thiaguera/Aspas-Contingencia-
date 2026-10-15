package br.com.aspas.contingencia.data.catalog

import android.content.Context
import androidx.room.withTransaction
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.CatalogCategoryEntity
import br.com.aspas.contingencia.data.db.entity.CatalogItemEntity
import br.com.aspas.contingencia.data.db.entity.PrinterEntity
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Serializable data class CatalogFile(val version: Int, val categories: List<CategoryJson>,
    val items: List<ItemJson>, val printers: List<PrinterJson>)
@Serializable data class CategoryJson(val id: String, val name: String,
    val print_destination: String, val sort_order: Int)
@Serializable data class ItemJson(val id: String, val name: String, val category_id: String,
    val price: Double, val pricing_mode: String = "fixed", val price_half: Double? = null,
    val weight_max_g: Int? = null, val weight_tiers_json: String? = null,
    val parrilla_bundle_json: String? = null, val print_destinations: List<String>,
    val modifiers: List<String> = emptyList(), val tags: List<String> = emptyList(),
    val description: String? = null, val origin: String? = null, val serves: Int? = null,
    val sort_order: Int = 0)
@Serializable data class PrinterJson(val id: String, val name: String, val ip: String,
    val port: Int = 9100, val header_label: String, val group_id: String? = null)

/** Imports bundled catalog data without changing operating configuration by default. */
@Singleton
class CatalogImporter @Inject constructor(
    private val db: AspasDatabase,
    private val json: Json
) {
    suspend fun importFromAssets(ctx: Context, assetName: String = "catalog.json", replace: Boolean = false) {
        val raw = ctx.assets.open(assetName).bufferedReader().use { it.readText() }
        importFile(json.decodeFromString<CatalogFile>(raw), replace)
    }

    /**
     * The default is create-only: existing catalog records, availability choices and printers
     * are left untouched. [replace] remains for an explicit definition refresh, but is still
     * non-destructive: it does not delete records and always retains availability choices.
     */
    suspend fun importFile(file: CatalogFile, replace: Boolean = false) = db.withTransaction {
        val catalog = db.catalogDao()
        val existingCategories = catalog.observeCategories().first().associateBy { it.categoryId }
        file.categories.forEach { source ->
            if (replace || source.id !in existingCategories) {
                catalog.upsertCategory(CatalogCategoryEntity(source.id, source.name, source.print_destination, source.sort_order))
            }
        }
        file.items.forEach { source ->
            val existing = catalog.getItem(source.id)
            if (existing == null || replace) {
                // A refresh updates supplied catalog fields but never turns an operator-disabled item back on.
                catalog.upsertItem(source.toEntity(json).copy(available = existing?.available ?: true))
            }
        }
        file.printers.forEach { source ->
            // IP, port, enabled state and health are operational configuration: never replace them.
            if (db.printerDao().getById(source.id) == null) {
                db.printerDao().upsert(PrinterEntity(source.id, source.name, source.ip, source.port,
                    source.header_label, source.group_id, true, null, "unknown"))
            }
        }
        Timber.i("catalog: ${file.items.size} itens processados (replace=$replace)")
    }
}

private fun ItemJson.toEntity(json: Json) = CatalogItemEntity(
    itemId = id, name = name, categoryId = category_id, price = price,
    pricingMode = pricing_mode, priceHalf = price_half, weightMaxG = weight_max_g,
    weightTiersJson = weight_tiers_json, parrillaBundleJson = parrilla_bundle_json,
    printDestinationsJson = json.encodeToString(print_destinations),
    modifiersJson = if (modifiers.isEmpty()) null else json.encodeToString(modifiers),
    tagsJson = if (tags.isEmpty()) null else json.encodeToString(tags),
    description = description, origin = origin, serves = serves,
    available = true, sortOrder = sort_order)
