package br.com.aspas.contingencia.data.catalog
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import android.content.Context
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.CatalogCategoryEntity
import br.com.aspas.contingencia.data.db.entity.CatalogItemEntity
import br.com.aspas.contingencia.data.db.entity.PrinterEntity
import kotlinx.serialization.Serializable
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
@Singleton
class CatalogImporter @Inject constructor(
private val db: AspasDatabase, private val json: Json
) {
suspend fun importFromAssets(ctx: Context, assetName: String = "catalog.json") {
val raw = ctx.assets.open(assetName).bufferedReader().use { it.readText() }
val file = json.decodeFromString<CatalogFile>(raw)
importFile(file)
}
suspend fun importFile(file: CatalogFile, replace: Boolean = false) {
if (replace) {
db.catalogDao().wipeCategories(); db.catalogDao().wipeItems()
}
db.catalogDao().upsertCategories(file.categories.map {
CatalogCategoryEntity(it.id, it.name, it.print_destination, it.sort_order) })
file.items.forEach { itemJson ->
val existing = db.catalogDao().getItem(itemJson.id)
val entity = itemJson.toEntity(json).copy(available = existing?.available ?: true)
db.catalogDao().upsertItem(entity)
}
db.printerDao().upsertAll(file.printers.map {
PrinterEntity(it.id, it.name, it.ip, it.port, it.header_label,
it.group_id, true, null, "unknown") })
Timber.i("catalog: ${file.items.size} itens")
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
