plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.spmForKmp) apply false
    alias(libs.plugins.vanniktechMavenPublish) apply false
}

val manualMavenRepository = layout.buildDirectory.dir("manual-maven-repository")
val releaseVersion = libs.versions.kmp.maps.compose.get()
val publishingModules = listOf(":kmp-maps-compose", ":kmp-maps-compose-utils")

val cleanManualMavenRepository by tasks.registering(Delete::class) {
    delete(manualMavenRepository)
}

subprojects {
    if (path in publishingModules) {
        pluginManager.withPlugin("com.vanniktech.maven.publish") {
            extensions.configure<PublishingExtension> {
                repositories.maven {
                    name = "manualBundle"
                    url = manualMavenRepository.get().asFile.toURI()
                }
            }
            tasks.withType<PublishToMavenRepository>().configureEach {
                if (name.endsWith("ToManualBundleRepository")) {
                    dependsOn(cleanManualMavenRepository)
                }
            }
        }
    }
}

tasks.register<Zip>("bundleMavenCentral") {
    group = "publishing"
    description = "Builds a signed Maven Central upload ZIP locally without uploading it."
    dependsOn(publishingModules.map { "$it:publishAllPublicationsToManualBundleRepository" })
    from(manualMavenRepository) {
        include("io/github/hysonhou/*/$releaseVersion/**")
    }
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    archiveFileName.set("kmp-maps-compose-$releaseVersion-maven-central.zip")
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    doFirst {
        val artifacts = manualMavenRepository.get().asFile.walkTopDown()
            .filter { it.isFile && it.extension in setOf("pom", "module", "aar", "jar", "klib") }
            .toList()
        check(artifacts.isNotEmpty()) { "No Maven artifacts were generated." }
        artifacts.forEach { artifact ->
            check(file("${artifact.path}.asc").isFile) {
                "Missing PGP signature for ${artifact.name}. Configure signing before creating the upload bundle."
            }
            listOf("md5", "sha1").forEach { checksum ->
                check(file("${artifact.path}.$checksum").isFile) {
                    "Missing $checksum checksum for ${artifact.name}."
                }
            }
        }
    }
}
