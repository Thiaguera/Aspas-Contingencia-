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
debug { applicationIdSuffix = ".recovery"; versionNameSuffix = "-recovery" }
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
