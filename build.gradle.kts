plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

group = "com.vayikra"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.h2database.h2)
    implementation(libs.h2database.r2dbc)
    implementation(libs.logback.classic)
    implementation(libs.postgresql)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.jbcrypt)

    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
}

tasks.named<JavaExec>("run") {
    val envFile = file(".env")
    if (envFile.exists()) {
        envFile
            .readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .forEach { line ->
                val (key, value) = line.split("=", limit = 2)
                environment(key.trim(), value.trim())
            }
    }
}

ktlint {
    version.set("1.5.0")
    android.set(false)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
    }
    additionalEditorconfig.set(
        mapOf("ij_kotlin_imports_layout" to "*"),
    )
}
