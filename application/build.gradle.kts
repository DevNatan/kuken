plugins {
    application
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

application {
    mainClass.set("org.katan.Application")
}

dependencies {
    runtimeOnly(libs.log4j.slf4j2)
    implementation(projects.core)
    implementation(projects.http.httpServer)
    implementation(projects.services.idService)
    implementation(projects.services.unitService)
    implementation(projects.services.accountService)
    implementation(projects.services.authService)
    implementation(projects.services.networkService)
    implementation(projects.services.instanceService)
    implementation(projects.services.cacheService)
    implementation(projects.services.databaseService)
    implementation(projects.services.hostFsService)
    implementation(projects.services.blueprintService)
    implementation(projects.services.projectsService)
    implementation(libs.yoki)
    implementation(libs.log4j.core)
    implementation(libs.koin.core)
    implementation(libs.ktx.coroutines.core)
    implementation(libs.exposed.core)
    implementation(libs.ktx.serialization.json)
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass.get()
            attributes["Implementation-Version"] = project.version
        }
    }

    shadowJar {
        archiveBaseName.set("katan")
        // TODO Set classifier based on environment
        archiveClassifier.set(null as String?)
    }
}
