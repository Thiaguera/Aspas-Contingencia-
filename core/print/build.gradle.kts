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
