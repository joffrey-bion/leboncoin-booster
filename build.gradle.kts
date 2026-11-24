plugins {
    kotlin("jvm") version "2.4.21"
    kotlin("plugin.serialization") version "2.4.21"
    application
}

group = "org.hildan.leboncoin"

application {
    mainClass.set("org.hildan.leboncoin.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kaml)
}
