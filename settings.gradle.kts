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
