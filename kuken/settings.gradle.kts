enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "kuken"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(
    "kuken",
//    "application",
//    "core",
//    "http:http-shared",
//    "http:http-server",
//    "http:http-test",
//    "services:auth-service",
//    "services:id-service",
//    "services:unit-service",
//    "services:instance-service",
//    "services:account-service",
//    "services:database-service",
//    "services:network-service",
//    "services:cache-service",
//    "services:fs-service",
//    "services:host-fs-service",
//    "services:blueprint-service",
//    "services:projects-service",
//    "services:nodes-service"
)
