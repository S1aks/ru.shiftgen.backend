val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val postgresqlVersion: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "ru.shiftgen"
version = "0.0.1"

application {
    mainClass.set("ru.shiftgen.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.2.4")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.2.4")
    implementation("io.ktor:ktor-server-cio-jvm:2.2.4")
    implementation("io.ktor:ktor-network-tls-certificates:2.2.4")

    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.4")

    implementation("io.ktor:ktor-server-auth:2.2.4")
    implementation("io.ktor:ktor-server-auth-jwt:2.2.4")
    implementation("io.ktor:ktor-server-call-logging:2.2.4")

    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")

    implementation("org.postgresql:postgresql:42.5.4")

    implementation("ch.qos.logback:logback-classic:1.4.7")

    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.0")
}
