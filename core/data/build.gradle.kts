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
defaultConfig { minSdk = 26; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions { jvmTarget = "17" }
}
dependencies {
api(project(":core:domain"))
implementation(project(":core:common"))
api(libs.room.runtime)
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
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test:runner:1.6.2")
androidTestImplementation("androidx.test:core:1.6.1")
}
