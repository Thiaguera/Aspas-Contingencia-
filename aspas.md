ASPAS Contingência — Projeto completo em arquivo único
Como usar:
1. Copie este arquivo inteiro para um arquivo local chamado aspas.md.
2. Crie um projeto Android vazio no Android Studio (Empty Activity, Kotlin, minSdk 26, package br.com.aspas.contingencia).
3. Cada bloco abaixo está marcado com ### caminho/do/arquivo. Crie o arquivo nesse caminho e cole o conteúdo.
4. Ao terminar, rode ./gradlew :app:assembleDebug.
5. Os erros vão aparecer. Cole num agente de IA (Cursor, Claude Code, Copilot) e peça para corrigir iterativamente.
Aviso: este projeto nunca foi compilado. A primeira compilação vai falhar com dezenas de erros. É esperado. Correção iterativa é parte do trabalho.
---
Estrutura de módulos

aspas-contingencia/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
├── core/common/
├── core/domain/
├── core/data/
├── core/print/
├── core/sync/
└── app/
---
============ BUILD FILES ============
settings.gradle.kts
pluginManagement {
repositories {
google()
mavenCentral()
gradlePluginPortal()
}
}
dependencyResolutionManagement {
repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
repositories {
google()
mavenCentral()
}
}
rootProject.name = "aspas-contingencia"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:print")
include(":core:sync")
build.gradle.kts
plugins {
alias(libs.plugins.android.application) apply false
alias(libs.plugins.android.library) apply false
alias(libs.plugins.kotlin.android) apply false
alias(libs.plugins.kotlin.jvm) apply false
alias(libs.plugins.kotlin.serialization) apply false
alias(libs.plugins.compose.compiler) apply false
alias(libs.plugins.ksp) apply false
alias(libs.plugins.hilt) apply false
}
gradle.properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
android.nonTransitiveRClass=true
android.defaults.buildfeatures.buildconfig=true
kotlin.code.style=official
gradle/libs.versions.toml
[versions]
agp = "8.7.2"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"
hilt = "2.52"
compose-bom = "2024.10.01"
room = "2.6.1"
sqlcipher = "4.6.1"
sqlite = "2.4.0"
coroutines = "1.9.0"
ktor = "2.3.12"
jmdns = "3.5.9"
serialization = "1.7.3"
security-crypto = "1.1.0-alpha06"
work = "2.9.1"
hilt-work = "1.2.0"
timber = "5.0.1"
lifecycle-process = "2.8.7"
material-icons-extended = "1.7.5"
[libraries]
androidx-core-ktx = { module = "androidx.core:core-ktx", version = "1.13.1" }
androidx-lifecycle-runtime-ktx = { module = "androidx.lifecycle:lifecycle-runtime-ktx", version = "2.8.7" }
androidx-lifecycle-process = { module = "androidx.lifecycle:lifecycle-process", version.ref = "lifecycle-process" }
androidx-activity-compose = { module = "androidx.activity:activity-compose", version = "1.9.3" }
compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose-bom" }
compose-ui = { module = "androidx.compose.ui:ui" }
compose-ui-graphics = { module = "androidx.compose.ui:ui-graphics" }
compose-ui-tooling = { module = "androidx.compose.ui:ui-tooling" }
compose-ui-tooling-preview = { module = "androidx.compose.ui:ui-tooling-preview" }
compose-material3 = { module = "androidx.compose.material3:material3" }
compose-material-icons-extended = { module = "androidx.compose.material:material-icons-extended", version.ref = "material-icons-extended" }
compose-navigation = { module = "androidx.navigation:navigation-compose", version = "2.8.4" }
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
sqlcipher-android = { module = "net.zetetic:android-database-sqlcipher", version.ref = "sqlcipher" }
androidx-sqlite = { module = "androidx.sqlite:sqlite-ktx", version.ref = "sqlite" }
coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }
ktor-server-core = { module = "io.ktor:ktor-server-core", version.ref = "ktor" }
ktor-server-cio = { module = "io.ktor:ktor-server-cio", version.ref = "ktor" }
ktor-server-content-negotiation = { module = "io.ktor:ktor-server-content-negotiation", version.ref = "ktor" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-cio = { module = "io.ktor:ktor-client-cio", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
jmdns = { module = "org.jmdns:jmdns", version.ref = "jmdns" }
serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "serialization" }
security-crypto = { module = "androidx.security:security-crypto", version.ref = "security-crypto" }
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version = "1.2.0" }
work-runtime = { module = "androidx.work:work-runtime-ktx", version.ref = "work" }
hilt-work = { module = "androidx.hilt:hilt-work", version.ref = "hilt-work" }
hilt-work-compiler = { module = "androidx.hilt:hilt-compiler", version.ref = "hilt-work" }
timber = { module = "com.jakewharton.timber:timber", version.ref = "timber" }
[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
core/common/build.gradle.kts
plugins {
alias(libs.plugins.android.library)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.serialization)
}
android {
namespace = "br.com.aspas.contingencia.common"
compileSdk = 35
defaultConfig { minSdk = 26 }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
}
dependencies {
implementation(libs.coroutines.core)
implementation(libs.serialization.json)
}
core/domain/build.gradle.kts
plugins {
alias(libs.plugins.android.library)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.serialization)
}
android {
namespace = "br.com.aspas.contingencia.domain"
compileSdk = 35
defaultConfig { minSdk = 26 }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
}
dependencies {
api(project(":core:common"))
implementation(libs.coroutines.core)
implementation(libs.serialization.json)
}
core/data/build.gradle.kts
plugins {
alias(libs.plugins.android.library)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.serialization)
alias(libs.plugins.ksp)
alias(libs.plugins.hilt)
}
android {
namespace = "br.com.aspas.contingencia.data"
compileSdk = 35
defaultConfig { minSdk = 26 }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
}
dependencies {
api(project(":core:domain"))
implementation(project(":core:common"))
implementation(libs.room.runtime)
implementation(libs.room.ktx)
ksp(libs.room.compiler)
implementation(libs.sqlcipher.android)
implementation(libs.androidx.sqlite)
implementation(libs.coroutines.core)
implementation(libs.coroutines.android)
implementation(libs.serialization.json)
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.security.crypto)
implementation(libs.timber)
}
core/print/build.gradle.kts
plugins {
alias(libs.plugins.android.library)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.serialization)
alias(libs.plugins.ksp)
alias(libs.plugins.hilt)
}
android {
namespace = "br.com.aspas.contingencia.print"
compileSdk = 35
defaultConfig { minSdk = 26 }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
}
dependencies {
api(project(":core:domain"))
implementation(project(":core:common"))
implementation(project(":core:data"))
implementation(libs.coroutines.core)
implementation(libs.coroutines.android)
implementation(libs.serialization.json)
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.timber)
}
core/sync/build.gradle.kts
plugins {
alias(libs.plugins.android.library)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.serialization)
alias(libs.plugins.ksp)
alias(libs.plugins.hilt)
}
android {
namespace = "br.com.aspas.contingencia.sync"
compileSdk = 35
defaultConfig { minSdk = 26 }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
}
dependencies {
api(project(":core:domain"))
implementation(project(":core:common"))
implementation(project(":core:data"))
implementation(libs.coroutines.core)
implementation(libs.coroutines.android)
implementation(libs.serialization.json)
implementation(libs.ktor.server.core)
implementation(libs.ktor.server.cio)
implementation(libs.ktor.server.content.negotiation)
implementation(libs.ktor.client.core)
implementation(libs.ktor.client.cio)
implementation(libs.ktor.client.content.negotiation)
implementation(libs.ktor.serialization.json)
implementation(libs.jmdns)
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.security.crypto)
implementation(libs.timber)
}
app/build.gradle.kts
plugins {
alias(libs.plugins.android.application)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.serialization)
alias(libs.plugins.compose.compiler)
alias(libs.plugins.ksp)
alias(libs.plugins.hilt)
}
android {
namespace = "br.com.aspas.contingencia"
compileSdk = 35
defaultConfig {
applicationId = "br.com.aspas.contingencia"
minSdk = 26
targetSdk = 35
versionCode = 1
versionName = "1.0.0"
testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
buildTypes {
debug { applicationIdSuffix = ".dev" }
release {
isMinifyEnabled = false
proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
}
}
buildFeatures { compose = true; buildConfig = true }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
packaging {
resources {
excludes += listOf(
"META-INF/DEPENDENCIES", "META-INF/LICENSE",
"META-INF/LICENSE.txt", "META-INF/NOTICE", "META-INF/NOTICE.txt",
"META-INF/*.kotlin_module"
)
}
}
}
dependencies {
implementation(project(":core:common"))
implementation(project(":core:domain"))
implementation(project(":core:data"))
implementation(project(":core:print"))
implementation(project(":core:sync"))
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.process)
implementation(libs.androidx.activity.compose)
implementation(platform(libs.compose.bom))
implementation(libs.compose.ui)
implementation(libs.compose.ui.graphics)
implementation(libs.compose.ui.tooling.preview)
implementation(libs.compose.material3)
implementation(libs.compose.material.icons.extended)
implementation(libs.compose.navigation)
implementation(libs.coroutines.core)
implementation(libs.coroutines.android)
implementation(libs.serialization.json)
implementation(libs.hilt.android)
implementation(libs.hilt.navigation.compose)
ksp(libs.hilt.compiler)
implementation(libs.work.runtime)
implementation(libs.hilt.work)
ksp(libs.hilt.work.compiler)
implementation(libs.security.crypto)
implementation(libs.timber)
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("androidx.test:core:1.6.1")
testImplementation("org.robolectric:robolectric:4.13")
androidTestImplementation(platform(libs.compose.bom))
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test:runner:1.6.2")
debugImplementation(libs.compose.ui.tooling)
debugImplementation("androidx.compose.ui:ui-test-manifest")
}
app/proguard-rules.pro
-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.
-keepclassmembers class kotlinx.serialization. { ; }
-keepclassmembers class br.com.aspas.contingencia.domain.event.payload.* { ; }
-keep class * extends androidx.room.RoomDatabase
-keep class dagger.hilt.* { ; }
-keep class javax.inject.* { ; }
-keepclassmembers class kotlinx.coroutines.* { ; }
-keep class javax.jmdns.* { ; }
-dontwarn javax.jmdns.*
-dontwarn org.slf4j.
app/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"
xmlns:tools="[http://schemas.android.com/tools](http://schemas.android.com/tools)">
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<application
android:name=".AspasApp"
android:allowBackup="false"
android:icon="@mipmap/ic_launcher"
android:label="@string/app_name"
android:networkSecurityConfig="@xml/network_security_config"
android:supportsRtl="true"
android:theme="@style/Theme.Aspas"
android:usesCleartextTraffic="true"
tools:targetApi="34">
<activity
android:name=".MainActivity"
android:exported="true"
android:screenOrientation="portrait"
android:windowSoftInputMode="adjustResize"
android:theme="@style/Theme.Aspas">
<intent-filter>
<action android:name="android.intent.action.MAIN" />
<category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
</activity>
<provider
android:name="androidx.startup.InitializationProvider"
android:authorities="${applicationId}.androidx-startup"
android:exported="false"
tools:node="merge">
<meta-data
android:name="androidx.work.WorkManagerInitializer"
android:value="androidx.startup"
tools:node="remove" />
</provider>
</application>
</manifest>
app/src/main/res/values/strings.xml
<resources>
<style name="Theme.Aspas" parent="android:Theme.Material.Light.NoActionBar">
<item name="android:statusBarColor">#1B4332</item>
<item name="android:windowLightStatusBar">false</item>
</style>
</resources>
app/src/main/res/values/themes.xml
<resources>
<style name="Theme.Aspas" parent="android:Theme.Material.Light.NoActionBar">
<item name="android:statusBarColor">#1B4332</item>
<item name="android:windowLightStatusBar">false</item>
</style>
</resources>
app/src/main/res/xml/network_security_config.xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
<base-config cleartextTrafficPermitted="true">
<trust-anchors><certificates src="system" /></trust-anchors>
</base-config>
</network-security-config>
---
============ CORE:COMMON ============
core/common/src/main/kotlin/br/com/aspas/contingencia
package br.com.aspas.contingencia.common
data class Hlc(val physical: Long, val counter: Int, val deviceSuffix: String) : Comparable<Hlc> {
fun encode(): String = "$physical-$counter-$deviceSuffix"
override fun compareTo(other: Hlc): Int {
val p = physical.compareTo(other.physical); if (p != 0) return p
val c = counter.compareTo(other.counter); if (c != 0) return c
return deviceSuffix.compareTo(other.deviceSuffix)
}
companion object {
fun decode(s: String): Hlc {
val parts = s.split("-")
require(parts.size == 3) { "Invalid HLC: $s" }
return Hlc(parts[0].toLong(), parts[1].toInt(), parts[2])
}
}
}
core/common/src/main/kotlin/br/com/aspas/contingencia
package br.com.aspas.contingencia.common
import java.util.concurrent.atomic.AtomicReference
class HlcGenerator(
private val deviceSuffix: String,
private val physicalClock: () -> Long = System::currentTimeMillis
) {
private val state = AtomicReference(Hlc(0L, 0, deviceSuffix))
fun next(): Hlc {
val now = physicalClock()
val prev = state.get()
val newHlc = if (now > prev.physical) Hlc(now, 0, deviceSuffix)
else Hlc(prev.physical, prev.counter + 1, deviceSuffix)
state.set(newHlc); return newHlc
}
fun observe(remote: Hlc): Hlc {
val now = physicalClock()
val prev = state.get()
val maxPhys = maxOf(now, prev.physical, remote.physical)
val newCounter = when {
maxPhys == prev.physical && maxPhys == remote.physical ->
maxOf(prev.counter, remote.counter) + 1
maxPhys == prev.physical -> prev.counter + 1
maxPhys == remote.physical -> remote.counter + 1
else -> 0
}
val newHlc = Hlc(maxPhys, newCounter, deviceSuffix)
state.set(newHlc); return newHlc
}
}
core/common/src/main/kotlin/br/com/aspas/contingencia/common/Uuid.kt
package br.com.aspas.contingencia.common
object Uuid { fun new(): String = java.util.UUID.randomUUID().toString() }

core/common/src/main/kotlin/br/com/aspas/contingencia/common/PinHasher.kt
package br.com.aspas.contingencia.common
import java.security.MessageDigest
import java.security.SecureRandom
object PinHasher {
fun newSalt(): String {
val bytes = ByteArray(16); SecureRandom().nextBytes(bytes)
return bytes.joinToString("") { "%02x".format(it) }
}
fun hash(pin: String, salt: String): String {
val md = MessageDigest.getInstance("SHA-256")
md.update(salt.toByteArray())
return md.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
}
fun verify(pin: String, salt: String, expected: String): Boolean = hash(pin, salt) == expected
}
---
============ CORE:DOMAIN ============
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/model/Models.kt
package br.com.aspas.contingencia.domain.model
data class Table(val tableId: String, val number: Int, val label: String?,
val status: String, val openedAt: Long?, val closedAt: Long?,
val currentLeaseId: String?, val serviceFeePercent: Double) {
val isFree get() = status == "free"
val isOpen get() = status == "open"
}
data class Order(val orderId: String, val tableId: String, val openedBy: String,
val openedAt: Long, val closedAt: Long?, val status: String,
val subtotal: Double, val serviceFee: Double, val total: Double, val notes: String?)
data class OrderItem(val itemId: String, val orderId: String, val catalogItemId: String,
val nameSnapshot: String, val priceSnapshot: Double, val priceOriginal: Double?,
val quantity: Int, val weightG: Int?, val modifiers: Map<String, String>,
val status: String, val includedIn: String?, val includedKind: String?,
val extraFor: String?, val addedBy: String, val addedAt: Long,
val printDestinations: List<String> = emptyList()) {
val isActive get() = status == "active"
companion object {
fun empty() = OrderItem("","","","—",0.0,null,1,null, emptyMap(),
"active",null,null,null,"",0L, emptyList())
}
}
data class CatalogCategory(val categoryId: String, val name: String,
val printDestination: String, val sortOrder: Int)
data class CatalogItem(val itemId: String, val name: String, val categoryId: String,
val price: Double, val pricingMode: String, val priceHalf: Double?,
val weightMaxG: Int?, val printDestinations: List<String>,
val available: Boolean, val description: String?, val origin: String?,
val serves: Int?, val tagsJson: String?, val parrillaBundleJson: String?,
val weightTiersJson: String?)
data class Printer(val printerId: String, val name: String, val ip: String, val port: Int,
val headerLabel: String, val groupId: String?, val enabled: Boolean,
val lastSeenAt: Long?, val lastStatus: String)
data class PrintBatch(val batchId: String, val orderId: String, val itemId: String,
val kind: String, val jobCount: Int)
data class User(val userId: String, val name: String, val role: String, val active: Boolean)
data class Lease(val leaseId: String, val tableId: String, val userId: String,
val deviceId: String, val acquiredAt: Long, val renewedAt: Long,
val expiresAt: Long, val status: String)
data class SyncState(val peerDeviceId: String, val lastHlcSent: String?,
val lastHlcReceived: String?, val lastSyncAt: Long?, val status: String)
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/event/EventEnvelope.kt
package br.com.aspas.contingencia.domain.event
import kotlinx.serialization.Serializable
@Serializable
data class EventEnvelope(
val eventId: String, val hlc: String, val deviceId: String,
val userId: String?, val type: String, val payload: String, val createdAt: Long)
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/event/EventType.kt
package br.com.aspas.contingencia.domain.event
object EventType {
const val CATALOG_IMPORTED = "catalog_imported"
const val CATALOG_ITEM_AVAILABILITY_CHANGED = "catalog_item_availability_changed"
const val TABLE_OPENED = "table_opened"
const val TABLE_CLOSED = "table_closed"
const val TABLE_LEASE_ACQUIRED = "table_lease_acquired"
const val TABLE_LEASE_RENEWED = "table_lease_renewed"
const val TABLE_LEASE_RELEASED = "table_lease_released"
const val TABLE_LEASE_EXPIRED = "table_lease_expired"
const val TABLE_LEASE_FORCED = "table_lease_forced"
const val ORDER_ITEM_ADDED = "order_item_added"
const val ORDER_ITEM_CANCELLED = "order_item_cancelled"
const val ORDER_ITEM_PRICE_ADJUSTED = "order_item_price_adjusted"
const val ORDER_ITEM_COMPED = "order_item_comped"
const val ORDER_VOIDED = "order_voided"
const val ORDER_NOTE_ADDED = "order_note_added"
const val PAYMENT_REGISTERED = "payment_registered"
const val PAYMENT_REVERSED = "payment_reversed"
const val SERVICE_FEE_REMOVED = "service_fee_removed"
const val PRINT_BATCH_QUEUED = "print_batch_queued"
const val PRINT_BATCH_CANCELLATION_QUEUED = "print_batch_cancellation_queued"
const val PRINT_JOB_COMPLETED = "print_job_completed"
const val PRINT_JOB_FAILED = "print_job_failed"
const val PRINT_JOB_FORWARDED = "print_job_forwarded"
const val REPRINT_REQUESTED = "reprint_requested"
const val PRINTER_STATUS_CHANGED = "printer_status_changed"
const val CASH_SESSION_OPENED = "cash_session_opened"
const val CASH_SESSION_CLOSED = "cash_session_closed"
const val CASH_MOVEMENT = "cash_movement"
const val SYNC_CAUGHT_UP = "sync_caught_up"
const val USER_CREATED = "user_created"
const val DEVICE_TRUSTED = "device_trusted"
const val AUDIT_ACTION_RECORDED = "audit_action_recorded"
val ALL: Set<String> = setOf(
CATALOG_IMPORTED, CATALOG_ITEM_AVAILABILITY_CHANGED,
TABLE_OPENED, TABLE_CLOSED, TABLE_LEASE_ACQUIRED, TABLE_LEASE_RENEWED,
TABLE_LEASE_RELEASED, TABLE_LEASE_EXPIRED, TABLE_LEASE_FORCED,
ORDER_ITEM_ADDED, ORDER_ITEM_CANCELLED, ORDER_ITEM_PRICE_ADJUSTED,
ORDER_ITEM_COMPED, ORDER_VOIDED, ORDER_NOTE_ADDED,
PAYMENT_REGISTERED, PAYMENT_REVERSED, SERVICE_FEE_REMOVED,
PRINT_BATCH_QUEUED, PRINT_BATCH_CANCELLATION_QUEUED,
PRINT_JOB_COMPLETED, PRINT_JOB_FAILED, PRINT_JOB_FORWARDED, REPRINT_REQUESTED, PRINTER_STATUS_CHANGED,
CASH_SESSION_OPENED, CASH_SESSION_CLOSED, CASH_MOVEMENT,
SYNC_CAUGHT_UP, USER_CREATED, DEVICE_TRUSTED, AUDIT_ACTION_RECORDED)
}
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/event/payload/Payloads.kt
package br.com.aspas.contingencia.domain.event.payload
import kotlinx.serialization.Serializable
@Serializable data class TableOpenedPayload(val tableId: String, val orderId: String, val openedBy: String)
@Serializable data class TableClosedPayload(val tableId: String, val orderId: String,
val subtotal: Double, val serviceFee: Double, val total: Double, val closedBy: String)
@Serializable data class TableLeaseAcquiredPayload(val leaseId: String, val tableId: String,
val userId: String, val deviceId: String, val expiresAt: Long)
@Serializable data class TableLeaseRenewedPayload(val leaseId: String, val renewedAt: Long, val expiresAt: Long)
@Serializable data class TableLeaseReleasedPayload(val leaseId: String, val releasedBy: String, val reason: String)
@Serializable data class TableLeaseExpiredPayload(val leaseId: String, val tableId: String, val detectedAt: Long)
@Serializable data class TableLeaseForcedPayload(val leaseId: String, val tableId: String,
val forcedBy: String, val reason: String)
@Serializable data class OrderItemAddedPayload(
val orderId: String, val itemId: String, val catalogItemId: String,
val nameSnapshot: String, val priceSnapshot: Double, val priceOriginal: Double? = null,
val quantity: Int, val weightG: Int? = null, val freeItemsAllowed: Int? = null,
val modifiers: Map<String, String> = emptyMap(),
val includedIn: String? = null, val includedKind: String? = null,
val extraFor: String? = null, val addedBy: String)
@Serializable data class OrderItemCancelledPayload(val orderId: String, val itemId: String,
val cancelledBy: String, val reason: String, val printDestinations: List<String> = emptyList())
@Serializable data class OrderItemPriceAdjustedPayload(val orderId: String, val itemId: String,
val newPrice: Double, val reason: String, val adjustedBy: String)
@Serializable data class OrderItemCompedPayload(val orderId: String, val itemId: String,
val reason: String, val compedBy: String)
@Serializable data class OrderVoidedPayload(val orderId: String, val tableId: String,
val reason: String, val voidedBy: String)
@Serializable data class OrderNoteAddedPayload(val orderId: String, val note: String, val addedBy: String)
@Serializable data class PaymentRegisteredPayload(val orderId: String, val paymentId: String,
val method: String, val amount: Double, val splitGroup: String? = null, val registeredBy: String)
@Serializable data class PaymentReversedPayload(val orderId: String, val paymentId: String,
val reversedBy: String, val reason: String)
@Serializable data class ServiceFeeRemovedPayload(val orderId: String, val previousPercent: Double,
val removedBy: String, val reason: String)
@Serializable data class PrintBatchQueuedPayload(
val batchId: String, val orderId: String, val itemId: String,
val kind: String, val createdBy: String,
val destinations: List<String> = emptyList(),
val jobs: List<PrintJobRef> = emptyList()) {
@Serializable data class PrintJobRef(val printId: String, val printerId: String)
}
@Serializable data class PrintJobCompletedPayload(val printId: String, val batchId: String,
val printerId: String, val completedAt: Long)
@Serializable data class PrintJobFailedPayload(val printId: String, val batchId: String,
val printerId: String, val attempt: Int, val error: String, val nextRetryAt: Long)
@Serializable data class PrinterStatusChangedPayload(val printerId: String, val previousStatus: String,
val newStatus: String, val detectedAt: Long, val lastSeenAt: Long? = null)
@Serializable data class CatalogItemAvailabilityChangedPayload(val itemId: String, val available: Boolean)
@Serializable data class CashSessionOpenedPayload(val sessionId: String, val openedBy: String,
val openedAt: Long, val openingAmount: Double)
@Serializable data class CashMovementPayload(val movementId: String, val sessionId: String,
val kind: String, val amount: Double, val reason: String, val registeredBy: String)
@Serializable data class CashSessionClosedPayload(val sessionId: String, val closedBy: String,
val closingAmount: Double, val expectedAmount: Double, val difference: Double, val notes: String? = null)
@Serializable data class UserCreatedPayload(val userId: String, val name: String, val role: String,
val pinHash: String, val pinSalt: String)
@Serializable data class AuditActionRecordedPayload(val action: String, val actorUserId: String,
val actorDeviceId: String, val targetType: String? = null, val targetId: String? = null,
val details: Map<String, String> = emptyMap())
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/repository/Repositories.kt
package br.com.aspas.contingencia.domain.repository
import br.com.aspas.contingencia.common.Hlc
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.model.*
import kotlinx.coroutines.flow.Flow
interface EventRepository {
suspend fun append(type: String, payloadJson: String, userId: String? = null): EventEnvelope
suspend fun appendRemote(envelope: EventEnvelope)
fun pendingForSync(): Flow<List<EventEnvelope>>
suspend fun eventsSince(sinceHlc: Hlc?, limit: Int = 500): List<EventEnvelope>
suspend fun markSynced(eventIds: List<String>)
suspend fun exists(eventId: String): Boolean
fun observeAll(): Flow<EventEnvelope>
}
interface TableRepository {
fun observeAll(): Flow<List<Table>>
fun observeById(id: String): Flow<Table?>
suspend fun getById(id: String): Table?
}
interface OrderRepository {
suspend fun activeByTable(tableId: String): Order?
fun observeById(orderId: String): Flow<Order?>
fun observeItems(orderId: String): Flow<List<OrderItem>>
fun observeAllOpen(): Flow<List<Order>>
}
interface PrinterRepository {
fun observeAll(): Flow<List<Printer>>
suspend fun getById(id: String): Printer?
suspend fun updateStatus(id: String, status: String, at: Long)
}
interface CatalogRepository {
fun observeCategories(): Flow<List<CatalogCategory>>
fun observeAvailableItems(): Flow<List<CatalogItem>>
suspend fun getItem(id: String): CatalogItem?
}
interface UserRepository {
fun observeActive(): Flow<List<User>>
suspend fun getById(id: String): User?
suspend fun listActive(): List<User>
}
interface SyncStateRepository {
fun observeAll(): Flow<List<SyncState>>
}
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/service/Services.kt
package br.com.aspas.contingencia.domain.service
import br.com.aspas.contingencia.domain.model.*
import kotlinx.coroutines.flow.Flow
interface LeaseManager {
suspend fun acquire(tableId: String, userId: String): AcquireResult
suspend fun renew(tableId: String, userId: String): Boolean
suspend fun release(tableId: String, userId: String, reason: String)
suspend fun forceRelease(tableId: String, managerId: String, reason: String)
fun observeActive(tableId: String): Flow<Lease?>
suspend fun sweepExpired()
sealed interface AcquireResult {
data class Success(val lease: Lease) : AcquireResult
data class Busy(val heldBy: String, val expiresAt: Long) : AcquireResult
data class Failed(val reason: String) : AcquireResult
}
companion object {
const val LEASE_TTL_MS = 90_000L
const val HEARTBEAT_MS = 30_000L
}
}
interface PrintDispatcher {
suspend fun enqueueItem(item: OrderItem, orderId: String): PrintBatch
suspend fun enqueueCancellation(item: OrderItem, orderId: String, reason: String, cancelledBy: String): PrintBatch
suspend fun reprint(printId: String, requestedBy: String): PrintBatch
suspend fun enqueueTest(printerId: String, requestedBy: String): PrintBatch
suspend fun forwardToPeer(printId: String, peerDeviceId: String)
suspend fun processQueue()
}
interface SyncEngine {
suspend fun start()
suspend fun stop()
fun isCaughtUp(): Flow<Boolean>
suspend fun syncWith(peerDeviceId: String)
}
core/domain/src/main/kotlin/br/com/aspas/contingencia/domain/usecase/UseCases.kt
package br.com.aspas.contingencia.domain.usecase
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.model.CatalogItem
import br.com.aspas.contingencia.domain.model.Order
import br.com.aspas.contingencia.domain.model.OrderItem
import br.com.aspas.contingencia.domain.repository.*
import br.com.aspas.contingencia.domain.service.LeaseManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
class OpenTableUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val leaseManager: LeaseManager,
private val tableRepository: TableRepository,
private val json: Json
) {
suspend operator fun invoke(tableId: String, userId: String): Result<String> {
val table = tableRepository.getById(tableId)
?: return Result.failure(IllegalStateException("mesa inexistente"))
if (table.status != "free") return Result.failure(IllegalStateException("mesa ocupada"))
val orderId = Uuid.new()
eventRepository.append(EventType.TABLE_OPENED,
json.encodeToString(TableOpenedPayload.serializer(),
TableOpenedPayload(tableId, orderId, userId)), userId = userId)
return when (val r = leaseManager.acquire(tableId, userId)) {
is LeaseManager.AcquireResult.Success -> Result.success(orderId)
else -> Result.failure(IllegalStateException("falha no lease"))
}
}
}
class AddOrderItemUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val json: Json
) {
suspend operator fun invoke(
orderId: String, catalogItemId: String, nameSnapshot: String,
price: Double, quantity: Int = 1,
modifiers: Map<String, String> = emptyMap(),
includedIn: String? = null, includedKind: String? = null,
extraFor: String? = null, priceOriginal: Double? = null,
weightG: Int? = null, userId: String
): Result<String> = runCatching {
val itemId = Uuid.new()
val payload = OrderItemAddedPayload(orderId, itemId, catalogItemId,
nameSnapshot, price, priceOriginal, quantity, weightG, null,
modifiers, includedIn, includedKind, extraFor, userId)
eventRepository.append(EventType.ORDER_ITEM_ADDED,
json.encodeToString(OrderItemAddedPayload.serializer(), payload), userId = userId)
itemId
}
}
class CancelOrderItemUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val catalogRepository: CatalogRepository,
private val json: Json
) {
suspend operator fun invoke(item: OrderItem, reason: String, userId: String): Result<Unit> =
runCatching {
val cat = catalogRepository.getItem(item.catalogItemId)
val payload = OrderItemCancelledPayload(item.orderId, item.itemId,
userId, reason, cat?.printDestinations ?: emptyList())
eventRepository.append(EventType.ORDER_ITEM_CANCELLED,
json.encodeToString(OrderItemCancelledPayload.serializer(), payload), userId = userId)
}
}
class SendToProductionUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val catalogRepository: CatalogRepository,
private val json: Json
) {
suspend operator fun invoke(item: OrderItem): Result<Int> = runCatching {
val cat = catalogRepository.getItem(item.catalogItemId) ?: error("item não existe")
val destinations = cat.printDestinations
val batchId = Uuid.new()
val jobs = destinations.map { PrintBatchQueuedPayload.PrintJobRef(Uuid.new(), it) }
val payload = PrintBatchQueuedPayload(batchId, item.orderId, item.itemId, "item",
item.addedBy, destinations, jobs)
eventRepository.append(EventType.PRINT_BATCH_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), userId = item.addedBy)
jobs.size
}
}
class CloseTableUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val leaseManager: LeaseManager,
private val json: Json
) {
suspend operator fun invoke(
tableId: String, order: Order,
payments: List<Triple<String, Double, String?>>, userId: String
): Result<Unit> = runCatching {
require(payments.isNotEmpty()) { "sem pagamentos" }
val fee = order.subtotal * 0.10
val total = order.subtotal + fee
val paid = payments.sumOf { it.second }
require(kotlin.math.abs(paid - total) < 0.01) { "pagamentos não conferem" }
payments.forEach { (method, amount, split) ->
eventRepository.append(EventType.PAYMENT_REGISTERED,
json.encodeToString(PaymentRegisteredPayload.serializer(),
PaymentRegisteredPayload(order.orderId, Uuid.new(), method, amount, split, userId)),
userId = userId)
}
eventRepository.append(EventType.TABLE_CLOSED,
json.encodeToString(TableClosedPayload.serializer(),
TableClosedPayload(tableId, order.orderId, order.subtotal, fee, total, userId)),
userId = userId)
leaseManager.release(tableId, userId, "closed")
}
}
class AddOrderNoteUseCase @Inject constructor(
private val eventRepository: EventRepository,
private val json: Json
) {
suspend operator fun invoke(orderId: String, note: String, userId: String): Result<Unit> =
runCatching {
eventRepository.append(EventType.ORDER_NOTE_ADDED,
json.encodeToString(OrderNoteAddedPayload.serializer(),
OrderNoteAddedPayload(orderId, note.trim().take(280), userId)), userId = userId)
}
}
class ParrillaBundleUseCase(private val json: Json) {
@Serializable
private data class FixedBundle(val included_guarnicoes: Int = 0, val included_acompanhamentos: Int = 0)
@Serializable
private data class WeightTier(val max_weight_g: Int, val free_items: Int)
sealed interface BundleInfo {
data class Fixed(val guarnicoes: Int, val acompanhamentos: Int) : BundleInfo
data class WeightBased(val maxWeightG: Int, val tiers: List<WeightTier>) : BundleInfo {
fun freeItemsFor(weightG: Int): Int =
tiers.firstOrNull { weightG <= it.max_weight_g }?.free_items ?: tiers.last().free_items
}
}
fun resolve(item: CatalogItem): BundleInfo? {
item.parrillaBundleJson?.let { raw ->
val fixed = runCatching { json.decodeFromString<FixedBundle>(raw) }.getOrNull()
if (fixed != null) return BundleInfo.Fixed(fixed.included_guarnicoes, fixed.included_acompanhamentos)
}
item.weightTiersJson?.let { raw ->
val tiers = runCatching { json.decodeFromString<List<WeightTier>>(raw) }.getOrNull()
if (tiers != null && item.weightMaxG != null) return BundleInfo.WeightBased(item.weightMaxG, tiers)
}
return null
}
}
---
============ CORE:DATA ============
core/data/src/main/kotlin/br/com/aspas/contingencia/data/db/entity/Entities.kt
package br.com.aspas.contingencia.data.db.entity
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(tableName = "events", indices = [Index("hlc"), Index("type"), Index("synced"), Index("createdAt")])
data class EventEntity(@PrimaryKey val eventId: String, val hlc: String, val deviceId: String,
val userId: String?, val type: String, val payload: String, val createdAt: Long,
val synced: Boolean = false)
@Entity(tableName = "tables", indices = [Index(value = ["number"], unique = true), Index("status")])
data class TableEntity(@PrimaryKey val tableId: String, val number: Int, val label: String?,
val status: String, val openedAt: Long?, val closedAt: Long?, val currentLeaseId: String?,
val serviceFeePercent: Double = 10.0)
@Entity(tableName = "orders", indices = [Index("tableId"), Index("status"), Index("openedBy")])
data class OrderEntity(@PrimaryKey val orderId: String, val tableId: String, val openedBy: String,
val openedAt: Long, val closedAt: Long?, val status: String, val subtotal: Double,
val serviceFee: Double, val total: Double, val notes: String?)
@Entity(tableName = "order_items", indices = [Index("orderId"), Index("status"), Index("parentItemId")])
data class OrderItemEntity(@PrimaryKey val itemId: String, val orderId: String, val catalogItemId: String,
val nameSnapshot: String, val priceSnapshot: Double, val priceOriginal: Double?, val quantity: Int,
val weightG: Int?, val modifiersJson: String?, val status: String, val includedIn: String?,
val includedKind: String?, val extraFor: String?, val cancelledBy: String?, val cancelledAt: Long?,
val cancelReason: String?, val addedBy: String, val addedAt: Long,
val parentItemId: String? = null)
@Entity(tableName = "payments", indices = [Index("orderId"), Index("method")])
data class PaymentEntity(@PrimaryKey val paymentId: String, val orderId: String, val method: String,
val amount: Double, val splitGroup: String?, val registeredBy: String, val registeredAt: Long,
val reversed: Boolean = false)
@Entity(tableName = "leases", indices = [Index("tableId"), Index("status"), Index("expiresAt")])
data class LeaseEntity(@PrimaryKey val leaseId: String, val tableId: String, val userId: String,
val deviceId: String, val acquiredAt: Long, val renewedAt: Long, val expiresAt: Long,
val status: String, val releasedBy: String?, val releasedAt: Long?)
@Entity(tableName = "catalog_categories")
data class CatalogCategoryEntity(@PrimaryKey val categoryId: String, val name: String,
val printDestination: String, val sortOrder: Int)
@Entity(tableName = "catalog_items", indices = [Index("categoryId"), Index("available")])
data class CatalogItemEntity(@PrimaryKey val itemId: String, val name: String, val categoryId: String,
val price: Double, val pricingMode: String, val priceHalf: Double?, val weightMaxG: Int?,
val weightTiersJson: String?, val parrillaBundleJson: String?, val printDestinationsJson: String,
val modifiersJson: String?, val tagsJson: String?, val description: String?, val origin: String?,
val serves: Int?, val available: Boolean, val sortOrder: Int)
@Entity(tableName = "printers")
data class PrinterEntity(@PrimaryKey val printerId: String, val name: String, val ip: String,
val port: Int, val headerLabel: String, val groupId: String?, val enabled: Boolean,
val lastSeenAt: Long?, val lastStatus: String)
@Entity(tableName = "print_batches")
data class PrintBatchEntity(@PrimaryKey val batchId: String, val orderItemId: String,
val kind: String, val createdAt: Long, val createdBy: String, val reason: String?)
@Entity(tableName = "print_jobs", indices = [Index("batchId"), Index("printerId"), Index("status"), Index("nextRetryAt")])
data class PrintJobEntity(@PrimaryKey val printId: String, val batchId: String, val printerId: String,
val status: String, val attempts: Int, val lastError: String?, val nextRetryAt: Long?,
val forwardedTo: String?, val reprint: Boolean, val originalPrint: String?,
val createdAt: Long, val completedAt: Long?)
@Entity(tableName = "users")
data class UserEntity(@PrimaryKey val userId: String, val name: String, val role: String,
val pinHash: String, val pinSalt: String, val active: Boolean, val createdAt: Long,
val failedAttempts: Int = 0, val lockedUntil: Long? = null)
@Entity(tableName = "devices")
data class DeviceEntity(@PrimaryKey val deviceId: String, val name: String, val address: String,
val port: Int, val token: String, val trusted: Boolean, val pairedAt: Long?,
val pairedBy: String?, val lastSeenAt: Long?)
@Entity(tableName = "sync_state")
data class SyncStateEntity(@PrimaryKey val peerDeviceId: String, val lastHlcSent: String?,
val lastHlcReceived: String?, val lastSyncAt: Long?, val status: String)
@Entity(tableName = "audit_log", indices = [Index("createdAt"), Index("action")])
data class AuditLogEntity(@PrimaryKey val auditId: String, val eventId: String, val action: String,
val actorUserId: String, val actorDeviceId: String, val targetType: String?,
val targetId: String?, val details: String?, val createdAt: Long)
@Entity(tableName = "snapshots")
data class SnapshotEntity(@PrimaryKey val snapshotId: String, val lastHlc: String,
val createdAt: Long, val eventsUpTo: Int, val tablesJson: String, val ordersJson: String,
val itemsJson: String, val paymentsJson: String)
@Entity(tableName = "cash_sessions", indices = [Index("status"), Index("openedAt")])
data class CashSessionEntity(@PrimaryKey val sessionId: String, val openedBy: String,
val openedAt: Long, val openingAmount: Double, val closedBy: String?, val closedAt: Long?,
val closingAmount: Double?, val expectedAmount: Double?, val difference: Double?,
val status: String, val notes: String?)
@Entity(tableName = "cash_movements", indices = [Index("sessionId"), Index("kind")])
data class CashMovementEntity(@PrimaryKey val movementId: String, val sessionId: String,
val kind: String, val amount: Double, val reason: String, val registeredBy: String,
val registeredAt: Long)
core/data/src/main/kotlin/br/com/aspas/contingencia/data/db/dao/Daos.kt
package br.com.aspas.contingencia.data.db.dao
import androidx.room.*
import br.com.aspas.contingencia.data.db.entity.*
import kotlinx.coroutines.flow.Flow
@Dao
interface EventDao {
@Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(e: EventEntity): Long
@Query("SELECT EXISTS(SELECT 1 FROM events WHERE eventId = :id)") suspend fun exists(id: String): Boolean
@Query("SELECT * FROM events WHERE synced = 0 ORDER BY hlc ASC LIMIT :limit") suspend fun pending(limit: Int = 500): List<EventEntity>
@Query("SELECT * FROM events WHERE hlc > :since ORDER BY hlc ASC LIMIT :limit") suspend fun eventsSince(since: String, limit: Int): List<EventEntity>
@Query("SELECT * FROM events ORDER BY hlc ASC") fun observeAll(): Flow<List<EventEntity>>
@Query("UPDATE events SET synced = 1 WHERE eventId IN (:ids)") suspend fun markSynced(ids: List<String>)
@Query("SELECT * FROM events ORDER BY hlc DESC LIMIT :limit") suspend fun latest(limit: Int): List<EventEntity>
@Query("SELECT COUNT() FROM events") suspend fun count(): Int
@Query("SELECT COUNT() FROM events WHERE synced = 0") suspend fun countPending(): Int
@Query("DELETE FROM events WHERE hlc <= :upto AND synced = 1") suspend fun pruneSyncedUpto(upto: String): Int
}
@Dao
interface TableDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(t: TableEntity)
@Query("SELECT * FROM tables ORDER BY number ASC") fun observeAll(): Flow<List<TableEntity>>
@Query("SELECT * FROM tables ORDER BY number ASC") suspend fun listAll(): List<TableEntity>
@Query("SELECT * FROM tables WHERE tableId = :id") suspend fun getById(id: String): TableEntity?
@Query("SELECT * FROM tables WHERE tableId = :id") fun observeById(id: String): Flow<TableEntity?>
@Query("SELECT COUNT(*) FROM tables WHERE status = 'open'") suspend fun countOpen(): Int
}
@Dao
interface OrderDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(o: OrderEntity)
@Query("SELECT * FROM orders WHERE orderId = :id") suspend fun getById(id: String): OrderEntity?
@Query("SELECT * FROM orders WHERE tableId = :tableId AND status != 'closed' ORDER BY openedAt DESC LIMIT 1") suspend fun activeByTable(tableId: String): OrderEntity?
@Query("SELECT * FROM orders WHERE orderId = :id") fun observeById(id: String): Flow<OrderEntity?>
@Query("SELECT * FROM orders WHERE status = 'open'") suspend fun listOpen(): List<OrderEntity>
@Query("SELECT * FROM orders WHERE status = 'open'") fun observeAllOpen(): Flow<List<OrderEntity>>
@Query("SELECT * FROM orders") suspend fun listAll(): List<OrderEntity>
@Query("SELECT * FROM orders WHERE openedAt BETWEEN :from AND :to") suspend fun listBetween(from: Long, to: Long): List<OrderEntity>
@Query("UPDATE orders SET subtotal = :s, serviceFee = :f, total = :t WHERE orderId = :id") suspend fun updateTotals(id: String, s: Double, f: Double, t: Double)
}
@Dao
interface OrderItemDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(i: OrderItemEntity)
@Query("SELECT * FROM order_items WHERE orderId = :orderId ORDER BY addedAt ASC") fun observeByOrder(orderId: String): Flow<List<OrderItemEntity>>
@Query("SELECT * FROM order_items WHERE itemId = :id") suspend fun getById(id: String): OrderItemEntity?
@Query("SELECT * FROM order_items WHERE parentItemId = :parentId") suspend fun childrenOf(parentId: String): List<OrderItemEntity>
@Query("SELECT * FROM order_items WHERE orderId = :orderId") suspend fun listByOrder(orderId: String): List<OrderItemEntity>
@Query("SELECT * FROM order_items WHERE orderId = :orderId AND status = 'active'") suspend fun listActive(orderId: String): List<OrderItemEntity>
@Query("SELECT * FROM order_items") suspend fun listAll(): List<OrderItemEntity>
}
@Dao
interface PaymentDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PaymentEntity)
@Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY registeredAt ASC") fun observeByOrder(orderId: String): Flow<List<PaymentEntity>>
@Query("SELECT * FROM payments WHERE orderId = :orderId") suspend fun listByOrder(orderId: String): List<PaymentEntity>
@Query("SELECT * FROM payments WHERE paymentId = :id") suspend fun getById(id: String): PaymentEntity?
@Query("SELECT * FROM payments WHERE registeredAt BETWEEN :from AND :to") suspend fun listBySessionWindow(from: Long, to: Long): List<PaymentEntity>
@Query("SELECT * FROM payments") suspend fun listAll(): List<PaymentEntity>
}
@Dao
interface LeaseDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(l: LeaseEntity)
@Query("SELECT * FROM leases WHERE leaseId = :id") suspend fun getById(id: String): LeaseEntity?
@Query("SELECT * FROM leases WHERE tableId = :tableId AND status = 'active' LIMIT 1") suspend fun activeByTable(tableId: String): LeaseEntity?
@Query("SELECT * FROM leases WHERE tableId = :tableId AND status = 'active' LIMIT 1") fun observeActiveByTable(tableId: String): Flow<LeaseEntity?>
@Query("SELECT * FROM leases WHERE status = 'active' AND expiresAt <= :now") suspend fun expired(now: Long): List<LeaseEntity>
@Query("UPDATE leases SET status = :status, releasedAt = :at, releasedBy = :by WHERE leaseId = :id") suspend fun markReleased(id: String, status: String, at: Long, by: String?)
}
@Dao
interface CatalogDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertCategory(c: CatalogCategoryEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertItem(i: CatalogItemEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertCategories(l: List<CatalogCategoryEntity>)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertItems(l: List<CatalogItemEntity>)
@Query("SELECT * FROM catalog_categories ORDER BY sortOrder ASC") fun observeCategories(): Flow<List<CatalogCategoryEntity>>
@Query("SELECT * FROM catalog_items WHERE available = 1 ORDER BY sortOrder ASC") fun observeAvailableItems(): Flow<List<CatalogItemEntity>>
@Query("SELECT * FROM catalog_items WHERE itemId = :id") suspend fun getItem(id: String): CatalogItemEntity?
@Query("DELETE FROM catalog_categories") suspend fun wipeCategories()
@Query("DELETE FROM catalog_items") suspend fun wipeItems()
}
@Dao
interface PrinterDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PrinterEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(l: List<PrinterEntity>)
@Query("SELECT * FROM printers ORDER BY printerId ASC") fun observeAll(): Flow<List<PrinterEntity>>
@Query("SELECT * FROM printers") suspend fun listAll(): List<PrinterEntity>
@Query("SELECT * FROM printers WHERE printerId = :id") suspend fun getById(id: String): PrinterEntity?
@Query("UPDATE printers SET lastStatus = :status, lastSeenAt = :at WHERE printerId = :id") suspend fun updateStatus(id: String, status: String, at: Long)
}
@Dao
interface PrintJobDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertBatch(b: PrintBatchEntity)
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertJob(j: PrintJobEntity)
@Query("SELECT * FROM print_jobs WHERE status IN ('queued','sending') AND (nextRetryAt IS NULL OR nextRetryAt <= :now) ORDER BY createdAt ASC LIMIT :limit") suspend fun dueJobs(now: Long, limit: Int = 20): List<PrintJobEntity>
@Query("SELECT * FROM print_jobs WHERE printId = :id") suspend fun getById(id: String): PrintJobEntity?
@Query("SELECT * FROM print_batches WHERE batchId = :id") suspend fun getBatch(id: String): PrintBatchEntity?
@Query("SELECT * FROM print_jobs WHERE status = 'failed' ORDER BY createdAt DESC") fun observeFailed(): Flow<List<PrintJobEntity>>
@Query("SELECT * FROM print_jobs WHERE status = 'queued' ORDER BY createdAt ASC") fun observeQueued(): Flow<List<PrintJobEntity>>
@Query("SELECT * FROM print_jobs WHERE printerId = :id ORDER BY createdAt DESC LIMIT 50") fun observeByPrinter(id: String): Flow<List<PrintJobEntity>>
@Query("UPDATE print_jobs SET status = :status, attempts = :a, lastError = :err, nextRetryAt = :retry, completedAt = :done WHERE printId = :id") suspend fun updateStatus(id: String, status: String, a: Int, err: String?, retry: Long?, done: Long?)
}
@Dao
interface UserDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(u: UserEntity)
@Query("SELECT * FROM users WHERE userId = :id") suspend fun getById(id: String): UserEntity?
@Query("SELECT * FROM users WHERE active = 1") suspend fun listActive(): List<UserEntity>
@Query("SELECT * FROM users WHERE active = 1 ORDER BY name ASC") fun observeActive(): Flow<List<UserEntity>>
@Query("UPDATE users SET active = 0 WHERE userId = :id") suspend fun deactivate(id: String)
}
@Dao
interface AuditDao {
@Insert suspend fun insert(e: AuditLogEntity)
@Query("SELECT * FROM audit_log ORDER BY createdAt DESC LIMIT :limit") fun observeRecent(limit: Int = 100): Flow<List<AuditLogEntity>>
}
@Dao
interface SnapshotDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(s: SnapshotEntity)
@Query("SELECT * FROM snapshots ORDER BY createdAt DESC LIMIT 1") suspend fun latest(): SnapshotEntity?
@Query("DELETE FROM snapshots WHERE snapshotId != :keep") suspend fun pruneExcept(keep: String)
}
@Dao
interface CashSessionDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(s: CashSessionEntity)
@Query("SELECT * FROM cash_sessions WHERE status = 'open' LIMIT 1") suspend fun active(): CashSessionEntity?
@Query("SELECT * FROM cash_sessions WHERE status = 'open' LIMIT 1") fun observeActive(): Flow<CashSessionEntity?>
@Query("SELECT * FROM cash_sessions WHERE sessionId = :id") suspend fun getById(id: String): CashSessionEntity?
}
@Dao
interface CashMovementDao {
@Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(m: CashMovementEntity)
@Query("SELECT * FROM cash_movements WHERE sessionId = :id ORDER BY registeredAt ASC") suspend fun listBySession(id: String): List<CashMovementEntity>
@Query("SELECT * FROM cash_movements WHERE sessionId = :id ORDER BY registeredAt ASC") fun observeBySession(id: String): Flow<List<CashMovementEntity>>
}
@Dao
interface SyncStateDao {
@Query("SELECT * FROM sync_state") fun observeAll(): Flow<List<SyncStateEntity>>
}
core/data/src/main/kotlin/br/com/aspas/contingencia/data/db/AspasDatabase.kt
package br.com.aspas.contingencia.data.db
import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.aspas.contingencia.data.db.dao.*
import br.com.aspas.contingencia.data.db.entity.*
@Database(
entities = [
EventEntity::class, TableEntity::class, OrderEntity::class, OrderItemEntity::class,
PaymentEntity::class, LeaseEntity::class, CatalogCategoryEntity::class,
CatalogItemEntity::class, PrinterEntity::class, PrintBatchEntity::class,
PrintJobEntity::class, UserEntity::class, DeviceEntity::class, SyncStateEntity::class,
AuditLogEntity::class, SnapshotEntity::class, CashSessionEntity::class,
CashMovementEntity::class],
version = 1,
exportSchema = false)
abstract class AspasDatabase : RoomDatabase() {
abstract fun eventDao(): EventDao
abstract fun tableDao(): TableDao
abstract fun orderDao(): OrderDao
abstract fun orderItemDao(): OrderItemDao
abstract fun paymentDao(): PaymentDao
abstract fun leaseDao(): LeaseDao
abstract fun catalogDao(): CatalogDao
abstract fun printerDao(): PrinterDao
abstract fun printJobDao(): PrintJobDao
abstract fun userDao(): UserDao
abstract fun auditDao(): AuditDao
abstract fun snapshotDao(): SnapshotDao
abstract fun cashSessionDao(): CashSessionDao
abstract fun cashMovementDao(): CashMovementDao
abstract fun syncStateDao(): SyncStateDao
companion object { const val NAME = "aspas_contingencia.db" }
}
core/data/src/main/kotlin/br/com/aspas/contingencia/data/db/DatabaseFactory.kt
package br.com.aspas.contingencia.data.db
import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.security.SecureRandom
object DatabaseFactory {
fun create(context: Context, passphrase: ByteArray): AspasDatabase {
System.loadLibrary("sqlcipher")
val factory = SupportOpenHelperFactory(passphrase)
return Room.databaseBuilder(context, AspasDatabase::class.java, AspasDatabase.NAME)
.openHelperFactory(factory)
.fallbackToDestructiveMigration()
.build()
}
fun generatePassphrase(): ByteArray {
val bytes = ByteArray(32); SecureRandom().nextBytes(bytes); return bytes
}
}
core/data/src/main/kotlin/br/com/aspas/contingencia/data/repository/SessionProvider.kt
package br.com.aspas.contingencia.data.repository
interface SessionProvider {
fun currentUserId(): String?
fun currentDeviceId(): String
}
core/data/src/main/kotlin/br/com/aspas/contingencia/data/repository/EventRepositoryImpl.kt
package br.com.aspas.contingencia.data.repository
import br.com.aspas.contingencia.common.Hlc
import br.com.aspas.contingencia.common.HlcGenerator
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.dao.EventDao
import br.com.aspas.contingencia.data.db.entity.EventEntity
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class EventRepositoryImpl @Inject constructor(
private val dao: EventDao,
private val hlcGenerator: HlcGenerator,
private val deviceId: String,
private val sessionProvider: SessionProvider
) : EventRepository {
private val lock = Mutex()
override suspend fun append(type: String, payloadJson: String, userId: String?): EventEnvelope =
withContext(Dispatchers.IO) {
lock.withLock {
val hlc = hlcGenerator.next()
val env = EventEnvelope(Uuid.new(), hlc.encode(), deviceId,
userId ?: sessionProvider.currentUserId(), type, payloadJson, System.currentTimeMillis())
dao.insert(env.toEntity(synced = false))
env
}
}
override suspend fun appendRemote(envelope: EventEnvelope) = withContext(Dispatchers.IO) {
if (dao.exists(envelope.eventId)) return@withContext
if (envelope.type !in EventType.ALL) return@withContext
val remoteHlc = runCatching { Hlc.decode(envelope.hlc) }.getOrNull() ?: return@withContext
hlcGenerator.observe(remoteHlc)
dao.insert(envelope.toEntity(synced = true))
}
override fun pendingForSync(): Flow<List<EventEnvelope>> =
dao.observeAll().map { list -> list.filter { !it.synced }.map { it.toEnvelope() } }
override suspend fun eventsSince(sinceHlc: Hlc?, limit: Int): List<EventEnvelope> =
withContext(Dispatchers.IO) {
val since = sinceHlc?.encode() ?: "0-0-0000"
dao.eventsSince(since, limit).map { it.toEnvelope() }
}
override suspend fun markSynced(eventIds: List<String>) = withContext(Dispatchers.IO) {
if (eventIds.isNotEmpty()) dao.markSynced(eventIds)
}
override suspend fun exists(eventId: String): Boolean = withContext(Dispatchers.IO) { dao.exists(eventId) }
override fun observeAll(): Flow<EventEnvelope> = flow {
dao.observeAll().collect { list -> list.forEach { emit(it.toEnvelope()) } }
}
}
private fun EventEnvelope.toEntity(synced: Boolean) = EventEntity(eventId, hlc, deviceId, userId, type, payload, createdAt, synced)
private fun EventEntity.toEnvelope() = EventEnvelope(eventId, hlc, deviceId, userId, type, payload, createdAt)
core/data/src/main/kotlin/br/com/aspas/contingencia/data/repository/RepositoriesImpl.kt
package br.com.aspas.contingencia.data.repository
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.CatalogItemEntity
import br.com.aspas.contingencia.data.db.entity.OrderItemEntity
import br.com.aspas.contingencia.domain.model.*
import br.com.aspas.contingencia.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TableRepositoryImpl @Inject constructor(private val db: AspasDatabase) : TableRepository {
override fun observeAll() = db.tableDao().observeAll().map { l ->
l.map { Table(it.tableId, it.number, it.label, it.status, it.openedAt,
it.closedAt, it.currentLeaseId, it.serviceFeePercent) } }
override fun observeById(id: String) = db.tableDao().observeById(id).map {
it?.let { t -> Table(t.tableId, t.number, t.label, t.status, t.openedAt,
t.closedAt, t.currentLeaseId, t.serviceFeePercent) } }
override suspend fun getById(id: String) = db.tableDao().getById(id)?.let {
Table(it.tableId, it.number, it.label, it.status, it.openedAt,
it.closedAt, it.currentLeaseId, it.serviceFeePercent) }
}
@Singleton
class OrderRepositoryImpl @Inject constructor(
private val db: AspasDatabase, private val json: Json
) : OrderRepository {
override suspend fun activeByTable(tableId: String) = db.orderDao().activeByTable(tableId)?.let {
Order(it.orderId, it.tableId, it.openedBy, it.openedAt, it.closedAt,
it.status, it.subtotal, it.serviceFee, it.total, it.notes) }
override fun observeById(orderId: String) = db.orderDao().observeById(orderId).map {
it?.let { o -> Order(o.orderId, o.tableId, o.openedBy, o.openedAt, o.closedAt,
o.status, o.subtotal, o.serviceFee, o.total, o.notes) } }
override fun observeItems(orderId: String) = db.orderItemDao().observeByOrder(orderId).map { list ->
list.map { it.toDomain(json) } }
override fun observeAllOpen() = db.orderDao().observeAllOpen().map { l ->
l.map { Order(it.orderId, it.tableId, it.openedBy, it.openedAt, it.closedAt,
it.status, it.subtotal, it.serviceFee, it.total, it.notes) } }
}
fun OrderItemEntity.toDomain(json: Json): OrderItem {
val mods = modifiersJson?.let { runCatching { json.decodeFromString<Map<String, String>>(it) }.getOrNull() } ?: emptyMap()
return OrderItem(itemId, orderId, catalogItemId, nameSnapshot, priceSnapshot, priceOriginal,
quantity, weightG, mods, status, includedIn, includedKind, extraFor, addedBy, addedAt, emptyList())
}
@Singleton
class PrinterRepositoryImpl @Inject constructor(private val db: AspasDatabase) : PrinterRepository {
override fun observeAll() = db.printerDao().observeAll().map { l ->
l.map { Printer(it.printerId, it.name, it.ip, it.port, it.headerLabel,
it.groupId, it.enabled, it.lastSeenAt, it.lastStatus) } }
override suspend fun getById(id: String) = db.printerDao().getById(id)?.let {
Printer(it.printerId, it.name, it.ip, it.port, it.headerLabel,
it.groupId, it.enabled, it.lastSeenAt, it.lastStatus) }
override suspend fun updateStatus(id: String, status: String, at: Long) =
db.printerDao().updateStatus(id, status, at)
}
@Singleton
class CatalogRepositoryImpl @Inject constructor(
private val db: AspasDatabase, private val json: Json
) : CatalogRepository {
override fun observeCategories() = db.catalogDao().observeCategories().map { l ->
l.map { CatalogCategory(it.categoryId, it.name, it.printDestination, it.sortOrder) } }
override fun observeAvailableItems() = db.catalogDao().observeAvailableItems().map { l ->
l.map { it.toDomain(json) } }
override suspend fun getItem(id: String) = db.catalogDao().getItem(id)?.let { it.toDomain(json) }
}
fun CatalogItemEntity.toDomain(json: Json): CatalogItem {
val dests = runCatching { json.decodeFromString<List<String>>(printDestinationsJson) }.getOrDefault(emptyList())
return CatalogItem(itemId, name, categoryId, price, pricingMode, priceHalf, weightMaxG,
dests, available, description, origin, serves, tagsJson, parrillaBundleJson, weightTiersJson)
}
@Singleton
class UserRepositoryImpl @Inject constructor(private val db: AspasDatabase) : UserRepository {
override fun observeActive() = db.userDao().observeActive().map { l ->
l.map { User(it.userId, it.name, it.role, it.active) } }
override suspend fun getById(id: String) = db.userDao().getById(id)?.let {
User(it.userId, it.name, it.role, it.active) }
override suspend fun listActive() = db.userDao().listActive().map {
User(it.userId, it.name, it.role, it.active) }
}
@Singleton
class SyncStateRepositoryImpl @Inject constructor(private val db: AspasDatabase) : SyncStateRepository {
override fun observeAll() = db.syncStateDao().observeAll().map { l ->
l.map { SyncState(it.peerDeviceId, it.lastHlcSent, it.lastHlcReceived, it.lastSyncAt, it.status) } }
}
core/data/src/main/kotlin/br/com/aspas/contingencia/data/projection/ProjectionEngine.kt
package br.com.aspas.contingencia.data.projection
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.*
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.repository.EventRepository
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ProjectionEngine @Inject constructor(
private val db: AspasDatabase,
private val json: Json,
private val eventRepository: EventRepository
) {
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
private var started = false
fun start() {
if (started) return
started = true
scope.launch {
eventRepository.observeAll().collect { e ->
runCatching { apply(e) }.onFailure { Timber.e(it, "projection ${e.type}") }
}
}
}
suspend fun apply(e: EventEnvelope) {
when (e.type) {
EventType.TABLE_OPENED -> onTableOpened(e)
EventType.TABLE_CLOSED -> onTableClosed(e)
EventType.TABLE_LEASE_ACQUIRED -> onLeaseAcquired(e)
EventType.TABLE_LEASE_RENEWED -> onLeaseRenewed(e)
EventType.TABLE_LEASE_RELEASED -> onLeaseReleased(e)
EventType.TABLE_LEASE_EXPIRED -> onLeaseExpired(e)
EventType.TABLE_LEASE_FORCED -> onLeaseForced(e)
EventType.ORDER_ITEM_ADDED -> onOrderItemAdded(e)
EventType.ORDER_ITEM_CANCELLED -> onOrderItemCancelled(e)
EventType.ORDER_NOTE_ADDED -> onOrderNoteAdded(e)
EventType.ORDER_ITEM_PRICE_ADJUSTED -> onPriceAdjusted(e)
EventType.ORDER_ITEM_COMPED -> onComped(e)
EventType.ORDER_VOIDED -> onVoided(e)
EventType.PAYMENT_REGISTERED -> onPaymentRegistered(e)
EventType.PAYMENT_REVERSED -> onPaymentReversed(e)
EventType.SERVICE_FEE_REMOVED -> onFeeRemoved(e)
EventType.PRINT_BATCH_QUEUED, EventType.PRINT_BATCH_CANCELLATION_QUEUED,
EventType.REPRINT_REQUESTED -> onPrintBatchQueued(e)
EventType.PRINT_JOB_COMPLETED -> onPrintJobCompleted(e)
EventType.PRINT_JOB_FAILED -> onPrintJobFailed(e)
EventType.PRINTER_STATUS_CHANGED -> onPrinterStatus(e)
EventType.AUDIT_ACTION_RECORDED -> onAudit(e)
EventType.CATALOG_ITEM_AVAILABILITY_CHANGED -> onCatalogAvailability(e)
EventType.CASH_SESSION_OPENED -> onCashOpened(e)
EventType.CASH_SESSION_CLOSED -> onCashClosed(e)
EventType.CASH_MOVEMENT -> onCashMovement(e)
}
}
private suspend fun onTableOpened(e: EventEnvelope) {
val p = json.decodeFromString<TableOpenedPayload>(e.payload)
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(status = "open", openedAt = e.createdAt, closedAt = null)) }
db.orderDao().upsert(OrderEntity(p.orderId, p.tableId, p.openedBy, e.createdAt, null,
"open", 0.0, 0.0, 0.0, null))
}
private suspend fun onTableClosed(e: EventEnvelope) {
val p = json.decodeFromString<TableClosedPayload>(e.payload)
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(status = "free", closedAt = e.createdAt, currentLeaseId = null)) }
db.orderDao().getById(p.orderId)?.let {
db.orderDao().upsert(it.copy(status = "closed", closedAt = e.createdAt,
subtotal = p.subtotal, serviceFee = p.serviceFee, total = p.total)) }
}
private suspend fun onLeaseAcquired(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseAcquiredPayload>(e.payload)
db.leaseDao().upsert(LeaseEntity(p.leaseId, p.tableId, p.userId, p.deviceId,
e.createdAt, e.createdAt, p.expiresAt, "active", null, null))
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(currentLeaseId = p.leaseId)) }
}
private suspend fun onLeaseRenewed(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseRenewedPayload>(e.payload)
db.leaseDao().getById(p.leaseId)?.let {
db.leaseDao().upsert(it.copy(renewedAt = p.renewedAt, expiresAt = p.expiresAt)) }
}
private suspend fun onLeaseReleased(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseReleasedPayload>(e.payload)
db.leaseDao().markReleased(p.leaseId, "released", e.createdAt, p.releasedBy)
}
private suspend fun onLeaseExpired(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseExpiredPayload>(e.payload)
db.leaseDao().markReleased(p.leaseId, "expired", p.detectedAt, null)
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(currentLeaseId = null)) }
}
private suspend fun onLeaseForced(e: EventEnvelope) {
val p = json.decodeFromString<TableLeaseForcedPayload>(e.payload)
db.leaseDao().markReleased(p.leaseId, "forced", e.createdAt, p.forcedBy)
}
private suspend fun onOrderItemAdded(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemAddedPayload>(e.payload)
db.orderItemDao().upsert(OrderItemEntity(p.itemId, p.orderId, p.catalogItemId,
p.nameSnapshot, p.priceSnapshot, p.priceOriginal, p.quantity, p.weightG,
if (p.modifiers.isEmpty()) null else json.encodeToString(p.modifiers),
"active", p.includedIn, p.includedKind, p.extraFor, null, null, null,
p.addedBy, e.createdAt, p.includedIn ?: p.extraFor))
recomputeTotals(p.orderId)
}
private suspend fun onOrderItemCancelled(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemCancelledPayload>(e.payload)
db.orderItemDao().getById(p.itemId)?.let {
db.orderItemDao().upsert(it.copy(status = "cancelled", cancelledBy = p.cancelledBy,
cancelledAt = e.createdAt, cancelReason = p.reason)) }
recomputeTotals(p.orderId)
}
private suspend fun onOrderNoteAdded(e: EventEnvelope) {
val p = json.decodeFromString<OrderNoteAddedPayload>(e.payload)
db.orderDao().getById(p.orderId)?.let { db.orderDao().upsert(it.copy(notes = p.note)) }
}
private suspend fun onPriceAdjusted(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemPriceAdjustedPayload>(e.payload)
db.orderItemDao().getById(p.itemId)?.let { db.orderItemDao().upsert(it.copy(priceSnapshot = p.newPrice)) }
recomputeTotals(p.orderId)
}
private suspend fun onComped(e: EventEnvelope) {
val p = json.decodeFromString<OrderItemCompedPayload>(e.payload)
db.orderItemDao().getById(p.itemId)?.let { db.orderItemDao().upsert(it.copy(priceSnapshot = 0.0)) }
recomputeTotals(p.orderId)
}
private suspend fun onVoided(e: EventEnvelope) {
val p = json.decodeFromString<OrderVoidedPayload>(e.payload)
db.orderDao().getById(p.orderId)?.let {
db.orderDao().upsert(it.copy(status = "voided", subtotal = 0.0, serviceFee = 0.0,
total = 0.0, closedAt = e.createdAt)) }
db.tableDao().getById(p.tableId)?.let {
db.tableDao().upsert(it.copy(status = "free", closedAt = e.createdAt)) }
}
private suspend fun onPaymentRegistered(e: EventEnvelope) {
val p = json.decodeFromString<PaymentRegisteredPayload>(e.payload)
db.paymentDao().upsert(PaymentEntity(p.paymentId, p.orderId, p.method,
p.amount, p.splitGroup, p.registeredBy, e.createdAt, false))
}
private suspend fun onPaymentReversed(e: EventEnvelope) {
val p = json.decodeFromString<PaymentReversedPayload>(e.payload)
db.paymentDao().getById(p.paymentId)?.let { db.paymentDao().upsert(it.copy(reversed = true)) }
}
private suspend fun onFeeRemoved(e: EventEnvelope) {
val p = json.decodeFromString<ServiceFeeRemovedPayload>(e.payload)
db.orderDao().getById(p.orderId)?.let {
db.orderDao().upsert(it.copy(serviceFee = 0.0, total = it.subtotal)) }
}
private suspend fun onPrintBatchQueued(e: EventEnvelope) {
val p = json.decodeFromString<PrintBatchQueuedPayload>(e.payload)
val kind = when (e.type) {
EventType.PRINT_BATCH_CANCELLATION_QUEUED -> "cancellation"
EventType.REPRINT_REQUESTED -> "reprint"
else -> p.kind }
db.printJobDao().upsertBatch(PrintBatchEntity(p.batchId, p.itemId, kind,
e.createdAt, p.createdBy, null))
p.jobs.forEach { ref ->
db.printJobDao().upsertJob(PrintJobEntity(ref.printId, p.batchId, ref.printerId,
"queued", 0, null, null, null, kind == "reprint", null, e.createdAt, null)) }
}
private suspend fun onPrintJobCompleted(e: EventEnvelope) {
val p = json.decodeFromString<PrintJobCompletedPayload>(e.payload)
db.printJobDao().updateStatus(p.printId, "ok", 0, null, null, p.completedAt)
}
private suspend fun onPrintJobFailed(e: EventEnvelope) {
val p = json.decodeFromString<PrintJobFailedPayload>(e.payload)
db.printJobDao().updateStatus(p.printId,
if (p.attempt >= 5) "failed" else "queued", p.attempt, p.error, p.nextRetryAt, null)
}
private suspend fun onPrinterStatus(e: EventEnvelope) {
val p = json.decodeFromString<PrinterStatusChangedPayload>(e.payload)
db.printerDao().updateStatus(p.printerId, p.newStatus, p.detectedAt)
}
private suspend fun onAudit(e: EventEnvelope) {
val p = json.decodeFromString<AuditActionRecordedPayload>(e.payload)
db.auditDao().insert(AuditLogEntity(e.eventId, e.eventId, p.action, p.actorUserId,
p.actorDeviceId, p.targetType, p.targetId, json.encodeToString(p.details), e.createdAt))
}
private suspend fun onCatalogAvailability(e: EventEnvelope) {
val p = json.decodeFromString<CatalogItemAvailabilityChangedPayload>(e.payload)
db.catalogDao().getItem(p.itemId)?.let {
db.catalogDao().upsertItem(it.copy(available = p.available)) }
}
private suspend fun onCashOpened(e: EventEnvelope) {
val p = json.decodeFromString<CashSessionOpenedPayload>(e.payload)
db.cashSessionDao().upsert(CashSessionEntity(p.sessionId, p.openedBy, p.openedAt,
p.openingAmount, null, null, null, null, null, "open", null))
}
private suspend fun onCashClosed(e: EventEnvelope) {
val p = json.decodeFromString<CashSessionClosedPayload>(e.payload)
db.cashSessionDao().getById(p.sessionId)?.let {
db.cashSessionDao().upsert(it.copy(closedBy = p.closedBy, closedAt = e.createdAt,
closingAmount = p.closingAmount, expectedAmount = p.expectedAmount,
difference = p.difference, status = "closed", notes = p.notes)) }
}
private suspend fun onCashMovement(e: EventEnvelope) {
val p = json.decodeFromString<CashMovementPayload>(e.payload)
db.cashMovementDao().insert(CashMovementEntity(p.movementId, p.sessionId, p.kind,
p.amount, p.reason, p.registeredBy, e.createdAt))
}
private suspend fun recomputeTotals(orderId: String) {
val items = db.orderItemDao().listByOrder(orderId).filter { it.status == "active" }
val subtotal = items.sumOf { it.priceSnapshot * it.quantity }
val order = db.orderDao().getById(orderId) ?: return
val fee = subtotal * 0.10
db.orderDao().updateTotals(orderId, subtotal, fee, subtotal + fee)
}
}
core/data/src/main/kotlin/br/com/aspas/contingencia/data/catalog/CatalogImporter.kt
package br.com.aspas.contingencia.data.catalog
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
---
============ CORE:PRINT ============
core/print/src/main/kotlin/br/com/aspas/contingencia/print/driver/EscPosCommands.kt
package br.com.aspas.contingencia.print.driver
object EscPosCommands {
val INIT = byteArrayOf(0x1B, 0x40)
val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
val FONT_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)
val FONT_DOUBLE_H = byteArrayOf(0x1D, 0x21, 0x01)
val FONT_DOUBLE_BOTH = byteArrayOf(0x1D, 0x21, 0x11)
val FEED = byteArrayOf(0x0A)
val CUT_FULL = byteArrayOf(0x1D, 0x56, 0x41, 0x10)
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/driver/ElginI9Driver.kt
package br.com.aspas.contingencia.print.driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
class ElginI9Driver(private val host: String, private val port: Int = 9100,
private val timeoutMs: Int = 3000) {
private var socket: Socket? = null
suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
runCatching {
val s = Socket()
s.connect(InetSocketAddress(host, port), timeoutMs)
s.soTimeout = timeoutMs
socket = s; true
}.getOrDefault(false)
}
suspend fun send(bytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
runCatching {
val s = socket ?: return@runCatching false
s.getOutputStream().apply { write(bytes); flush() }; true
}.getOrDefault(false)
}
suspend fun close() = withContext(Dispatchers.IO) {
runCatching { socket?.close() }; socket = null
}
suspend fun isOnline(): Boolean = withContext(Dispatchers.IO) {
runCatching {
Socket().use { s -> s.connect(InetSocketAddress(host, port), timeoutMs); true }
}.getOrDefault(false)
}
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/render/TicketLayout.kt
package br.com.aspas.contingencia.print.render
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
object TicketLayout {
const val TOTAL_COLS = 32
const val MARGIN = 2
const val USABLE = TOTAL_COLS - (MARGIN * 2)
fun center(text: String, width: Int = USABLE): String =
text.take(width).padStart((width + text.length) / 2).padEnd(width)
fun twoCols(left: String, right: String, width: Int = USABLE): String {
val space = width - left.length - right.length
return when {
space <= 0 -> "{left.take(width - right.length - 1)} $right"
else -> left + " ".repeat(space) + right }
}
fun border(width: Int = USABLE): String = "+" + "-".repeat(width) + "+"
fun boxed(content: String, width: Int = USABLE): String = "|" + content.take(width).padEnd(width) + "|"
fun boxedTitle(text: String, width: Int = USABLE): String = "|" + center(text, width) + "|"
fun bold(text: String): String = "\u001B\u0045\u0001$text\u001B\u0045\u0000"
fun formatTime(ms: Long): String = SimpleDateFormat("HH:mm", Locale("pt","BR")).format(Date(ms))
fun formatDateTime(ms: Long): String = SimpleDateFormat("dd/MM HH:mm", Locale("pt","BR")).format(Date(ms))
fun formatMoney(value: Double): String = "R " + String.format(Locale("pt","BR"), "%,.2f", value)
fun wrap(text: String, width: Int = USABLE): List<String> {
if (text.isBlank()) return emptyList()
val words = text.split(" ").filter { it.isNotEmpty() }
val lines = mutableListOf<String>()
var current = StringBuilder()
for (w in words) {
if (current.length + w.length + 1 > width) {
lines += current.toString().trim(); current = StringBuilder() }
current.append(w).append(" ")
}
if (current.isNotEmpty()) lines += current.toString().trim()
return lines
}
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/render/TicketRenderer.kt
package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.domain.model.OrderItem
import br.com.aspas.contingencia.domain.model.Printer
interface TicketRenderer { fun render(context: TicketContext): ByteArray }
data class TicketContext(
val printer: Printer, val tableNumber: Int, val waiterName: String,
val orderItem: OrderItem, val orderNote: String? = null,
val includedChildren: List<OrderItem> = emptyList(),
val extras: List<OrderItem> = emptyList(),
val kind: TicketKind, val cancelReason: String? = null,
val cancelledBy: String? = null, val isReprint: Boolean = false,
val printedAt: Long = System.currentTimeMillis())
enum class TicketKind { ITEM, CANCELLATION, REPRINT, TEST }
core/print/src/main/kotlin/br/com/aspas/contingencia/print/render/ItemTicketRenderer.kt
package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.print.driver.EscPosCommands
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ItemTicketRenderer @Inject constructor() : TicketRenderer {
override fun render(ctx: TicketContext): ByteArray {
val out = ByteArrayOutputStream()
out.write(EscPosCommands.INIT)
out.write(EscPosCommands.ALIGN_CENTER)
out.write(EscPosCommands.FONT_DOUBLE_BOTH)
out.write(TicketLayout.bold(ctx.printer.headerLabel).toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
out.write(EscPosCommands.FONT_NORMAL)
out.write(EscPosCommands.ALIGN_LEFT)
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols(
"MESA ${ctx.tableNumber.toString().padStart(2, '0')}",
ctx.waiterName.take(14).uppercase())))
writeLine(out, TicketLayout.boxed("Hora: {TicketLayout.formatDateTime(ctx.printedAt)}"))
ctx.orderNote?.takeIf { it.isNotBlank() }?.let { note ->
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("*** OBSERVAÇÃO DA MESA ***"))
TicketLayout.wrap(note, TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) }
}
writeLine(out, TicketLayout.border())
val item = ctx.orderItem
val qty = if (item.quantity > 1) "${item.quantity}x " else ""
val weight = item.weightG?.let { " ({it}g)" } ?: ""
out.write(EscPosCommands.FONT_DOUBLE_H)
out.write(EscPosCommands.BOLD_ON)
TicketLayout.wrap("qty{item.nameSnapshot}weight", TicketLayout.USABLE).forEach { l ->
writeLine(out, TicketLayout.boxed(l)) }
out.write(EscPosCommands.BOLD_OFF)
out.write(EscPosCommands.FONT_NORMAL)
item.modifiers.forEach { (k, v) ->
if (v.isNotBlank()) {
TicketLayout.wrap("{labelize(k)}: $v", TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) } } }
if (ctx.includedChildren.isNotEmpty()) {
writeLine(out, TicketLayout.border())
ctx.includedChildren.groupBy { it.includedKind ?: "incluso" }.forEach { (kind, list) ->
writeLine(out, TicketLayout.boxed("INCLUSO · ${labelize(kind).uppercase()}"))
list.forEach { c ->
TicketLayout.wrap("• ${c.nameSnapshot}", TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) } } } }
if (ctx.extras.isNotEmpty()) {
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("EXTRAS COBRADOS"))
ctx.extras.forEach { ex ->
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols(
"• ${ex.nameSnapshot}",
TicketLayout.formatMoney(ex.priceSnapshot * ex.quantity)))) } }
writeLine(out, TicketLayout.border())
if (ctx.isReprint) {
out.write(EscPosCommands.ALIGN_CENTER)
out.write(TicketLayout.bold("*** REIMPRESSÃO ***").toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
}
out.write(EscPosCommands.ALIGN_LEFT)
repeat(4) { out.write(EscPosCommands.FEED) }
out.write(EscPosCommands.CUT_FULL)
return out.toByteArray()
}
private fun writeLine(out: ByteArrayOutputStream, line: String) {
out.write(line.toByteArray(Charsets.ISO_8859_1)); out.write(EscPosCommands.FEED)
}
private fun labelize(k: String) = when (k) {
"ponto" -> "Ponto"; "porcao" -> "Porção"; "preparo_peixe" -> "Preparo"
"observacao" -> "Obs"; "guarnicao" -> "Guarnição"
"acompanhamento" -> "Acompanhamento"; "incluso" -> "Incluso"
"free" -> "Item livre"; else -> k.replaceFirstChar { it.uppercase() } }
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/render/CancellationTicketRenderer.kt
package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.print.driver.EscPosCommands
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CancellationTicketRenderer @Inject constructor() : TicketRenderer {
override fun render(ctx: TicketContext): ByteArray {
val out = ByteArrayOutputStream()
out.write(EscPosCommands.INIT)
out.write(EscPosCommands.ALIGN_CENTER)
out.write(EscPosCommands.FONT_DOUBLE_BOTH)
out.write(TicketLayout.bold(ctx.printer.headerLabel).toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
out.write(EscPosCommands.FONT_NORMAL)
out.write(EscPosCommands.ALIGN_LEFT)
writeLine(out, TicketLayout.center("*** CANCELAMENTO ***"))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols(
"MESA {ctx.tableNumber.toString().padStart(2, '0')}",
ctx.waiterName.take(14).uppercase())))
writeLine(out, TicketLayout.border())
out.write(EscPosCommands.FONT_DOUBLE_H)
TicketLayout.wrap("{ctx.orderItem.quantity}x ${ctx.orderItem.nameSnapshot}",
TicketLayout.USABLE).forEach { l -> writeLine(out, TicketLayout.boxed(l)) }
out.write(EscPosCommands.FONT_NORMAL)
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("MOTIVO:"))
TicketLayout.wrap(ctx.cancelReason ?: "—", TicketLayout.USABLE - 2).forEach { l ->
writeLine(out, TicketLayout.boxed("  $l")) }
ctx.cancelledBy?.let { writeLine(out, TicketLayout.boxed("Por: $it")) }
writeLine(out, TicketLayout.border())
out.write(EscPosCommands.ALIGN_LEFT)
repeat(4) { out.write(EscPosCommands.FEED) }
out.write(EscPosCommands.CUT_FULL)
return out.toByteArray()
}
private fun writeLine(out: ByteArrayOutputStream, line: String) {
out.write(line.toByteArray(Charsets.ISO_8859_1)); out.write(EscPosCommands.FEED)
}
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/render/TestTicketRenderer.kt
package br.com.aspas.contingencia.print.render
import br.com.aspas.contingencia.print.driver.EscPosCommands
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TestTicketRenderer @Inject constructor() : TicketRenderer {
override fun render(ctx: TicketContext): ByteArray {
val out = ByteArrayOutputStream()
out.write(EscPosCommands.INIT)
out.write(EscPosCommands.ALIGN_CENTER)
out.write(EscPosCommands.FONT_DOUBLE_BOTH)
out.write(TicketLayout.bold(ctx.printer.headerLabel).toByteArray(Charsets.ISO_8859_1))
out.write(EscPosCommands.FEED)
out.write(EscPosCommands.FONT_NORMAL)
out.write(EscPosCommands.ALIGN_LEFT)
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxedTitle("TESTE DE IMPRESSÃO"))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols("Impressora", ctx.printer.printerId)))
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols("IP", ctx.printer.ip)))
writeLine(out, TicketLayout.boxed(TicketLayout.twoCols("Hora", TicketLayout.formatTime(ctx.printedAt))))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxed("Acentos: ção ãéíóú"))
writeLine(out, TicketLayout.boxed("Números: 0123456789"))
writeLine(out, TicketLayout.border())
writeLine(out, TicketLayout.boxedTitle("Se você lê isto, OK"))
writeLine(out, TicketLayout.border())
out.write(EscPosCommands.ALIGN_LEFT)
repeat(4) { out.write(EscPosCommands.FEED) }
out.write(EscPosCommands.CUT_FULL)
return out.toByteArray()
}
private fun writeLine(out: ByteArrayOutputStream, line: String) {
out.write(line.toByteArray(Charsets.ISO_8859_1)); out.write(EscPosCommands.FEED)
}
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/RetryPolicy.kt
package br.com.aspas.contingencia.print
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class RetryPolicy @Inject constructor() {
private val backoff = longArrayOf(5_000, 15_000, 45_000, 120_000, 300_000)
fun nextRetry(attempt: Int): Long {
val idx = (attempt - 1).coerceIn(0, backoff.size - 1)
return System.currentTimeMillis() + backoff[idx]
}
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/PrintDispatcherImpl.kt
package br.com.aspas.contingencia.print
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.PrintBatchEntity
import br.com.aspas.contingencia.data.db.entity.PrintJobEntity
import br.com.aspas.contingencia.data.repository.toDomain
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.*
import br.com.aspas.contingencia.domain.model.*
import br.com.aspas.contingencia.domain.repository.EventRepository
import br.com.aspas.contingencia.domain.service.PrintDispatcher
import br.com.aspas.contingencia.print.driver.ElginI9Driver
import br.com.aspas.contingencia.print.render.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class PrintDispatcherImpl @Inject constructor(
private val db: AspasDatabase,
private val eventRepository: EventRepository,
private val json: Json,
private val itemRenderer: ItemTicketRenderer,
private val cancelRenderer: CancellationTicketRenderer,
private val testRenderer: TestTicketRenderer,
private val retryPolicy: RetryPolicy
) : PrintDispatcher {
override suspend fun enqueueItem(item: OrderItem, orderId: String): PrintBatch =
withContext(Dispatchers.IO) {
val batchId = Uuid.new()
val jobs = item.printDestinations.map { PrintBatchQueuedPayload.PrintJobRef(Uuid.new(), it) }
val payload = PrintBatchQueuedPayload(batchId, orderId, item.itemId, "item",
item.addedBy, item.printDestinations, jobs)
eventRepository.append(EventType.PRINT_BATCH_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), item.addedBy)
PrintBatch(batchId, orderId, item.itemId, "item", jobs.size)
}
override suspend fun enqueueCancellation(item: OrderItem, orderId: String,
reason: String, cancelledBy: String): PrintBatch = withContext(Dispatchers.IO) {
val batchId = Uuid.new()
val jobs = item.printDestinations.map { PrintBatchQueuedPayload.PrintJobRef(Uuid.new(), it) }
val payload = PrintBatchQueuedPayload(batchId, orderId, item.itemId, "cancellation",
cancelledBy, item.printDestinations, jobs)
eventRepository.append(EventType.PRINT_BATCH_CANCELLATION_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), cancelledBy)
PrintBatch(batchId, orderId, item.itemId, "cancellation", jobs.size)
}
override suspend fun reprint(printId: String, requestedBy: String): PrintBatch =
withContext(Dispatchers.IO) {
val original = db.printJobDao().getById(printId) ?: error("job não existe")
val batchId = Uuid.new()
val newPrintId = Uuid.new()
val payload = PrintBatchQueuedPayload(batchId, original.batchId, original.batchId,
"reprint", requestedBy, listOf(original.printerId),
listOf(PrintBatchQueuedPayload.PrintJobRef(newPrintId, original.printerId)))
eventRepository.append(EventType.REPRINT_REQUESTED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), requestedBy)
PrintBatch(batchId, original.batchId, original.batchId, "reprint", 1)
}
override suspend fun enqueueTest(printerId: String, requestedBy: String): PrintBatch =
withContext(Dispatchers.IO) {
val batchId = Uuid.new()
val printId = Uuid.new()
val payload = PrintBatchQueuedPayload(batchId, "test", "test",
"test", requestedBy, listOf(printerId),
listOf(PrintBatchQueuedPayload.PrintJobRef(printId, printerId)))
eventRepository.append(EventType.PRINT_BATCH_QUEUED,
json.encodeToString(PrintBatchQueuedPayload.serializer(), payload), requestedBy)
PrintBatch(batchId, "test", "test", "test", 1)
}
override suspend fun forwardToPeer(printId: String, peerDeviceId: String) {
eventRepository.append(EventType.PRINT_JOB_FORWARDED,
"""{"printId":"$printId","toDevice":"$peerDeviceId"}""", null)
}
override suspend fun processQueue() = withContext(Dispatchers.IO) {
val due = db.printJobDao().dueJobs(System.currentTimeMillis(), 20)
for (job in due) {
runCatching { processOne(job) }.onFailure { Timber.e(it, "processOne ${job.printId}") }
}
}
private suspend fun processOne(job: PrintJobEntity) {
val printer = db.printerDao().getById(job.printerId) ?: return
if (!printer.enabled) return
val batch = db.printJobDao().getBatch(job.batchId) ?: return
val bytes = renderBatch(batch, job, printer) ?: return
val driver = ElginI9Driver(printer.ip, printer.port)
val connected = driver.connect()
val sent = connected && driver.send(bytes)
driver.close()
val attempt = job.attempts + 1
if (sent) {
db.printJobDao().updateStatus(job.printId, "ok", attempt, null, null, System.currentTimeMillis())
db.printerDao().updateStatus(printer.printerId, "online", System.currentTimeMillis())
eventRepository.append(EventType.PRINT_JOB_COMPLETED,
json.encodeToString(PrintJobCompletedPayload.serializer(),
PrintJobCompletedPayload(job.printId, job.batchId, job.printerId,
System.currentTimeMillis())), null)
} else {
val next = retryPolicy.nextRetry(attempt)
val err = if (!connected) "connection refused" else "send failed"
db.printJobDao().updateStatus(job.printId,
if (attempt >= 5) "failed" else "queued", attempt, err, next, null)
db.printerDao().updateStatus(printer.printerId, "offline", System.currentTimeMillis())
eventRepository.append(EventType.PRINT_JOB_FAILED,
json.encodeToString(PrintJobFailedPayload.serializer(),
PrintJobFailedPayload(job.printId, job.batchId, job.printerId,
attempt, err, next)), null)
}
}
private suspend fun renderBatch(batch: PrintBatchEntity, job: PrintJobEntity,
printer: br.com.aspas.contingencia.data.db.entity.PrinterEntity): ByteArray? {
val printerDomain = Printer(printer.printerId, printer.name, printer.ip, printer.port,
printer.headerLabel, printer.groupId, printer.enabled, printer.lastSeenAt, printer.lastStatus)
if (batch.kind == "test") {
return testRenderer.render(TicketContext(printerDomain, 0, "—",
OrderItem.empty(), kind = TicketKind.TEST))
}
val entity = db.orderItemDao().getById(batch.orderItemId) ?: return null
val item = entity.toDomain(json)
val order = db.orderDao().getById(item.orderId) ?: return null
val table = db.tableDao().getById(order.tableId)
val user = db.userDao().getById(item.addedBy)
return when (batch.kind) {
"cancellation" -> cancelRenderer.render(TicketContext(
printer = printerDomain, tableNumber = table?.number ?: 0,
waiterName = user?.name ?: "—", orderItem = item,
orderNote = order.notes, kind = TicketKind.CANCELLATION,
cancelReason = batch.reason ?: "—", cancelledBy = batch.createdBy))
else -> {
val children = db.orderItemDao().childrenOf(item.itemId).map { it.toDomain(json) }
itemRenderer.render(TicketContext(
printer = printerDomain, tableNumber = table?.number ?: 0,
waiterName = user?.name ?: "—", orderItem = item,
orderNote = order.notes,
includedChildren = children.filter { it.includedIn == item.itemId },
extras = children.filter { it.extraFor == item.itemId },
kind = if (batch.kind == "reprint") TicketKind.REPRINT else TicketKind.ITEM,
isReprint = batch.kind == "reprint"))
}
}
}
}
core/print/src/main/kotlin/br/com/aspas/contingencia/print/PrintScheduler.kt
package br.com.aspas.contingencia.print
import br.com.aspas.contingencia.domain.service.PrintDispatcher
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class PrintScheduler @Inject constructor(private val dispatcher: PrintDispatcher) {
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
private var job: Job? = null
fun start() {
if (job?.isActive == true) return
job = scope.launch {
while (isActive) {
runCatching { dispatcher.processQueue() }.onFailure { Timber.e(it, "print q") }
delay(3_000)
}
}
}
fun stop() { job?.cancel() }
}
---
============ CORE:SYNC (mínimo) ============
core/sync/src/main/kotlin/br/com/aspas/contingencia/sync/SyncPreference.kt
package br.com.aspas.contingencia.sync
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SyncPreference @Inject constructor(ctx: Context) {
private val prefs = ctx.getSharedPreferences("sync", Context.MODE_PRIVATE)
var enabled: Boolean
get() = prefs.getBoolean("enabled", false)
set(v) = prefs.edit().putBoolean("enabled", v).apply()
var deviceName: String
get() = prefs.getString("name", android.os.Build.MODEL) ?: "aspas"
set(v) = prefs.edit().putString("name", v).apply()
}
core/sync/src/main/kotlin/br/com/aspas/contingencia/sync/SyncEngineImpl.kt
package br.com.aspas.contingencia.sync
import br.com.aspas.contingencia.domain.service.SyncEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SyncEngineImpl @Inject constructor() : SyncEngine {
override suspend fun start() { /* TODO implementar mDNS+Ktor */ }
override suspend fun stop() { }
override fun isCaughtUp(): Flow<Boolean> = flowOf(true)
override suspend fun syncWith(peerDeviceId: String) { }
}
---
============ APP ============
app/src/main/kotlin/br/com/aspas/contingencia/AspasApp.kt
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
app/src/main/kotlin/br/com/aspas/contingencia/MainActivity.kt
package br.com.aspas.contingencia
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import br.com.aspas.contingencia.navigation.AspasNavHost
import br.com.aspas.contingencia.navigation.Routes
import br.com.aspas.contingencia.ui.theme.AspasTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContent {
AspasTheme {
Surface(Modifier.fillMaxSize()) {
AspasNavHost(rememberNavController(), startDestination = Routes.LOGIN)
}
}
}
}
}
app/src/main/kotlin/br/com/aspas/contingencia/session/SessionHolder.kt
package br.com.aspas.contingencia.session
object SessionHolder {
@Volatile lateinit var session: SessionManager
}
app/src/main/kotlin/br/com/aspas/contingencia/session/SessionManager.kt
package br.com.aspas.contingencia.session
import br.com.aspas.contingencia.data.repository.SessionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SessionManager @Inject constructor(private val deviceId: String) : SessionProvider {
data class Session(val userId: String, val userName: String, val role: String, val loginAt: Long)
private val _session = MutableStateFlow<Session?>(null)
val session: StateFlow<Session?> = _session.asStateFlow()
override fun currentUserId(): String? = _session.value?.userId
override fun currentDeviceId(): String = deviceId
val currentRole: String? get() = _session.value?.role
fun login(userId: String, userName: String, role: String) {
_session.value = Session(userId, userName, role, System.currentTimeMillis())
}
fun logout() { _session.value = null }
}
app/src/main/kotlin/br/com/aspas/contingencia/di/AppModule.kt
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
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
@Provides @Singleton
fun provideDeviceId(@ApplicationContext ctx: Context): String {
val p = ctx.getSharedPreferences("device", Context.MODE_PRIVATE)
return p.getString("deviceId", null)
?: UUID.randomUUID().toString().also { p.edit().putString("deviceId", it).apply() }
}
@Provides @Singleton
fun provideDeviceName(@ApplicationContext ctx: Context): String {
val p = ctx.getSharedPreferences("device", Context.MODE_PRIVATE)
return p.getString("deviceName", null)
?: android.os.Build.MODEL.also { p.edit().putString("deviceName", it).apply() }
}
@Provides @Singleton
fun provideHlcGenerator(deviceId: String): HlcGenerator = HlcGenerator(deviceId.takeLast(4))
}
@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {
@Binds @Singleton
abstract fun sessionProvider(impl: SessionManager): SessionProvider
}
app/src/main/kotlin/br/com/aspas/contingencia/di/DatabaseModule.kt
package br.com.aspas.contingencia.di
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.DatabaseFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
@Provides @Singleton
fun provideJson(): Json = Json {
ignoreUnknownKeys = true
encodeDefaults = true
explicitNulls = false
}
@Provides @Singleton
fun provideDatabase(@ApplicationContext ctx: Context): AspasDatabase {
val mk = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
val prefs = EncryptedSharedPreferences.create(ctx, "aspas_secrets", mk,
EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
val hex = prefs.getString("db_passphrase", null)
val pass = if (hex != null) {
ByteArray(hex.length / 2) { i -> hex.substring(i * 2, i * 2 + 2).toInt(16).toByte() }
} else {
DatabaseFactory.generatePassphrase().also { bytes ->
prefs.edit().putString("db_passphrase",
bytes.joinToString("") { "%02x".format(it) }).apply()
}
}
return DatabaseFactory.create(ctx, pass)
}
}
app/src/main/kotlin/br/com/aspas/contingencia/di/RepositoryModule.kt
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
app/src/main/kotlin/br/com/aspas/contingencia/di/ServiceModule.kt
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
core/data/src/main/kotlin/br/com/aspas/contingencia/core/lease/LeaseManagerImpl.kt
package br.com.aspas.contingencia.core.lease
import br.com.aspas.contingencia.common.Uuid
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.event.payload.TableLeaseAcquiredPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseExpiredPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseForcedPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseReleasedPayload
import br.com.aspas.contingencia.domain.event.payload.TableLeaseRenewedPayload
import br.com.aspas.contingencia.domain.model.Lease
import br.com.aspas.contingencia.domain.repository.EventRepository
import br.com.aspas.contingencia.domain.service.LeaseManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class LeaseManagerImpl @Inject constructor(
private val db: AspasDatabase,
private val eventRepository: EventRepository,
private val json: Json
) : LeaseManager {
override suspend fun acquire(tableId: String, userId: String): LeaseManager.AcquireResult {
val current = db.leaseDao().activeByTable(tableId)
if (current != null && current.expiresAt > System.currentTimeMillis()) {
return LeaseManager.AcquireResult.Busy(current.userId, current.expiresAt)
}
val leaseId = Uuid.new()
val now = System.currentTimeMillis()
val expiresAt = now + LeaseManager.LEASE_TTL_MS
val payload = TableLeaseAcquiredPayload(leaseId, tableId, userId, "self", expiresAt)
eventRepository.append(EventType.TABLE_LEASE_ACQUIRED,
json.encodeToString(TableLeaseAcquiredPayload.serializer(), payload), userId)
return LeaseManager.AcquireResult.Success(Lease(leaseId, tableId, userId, "self",
now, now, expiresAt, "active"))
}
override suspend fun renew(tableId: String, userId: String): Boolean {
val lease = db.leaseDao().activeByTable(tableId) ?: return false
if (lease.userId != userId) return false
val now = System.currentTimeMillis()
val expires = now + LeaseManager.LEASE_TTL_MS
eventRepository.append(EventType.TABLE_LEASE_RENEWED,
json.encodeToString(TableLeaseRenewedPayload.serializer(),
TableLeaseRenewedPayload(lease.leaseId, now, expires)), userId)
return true
}
override suspend fun release(tableId: String, userId: String, reason: String) {
val lease = db.leaseDao().activeByTable(tableId) ?: return
eventRepository.append(EventType.TABLE_LEASE_RELEASED,
json.encodeToString(TableLeaseReleasedPayload.serializer(),
TableLeaseReleasedPayload(lease.leaseId, userId, reason)), userId)
}
override suspend fun forceRelease(tableId: String, managerId: String, reason: String) {
val lease = db.leaseDao().activeByTable(tableId) ?: return
eventRepository.append(EventType.TABLE_LEASE_FORCED,
json.encodeToString(TableLeaseForcedPayload.serializer(),
TableLeaseForcedPayload(lease.leaseId, tableId, managerId, reason)), managerId)
}
override fun observeActive(tableId: String): Flow<Lease?> =
db.leaseDao().observeActiveByTable(tableId).map {
it?.let { Lease(it.leaseId, it.tableId, it.userId, it.deviceId,
it.acquiredAt, it.renewedAt, it.expiresAt, it.status) }
}
override suspend fun sweepExpired() {
val now = System.currentTimeMillis()
db.leaseDao().expired(now).forEach {
eventRepository.append(EventType.TABLE_LEASE_EXPIRED,
json.encodeToString(TableLeaseExpiredPayload.serializer(),
TableLeaseExpiredPayload(it.leaseId, it.tableId, now)), null)
db.leaseDao().markReleased(it.leaseId, "expired", now, null)
}
}
}
app/src/main/kotlin/br/com/aspas/contingencia/ui/theme/Theme.kt
package br.com.aspas.contingencia.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val AspasGreen = Color(0xFF1B4332)
val AspasGreenLight = Color(0xFF2D6A4F)
val AspasSand = Color(0xFFE9C46A)
val AspasClay = Color(0xFFB08968)
val AspasCream = Color(0xFFFAF3E0)
val AspasInk = Color(0xFF1A1A1A)
val StatusFree = Color(0xFF52B788)
val StatusOpen = Color(0xFFF4A261)
val StatusClosing = Color(0xFF457B9D)
private val Light = lightColorScheme(
primary = AspasGreen, onPrimary = AspasCream,
secondary = AspasSand, tertiary = AspasClay,
background = AspasCream, surface = AspasCream, onSurface = AspasInk)
private val Dark = darkColorScheme(
primary = AspasGreenLight, secondary = AspasSand, tertiary = AspasClay)
@Composable
fun AspasTheme(content: @Composable () -> Unit) {
MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
app/src/main/kotlin/br/com/aspas/contingencia/navigation/Routes.kt
package br.com.aspas.contingencia.navigation
object Routes {
const val LOGIN = "login"
const val TABLES = "tables"
const val ORDER = "order/{tableId}"
const val CHECKOUT = "checkout/{tableId}/{orderId}"
const val PRINTERS = "printers"
const val KITCHEN = "kitchen"
fun order(tableId: String) = "order/$tableId"
fun checkout(tableId: String, orderId: String) = "checkout/$tableId/$orderId"
}

app/src/main/kotlin/br/com/aspas/contingencia/navigation/AspasNavHost.kt
package br.com.aspas.contingencia.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.aspas.contingencia.feature.checkout.CheckoutScreen
import br.com.aspas.contingencia.feature.kitchen.KitchenScreen
import br.com.aspas.contingencia.feature.login.LoginScreen
import br.com.aspas.contingencia.feature.order.OrderScreen
import br.com.aspas.contingencia.feature.printers.PrintersScreen
import br.com.aspas.contingencia.feature.tables.TablesScreen
@Composable
fun AspasNavHost(nav: NavHostController, startDestination: String = Routes.LOGIN) {
NavHost(nav, startDestination = startDestination) {
composable(Routes.LOGIN) {
LoginScreen(onSuccess = {
nav.navigate(Routes.TABLES) { popUpTo(Routes.LOGIN) { inclusive = true } } })
}
composable(Routes.TABLES) {
TablesScreen(
onOpen = { tableId -> nav.navigate(Routes.order(tableId)) },
onPrinters = { nav.navigate(Routes.PRINTERS) },
onKitchen = { nav.navigate(Routes.KITCHEN) })
}
composable(Routes.ORDER) {
OrderScreen(
onBack = { nav.popBackStack() },
onCheckout = { tableId, orderId -> nav.navigate(Routes.checkout(tableId, orderId)) })
}
composable(Routes.CHECKOUT) {
CheckoutScreen(
onBack = { nav.popBackStack() },
onClosed = { nav.popBackStack(Routes.TABLES, inclusive = false) })
}
composable(Routes.PRINTERS) { PrintersScreen(onBack = { nav.popBackStack() }) }
composable(Routes.KITCHEN) { KitchenScreen(onBack = { nav.popBackStack() }) }
}
}
app/src/main/kotlin/br/com/aspas/contingencia/feature/login/LoginScreen.kt
package br.com.aspas.contingencia.feature.login
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.common.PinHasher
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.UserEntity
import br.com.aspas.contingencia.domain.model.User
import br.com.aspas.contingencia.domain.repository.UserRepository
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(
private val db: AspasDatabase,
private val userRepository: UserRepository,
private val session: SessionManager
) : ViewModel() {
val users: StateFlow<List<User>> = userRepository.observeActive()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
private val _result = MutableSharedFlow<String?>()
val result: SharedFlow<String?> = _result.asSharedFlow()
fun attempt(userId: String, pin: String) {
viewModelScope.launch {
val user = db.userDao().getById(userId)
val ok = user != null && PinHasher.verify(pin, user.pinSalt, user.pinHash)
if (ok && user != null) session.login(user.userId, user.name, user.role)
_result.emit(if (ok) null else "PIN incorreto")
}
}
fun seedAdminIfMissing() {
viewModelScope.launch {
if (db.userDao().listActive().isEmpty()) {
val salt = PinHasher.newSalt()
db.userDao().upsert(UserEntity("usr-admin", "Admin", "manager",
PinHasher.hash("0000", salt), salt, true, System.currentTimeMillis()))
}
}
}
}
@Composable
fun LoginScreen(onSuccess: () -> Unit, vm: LoginViewModel = hiltViewModel()) {
LaunchedEffect(Unit) { vm.seedAdminIfMissing() }
val users by vm.users.collectAsState()
var selected by remember { mutableStateOf<User?>(null) }
var pin by remember { mutableStateOf("") }
var error by remember { mutableStateOf<String?>(null) }
LaunchedEffect(Unit) {
vm.result.collect { if (it == null) onSuccess() else error = it }
}
Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
Text("ASPAS Contingência", style = MaterialTheme.typography.headlineMedium)
Spacer(Modifier.height(24.dp))
if (selected == null) {
Text("Quem está operando?", style = MaterialTheme.typography.titleMedium)
Spacer(Modifier.height(12.dp))
LazyColumn(Modifier.weight(1f)) {
items(users, key = { it.userId }) { u ->
Card(onClick = { selected = u },
modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
ListItem(headlineContent = { Text(u.name) },
supportingContent = { Text(u.role) })
}
}
}
} else {
Text("Olá, ${selected!!.name}", style = MaterialTheme.typography.titleLarge)
Spacer(Modifier.height(12.dp))
OutlinedTextField(value = pin,
onValueChange = { pin = it.filter(Char::isDigit).take(6) },
label = { Text("PIN") },
visualTransformation = PasswordVisualTransformation(),
singleLine = true)
error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
Spacer(Modifier.height(12.dp))
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
OutlinedButton(onClick = { selected = null; pin = ""; error = null }) { Text("Voltar") }
Button(onClick = { vm.attempt(selected!!.userId, pin) }, enabled = pin.length >= 4) {
Text("Entrar") }
}
}
}
}
app/src/main/kotlin/br/com/aspas/contingencia/feature/tables/TablesScreen.kt
package br.com.aspas.contingencia.feature.tables
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.Table
import br.com.aspas.contingencia.domain.repository.TableRepository
import br.com.aspas.contingencia.domain.usecase.OpenTableUseCase
import br.com.aspas.contingencia.session.SessionManager
import br.com.aspas.contingencia.ui.theme.StatusFree
import br.com.aspas.contingencia.ui.theme.StatusOpen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TablesViewModel @Inject constructor(
private val tableRepository: TableRepository,
private val openTableUseCase: OpenTableUseCase,
private val session: SessionManager
) : ViewModel() {
val tables: StateFlow<List<Table>> = tableRepository.observeAll()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
private val _event = MutableSharedFlow<String>()
val event: SharedFlow<String> = _event.asSharedFlow()
fun open(tableId: String) {
val uid = session.currentUserId() ?: return
viewModelScope.launch {
openTableUseCase(tableId, uid)
.onSuccess { _event.emit(tableId) }
.onFailure { _event.emit("erro: {it.message}") }
}
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
onOpen: (String) -> Unit,
onPrinters: () -> Unit,
onKitchen: () -> Unit,
vm: TablesViewModel = hiltViewModel()
) {
val tables by vm.tables.collectAsState()
val snack = remember { SnackbarHostState() }
var menuOpen by remember { mutableStateOf(false) }
LaunchedEffect(Unit) {
vm.event.collect { onOpen(it) }
}
Scaffold(
topBar = {
TopAppBar(
title = { Text("Mesas") },
actions = {
IconButton(onClick = onKitchen) { Icon(Icons.Default.Restaurant, "Produção") }
IconButton(onClick = onPrinters) { Icon(Icons.Default.Print, "Impressoras") }
IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "Menu") }
DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
DropdownMenuItem(text = { Text("Impressoras") },
onClick = { menuOpen = false; onPrinters() })
DropdownMenuItem(text = { Text("Produção") },
onClick = { menuOpen = false; onKitchen() })
}
}
)
},
snackbarHost = { SnackbarHost(snack) }
) { padding ->
LazyVerticalGrid(
columns = GridCells.Adaptive(minSize = 96.dp),
modifier = Modifier.padding(padding).fillMaxSize().padding(12.dp),
horizontalArrangement = Arrangement.spacedBy(8.dp),
verticalArrangement = Arrangement.spacedBy(8.dp)
) {
items(tables, key = { it.tableId }) { t ->
val bg = when (t.status) {
"free" -> StatusFree
"open" -> StatusOpen
else -> Color.LightGray }
Card(onClick = { vm.open(t.tableId) },
modifier = Modifier.aspectRatio(1f),
colors = CardDefaults.cardColors(containerColor = bg)) {
Column(Modifier.fillMaxSize().padding(8.dp),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.Center) {
Text("{t.number}", style = MaterialTheme.typography.headlineMedium)
Text(t.status, style = MaterialTheme.typography.labelSmall)
}
}
}
}
}
}
app/src/main/kotlin/br/com/aspas/contingencia/feature/order/OrderScreen.kt

package br.com.aspas.contingencia.feature.order
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.CatalogItem
import br.com.aspas.contingencia.domain.model.Order
import br.com.aspas.contingencia.domain.model.OrderItem
import br.com.aspas.contingencia.domain.repository.CatalogRepository
import br.com.aspas.contingencia.domain.repository.OrderRepository
import br.com.aspas.contingencia.domain.usecase.AddOrderItemUseCase
import br.com.aspas.contingencia.domain.usecase.AddOrderNoteUseCase
import br.com.aspas.contingencia.domain.usecase.CancelOrderItemUseCase
import br.com.aspas.contingencia.domain.usecase.SendToProductionUseCase
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
private val orderRepository: OrderRepository,
private val catalogRepository: CatalogRepository,
private val addOrderItem: AddOrderItemUseCase,
private val cancelOrderItem: CancelOrderItemUseCase,
private val sendToProduction: SendToProductionUseCase,
private val addOrderNote: AddOrderNoteUseCase,
private val session: SessionManager,
savedStateHandle: SavedStateHandle
) : ViewModel() {
val tableId: String = savedStateHandle["tableId"] ?: ""
private val _order = MutableStateFlow<Order?>(null)
val order: StateFlow<Order?> = _order.asStateFlow()
val items: StateFlow<List<OrderItem>> = flow {
val o = orderRepository.activeByTable(tableId)
_order.value = o
if (o != null) emitAll(orderRepository.observeItems(o.orderId))
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
val catalog: StateFlow<List<CatalogItem>> = catalogRepository.observeAvailableItems()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
private val _event = MutableSharedFlow<String>()
val event: SharedFlow<String> = _event.asSharedFlow()
fun addItem(item: CatalogItem) {
val o = _order.value ?: return
val uid = session.currentUserId() ?: return
viewModelScope.launch {
addOrderItem(o.orderId, item.itemId, item.name, item.price, userId = uid)
.onSuccess { _event.emit("+ {item.name}") }
.onFailure { _event.emit("erro") }
}
}
fun send(item: OrderItem) {
viewModelScope.launch {
sendToProduction(item)
.onSuccess { _event.emit("$it fichas enviadas") }
.onFailure { _event.emit("erro") }
}
}
fun cancel(item: OrderItem) {
val uid = session.currentUserId() ?: return
viewModelScope.launch {
cancelOrderItem(item, "cancelado pelo operador", uid)
.onSuccess { _event.emit("cancelado") }
.onFailure { _event.emit("erro") }
}
}
fun setNote(note: String) {
val o = _order.value ?: return
val uid = session.currentUserId() ?: return
viewModelScope.launch { addOrderNote(o.orderId, note, uid) }
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(onBack: () -> Unit, onCheckout: (String, String) -> Unit,
vm: OrderViewModel = hiltViewModel()) {
val order by vm.order.collectAsState()
val items by vm.items.collectAsState()
val catalog by vm.catalog.collectAsState()
val snack = remember { SnackbarHostState() }
var showCatalog by remember { mutableStateOf(false) }
var editingNote by remember { mutableStateOf(false) }
var noteText by remember { mutableStateOf("") }
LaunchedEffect(Unit) { vm.event.collect { snack.showSnackbar(it) } }
LaunchedEffect(order?.notes) { noteText = order?.notes ?: "" }
Scaffold(
topBar = {
TopAppBar(
title = { Text("Mesa") },
navigationIcon = { IconButton(onClick = onBack) {
Icon(Icons.Default.ArrowBack, null) } })
},
snackbarHost = { SnackbarHost(snack) },
floatingActionButton = {
FloatingActionButton(onClick = { showCatalog = true }) {
Icon(Icons.Default.Add, "Adicionar")
}
}
) { padding ->
Column(Modifier.padding(padding).fillMaxSize()) {
Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
Row(Modifier.fillMaxWidth().padding(12.dp),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically) {
Text(order?.notes?.takeIf { it.isNotBlank() }
?: "Sem observação da mesa",
style = MaterialTheme.typography.bodyMedium)
TextButton(onClick = {
if (editingNote) { vm.setNote(noteText); editingNote = false }
else editingNote = true
}) { Text(if (editingNote) "Salvar" else "Editar") }
}
}
if (editingNote) {
OutlinedTextField(value = noteText, onValueChange = { noteText = it.take(280) },
modifier = Modifier.fillMaxWidth().padding(12.dp),
placeholder = { Text("Ex.: aniversário, alergia…") }, minLines = 2)
}
LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
items(items, key = { it.itemId }) { it ->
ListItem(
headlineContent = { Text("${it.quantity}x ${it.nameSnapshot}") },
supportingContent = { Text("R$ %.2f".format(it.priceSnapshot * it.quantity)) },
trailingContent = {
Row {
IconButton(onClick = { vm.send(it) }) {
Icon(Icons.Default.Send, "Enviar") }
TextButton(onClick = { vm.cancel(it) }) { Text("X") }
}
})
}
}
order?.let {
Card(Modifier.fillMaxWidth().padding(12.dp)) {
Column(Modifier.padding(12.dp)) {
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("Subtotal"); Text("R %.2f".format(it.subtotal)) }
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("Taxa 10%"); Text("R$ %.2f".format(it.serviceFee)) }
HorizontalDivider()
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("Total", style = MaterialTheme.typography.titleLarge)
Text("R$ %.2f".format(it.total), style = MaterialTheme.typography.titleLarge) }
Spacer(Modifier.height(8.dp))
Button(onClick = { onCheckout(vm.tableId, it.orderId) },
modifier = Modifier.fillMaxWidth()) { Text("Fechar") }
}
}
}
}
}
if (showCatalog) {
ModalBottomSheet(onDismissRequest = { showCatalog = false }) {
LazyColumn(Modifier.fillMaxWidth().padding(12.dp)) {
items(catalog, key = { it.itemId }) { cat ->
ListItem(
headlineContent = { Text(cat.name) },
supportingContent = { Text("R$ %.2f".format(cat.price)) },
modifier = Modifier.clickable {
vm.addItem(cat); showCatalog = false })
HorizontalDivider()
}
}
}
}
}
app/src/main/kotlin/br/com/aspas/contingencia/feature/checkout/CheckoutScreen.kt
package br.com.aspas.contingencia.feature.checkout
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.Order
import br.com.aspas.contingencia.domain.repository.OrderRepository
import br.com.aspas.contingencia.domain.usecase.CloseTableUseCase
import br.com.aspas.contingencia.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CheckoutViewModel @Inject constructor(
private val orderRepository: OrderRepository,
private val closeTableUseCase: CloseTableUseCase,
private val session: SessionManager,
savedStateHandle: SavedStateHandle
) : ViewModel() {
val tableId: String = savedStateHandle["tableId"] ?: ""
private val orderId: String = savedStateHandle["orderId"] ?: ""
val order: StateFlow<Order?> = orderRepository.observeById(orderId)
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
private val _payments = MutableStateFlow<List<Triple<String, Double, String?>>>(emptyList())
val payments: StateFlow<List<Triple<String, Double, String?>>> = _payments.asStateFlow()
private val _event = MutableSharedFlow<String>()
val event: SharedFlow<String> = _event.asSharedFlow()
fun addPayment(method: String, amount: Double) {
_payments.value = _payments.value + Triple(method, amount, null)
}
fun removePayment(i: Int) {
_payments.value = _payments.value.filterIndexed { idx, _ -> idx != i }
}
fun confirm() {
val uid = session.currentUserId() ?: return
val o = order.value ?: return
viewModelScope.launch {
closeTableUseCase(tableId, o, _payments.value, uid)
.onSuccess { _event.emit("fechado") }
.onFailure { _event.emit("erro: {it.message}") }
}
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBack: () -> Unit, onClosed: () -> Unit,
vm: CheckoutViewModel = hiltViewModel()) {
val order by vm.order.collectAsState()
val payments by vm.payments.collectAsState()
val snack = remember { SnackbarHostState() }
var showPayment by remember { mutableStateOf(false) }
LaunchedEffect(Unit) {
vm.event.collect { if (it == "fechado") onClosed() else snack.showSnackbar(it) }
}
Scaffold(
topBar = { TopAppBar(title = { Text("Fechar mesa") },
navigationIcon = { IconButton(onClick = onBack) {
Icon(Icons.Default.ArrowBack, null) } }) },
snackbarHost = { SnackbarHost(snack) }
) { padding ->
Column(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {
order?.let {
Text("Subtotal: R %.2f".format(it.subtotal))
Text("Taxa 10%: R$ %.2f".format(it.serviceFee))
Text("Total: R$ %.2f".format(it.total),
style = MaterialTheme.typography.titleLarge)
}
Spacer(Modifier.height(12.dp))
Text("Pagamentos", style = MaterialTheme.typography.titleSmall)
payments.forEachIndexed { i, p ->
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
Text("{p.first} — R %.2f".format(p.second))
TextButton(onClick = { vm.removePayment(i) }) { Text("x") } }
}
Spacer(Modifier.weight(1f))
Button(onClick = { showPayment = true }, modifier = Modifier.fillMaxWidth()) {
Text("+ Pagamento") }
Spacer(Modifier.height(8.dp))
Button(onClick = { vm.confirm() }, modifier = Modifier.fillMaxWidth(),
enabled = payments.isNotEmpty()) { Text("Confirmar fechamento") }
}
}
if (showPayment) {
var method by remember { mutableStateOf("cash") }
var amount by remember { mutableStateOf("") }
AlertDialog(onDismissRequest = { showPayment = false },
title = { Text("Novo pagamento") },
text = {
Column {
listOf("cash", "credit", "debit", "pix", "voucher").forEach { m ->
Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
RadioButton(selected = method == m, onClick = { method = m })
Text(m)
} }
OutlinedTextField(value = amount, onValueChange = { amount = it },
label = { Text("Valor") })
}
},
confirmButton = {
TextButton(onClick = {
val v = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
if (v > 0) vm.addPayment(method, v); showPayment = false
}) { Text("Adicionar") } },
dismissButton = { TextButton(onClick = { showPayment = false }) { Text("Cancelar") } })
}
}
app/src/main/kotlin/br/com/aspas/contingencia/feature/printers/PrintersScreen.kt
package br.com.aspas.contingencia.feature.printers
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.domain.model.Printer
import br.com.aspas.contingencia.domain.repository.PrinterRepository
import br.com.aspas.contingencia.print.driver.ElginI9Driver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PrintersViewModel @Inject constructor(
private val printerRepository: PrinterRepository
) : ViewModel() {
val printers: StateFlow<List<Printer>> = printerRepository.observeAll()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
fun pingAll() {
viewModelScope.launch {
printers.value.forEach { p ->
val ok = ElginI9Driver(p.ip, p.port).isOnline()
printerRepository.updateStatus(p.printerId,
if (ok) "online" else "offline", System.currentTimeMillis())
}
}
}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintersScreen(onBack: () -> Unit, vm: PrintersViewModel = hiltViewModel()) {
val printers by vm.printers.collectAsState()
LaunchedEffect(Unit) { vm.pingAll() }
Scaffold(topBar = { TopAppBar(title = { Text("Impressoras") },
navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
actions = { IconButton(onClick = { vm.pingAll() }) { Icon(Icons.Default.Refresh, null) } }) }
) { padding ->
LazyColumn(Modifier.padding(padding).fillMaxSize()) {
items(printers, key = { it.printerId }) { p ->
ListItem(
headlineContent = { Text(p.name) },
supportingContent = { Text("{p.ip}:{p.port}") },
leadingContent = {
Box(Modifier.size(12.dp).background(when (p.lastStatus) {
"online" -> Color(0xFF52B788)
"offline" -> Color(0xFFE63946)
else -> Color.Gray })) })
HorizontalDivider()
}
}
}
}
app/src/main/kotlin/br/com/aspas/contingencia/feature/kitchen/KitchenScreen.kt
package br.com.aspas.contingencia.feature.kitchen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.PrintJobEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
@HiltViewModel
class KitchenViewModel @Inject constructor(db: AspasDatabase) : ViewModel() {
val jobs: StateFlow<List<PrintJobEntity>> = db.printJobDao().observeQueued()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
val failed: StateFlow<List<PrintJobEntity>> = db.printJobDao().observeFailed()
.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(onBack: () -> Unit, vm: KitchenViewModel = hiltViewModel()) {
val jobs by vm.jobs.collectAsState()
val failed by vm.failed.collectAsState()
val fmt = remember { SimpleDateFormat("HH:mm:ss", Locale("pt","BR")) }
Scaffold(topBar = { TopAppBar(title = { Text("Produção") },
navigationIcon = { IconButton(onClick = onBack) {
Icon(Icons.Default.ArrowBack, null) } }) }
) { padding ->
Column(Modifier.padding(padding).fillMaxSize()) {
if (failed.isNotEmpty()) {
Card(Modifier.fillMaxWidth().padding(8.dp),
colors = CardDefaults.cardColors(
containerColor = MaterialTheme.colorScheme.errorContainer)) {
Text("${failed.size} impressões falharam",
Modifier.padding(12.dp))
}
}
LazyColumn(Modifier.fillMaxSize()) {
items(jobs, key = { it.printId }) { j ->
ListItem(
headlineContent = { Text("⏳ ${j.printerId}") },
supportingContent = { Text("tentativas: ${j.attempts}") },
trailingContent = { Text(fmt.format(Date(j.createdAt))) })
HorizontalDivider()
}
}
}
}
}
---
============ ASSETS ============
app/src/main/assets/catalog.json
{
"version": 1,
"categories": [
{ "id": "couvert", "name": "Couvert", "print_destination": "couvert", "sort_order": 5 },
{ "id": "entradas", "name": "Entradas", "print_destination": "producao_quente", "sort_order": 10 },
{ "id": "pratos_individuais", "name": "Pratos Individuais", "print_destination": "producao_quente", "sort_order": 30 },
{ "id": "parrilla_mar", "name": "Fogo da Parrilla · Mar", "print_destination": "producao_quente", "sort_order": 40 },
{ "id": "parrilla_carnes", "name": "Fogo da Parrilla · Carnes", "print_destination": "producao_quente", "sort_order": 41 },
{ "id": "parrilla_guarnicoes", "name": "Guarnições", "print_destination": "producao_quente", "sort_order": 42 },
{ "id": "parrilla_acompanhamentos", "name": "Acompanhamentos", "print_destination": "producao_quente", "sort_order": 43 },
{ "id": "sobremesas", "name": "Sobremesas", "print_destination": "sobremesa", "sort_order": 50 },
{ "id": "cervejas", "name": "Cervejas e Chopes", "print_destination": "bar", "sort_order": 60 },
{ "id": "doses", "name": "Doses", "print_destination": "bar", "sort_order": 64 },
{ "id": "soft", "name": "Soft", "print_destination": "bar_soft", "sort_order": 71 },
{ "id": "espumantes", "name": "Espumantes", "print_destination": "vinhos", "sort_order": 80 },
{ "id": "tintos", "name": "Tintos", "print_destination": "vinhos", "sort_order": 84 }
],
"items": [
{ "id": "couvert_padrao", "name": "Couvert", "category_id": "couvert", "price": 26.0, "print_destinations": ["couvert"], "sort_order": 1 },
{ "id": "ostra_brasa_queijos", "name": "Ostra na brasa com queijos catarinenses (4 unid)", "category_id": "entradas", "price": 39.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 1 },
{ "id": "batata_frita_aspas", "name": "Batata Frita do Aspas", "category_id": "entradas", "price": 58.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 2 },
{ "id": "ceviche_aspas", "name": "Ceviche Aspas", "category_id": "entradas", "price": 102.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 3 },
{ "id": "mignon_tournedos", "name": "Mignon Tournedos", "category_id": "pratos_individuais", "price": 156.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 1 },
{ "id": "camarao_citrico", "name": "Camarão Cítrico", "category_id": "pratos_individuais", "price": 158.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 2 },
{ "id": "peixe_file_500g", "name": "Peixe Filé (500g)", "category_id": "parrilla_mar", "price": 329.0, "print_destinations": ["cozinha","parrilla"], "parrilla_bundle_json": "{"included_guarnicoes":1,"included_acompanhamentos":2}", "sort_order": 1 },
{ "id": "peixe_inteiro_100g", "name": "Peixe Inteiro", "category_id": "parrilla_mar", "price": 32.0, "pricing_mode": "per_100g", "weight_max_g": 5000, "weight_tiers_json": "[{"max_weight_g":1000,"free_items":3},{"max_weight_g":1500,"free_items":4},{"max_weight_g":2000,"free_items":6},{"max_weight_g":2500,"free_items":7},{"max_weight_g":3000,"free_items":8},{"max_weight_g":3500,"free_items":9},{"max_weight_g":4000,"free_items":11},{"max_weight_g":4500,"free_items":12},{"max_weight_g":5000,"free_items":15}]", "print_destinations": ["cozinha","parrilla"], "sort_order": 2 },
{ "id": "mix_frutos_do_mar", "name": "Mix de Frutos do Mar", "category_id": "parrilla_mar", "price": 420.0, "print_destinations": ["cozinha","parrilla"], "parrilla_bundle_json": "{"included_guarnicoes":1,"included_acompanhamentos":2}", "sort_order": 3 },
{ "id": "flat_iron_400g", "name": "Flat Iron (400g)", "category_id": "parrilla_carnes", "price": 290.0, "print_destinations": ["cozinha","parrilla"], "parrilla_bundle_json": "{"included_guarnicoes":1,"included_acompanhamentos":2}", "sort_order": 1 },
{ "id": "t_bone_800g", "name": "T-Bone (800g)", "category_id": "parrilla_carnes", "price": 400.0, "print_destinations": ["cozinha","parrilla"], "parrilla_bundle_json": "{"included_guarnicoes":1,"included_acompanhamentos":2}", "sort_order": 2 },
{ "id": "ancho_550g", "name": "Ancho (550g)", "category_id": "parrilla_carnes", "price": 315.0, "print_destinations": ["cozinha","parrilla"], "parrilla_bundle_json": "{"included_guarnicoes":1,"included_acompanhamentos":2}", "sort_order": 3 },
{ "id": "guarn_bernaise", "name": "Bernoise", "category_id": "parrilla_guarnicoes", "price": 18.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 1 },
{ "id": "guarn_molho_queijos", "name": "Molho de Queijos Catarinenses", "category_id": "parrilla_guarnicoes", "price": 18.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 2 },
{ "id": "acomp_arroz_branco", "name": "Arroz Branco", "category_id": "parrilla_acompanhamentos", "price": 26.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 1 },
{ "id": "acomp_batata_frita", "name": "Batata Frita", "category_id": "parrilla_acompanhamentos", "price": 26.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 2 },
{ "id": "acomp_pure_batata", "name": "Purê de Batata com Alho Assado", "category_id": "parrilla_acompanhamentos", "price": 26.0, "print_destinations": ["cozinha","parrilla"], "sort_order": 3 },
{ "id": "sob_bolo_gelado_coco", "name": "Bolo Gelado de Coco", "category_id": "sobremesas", "price": 46.0, "print_destinations": ["sobremesa"], "sort_order": 1 },
{ "id": "sob_panna_cotta", "name": "Panna Cotta de Baunilha", "category_id": "sobremesas", "price": 48.0, "print_destinations": ["sobremesa"], "sort_order": 2 },
{ "id": "chope_brahma_360", "name": "Chope Brahma 360ml", "category_id": "cervejas", "price": 20.0, "print_destinations": ["bar"], "sort_order": 1 },
{ "id": "chope_patagonia_360", "name": "Chope Patagonia 360ml", "category_id": "cervejas", "price": 24.0, "print_destinations": ["bar"], "sort_order": 2 },
{ "id": "corona_extra", "name": "Corona Extra Long Neck", "category_id": "cervejas", "price": 20.0, "print_destinations": ["bar"], "sort_order": 3 },
{ "id": "dose_gin_bombay", "name": "Gin Bombay", "category_id": "doses", "price": 40.0, "print_destinations": ["bar"], "sort_order": 1 },
{ "id": "dose_vodka_absolut", "name": "Vodka Absolut", "category_id": "doses", "price": 36.0, "print_destinations": ["bar"], "sort_order": 2 },
{ "id": "soft_agua_sc_sem_gas", "name": "Água Santa Catarina sem Gás", "category_id": "soft", "price": 12.0, "print_destinations": ["bar_soft"], "sort_order": 1 },
{ "id": "soft_coca_cola", "name": "Coca-Cola", "category_id": "soft", "price": 11.0, "print_destinations": ["bar_soft"], "sort_order": 2 },
{ "id": "soft_suco_natural", "name": "Suco Natural", "category_id": "soft", "price": 18.0, "print_destinations": ["bar_soft"], "sort_order": 3 },
{ "id": "soft_cafe_illy", "name": "Café Expresso Illy", "category_id": "soft", "price": 11.0, "print_destinations": ["bar_soft"], "sort_order": 4 },
{ "id": "vin_esp_chandon_brut", "name": "Chandon Brut", "category_id": "espumantes", "price": 219.0, "print_destinations": ["vinhos"], "sort_order": 1 },
{ "id": "vin_tin_petirrojo", "name": "Petirrojo Reserva Carménère", "category_id": "tintos", "price": 149.0, "print_destinations": ["vinhos"], "sort_order": 1 }
],
"printers": [
{ "id": "bar", "name": "Bar", "ip": "192.168.0.101", "header_label": "BAR" },
{ "id": "bar_soft", "name": "Bar Soft", "ip": "192.168.0.102", "header_label": "BAR SOFT" },
{ "id": "cozinha", "name": "Cozinha", "ip": "192.168.0.103", "header_label": "COZINHA" },
{ "id": "parrilla", "name": "Parrilla", "ip": "192.168.0.104", "header_label": "PARRILLA" },
{ "id": "sobremesa", "name": "Sobremesa", "ip": "192.168.0.105", "header_label": "SOBREMESA" },
{ "id": "vinhos", "name": "Vinhos", "ip": "192.168.0.106", "header_label": "VINHOS" },
{ "id": "couvert", "name": "Couvert", "ip": "192.168.0.107", "header_label": "COUVERT" }
]
}
---
============ COMANDOS ============
Rode nesta ordem:
```bash
# 1. Verificar catálogo
jq empty app/src/main/assets/catalog.json
# 2. Compilar módulos um a um
./gradlew :core:common:compileDebugKotlin
./gradlew :core:domain:compileDebugKotlin
./gradlew :core:data:compileDebugKotlin
./gradlew :core:print:compileDebugKotlin
./gradlew :core:sync:compileDebugKotlin
./gradlew :app:compileDebugKotlin
# 3. Build completo
./gradlew :app:assembleDebug
# 4. Instalar
adb install -r app/build/outputs/apk/dev/debug/app-dev-debug.apk































