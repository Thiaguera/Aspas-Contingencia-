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
implementation("javax.inject:javax.inject:1")
}
