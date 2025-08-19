plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    val ktorVersion = "3.2.3"
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0")
    implementation("com.charleskorn.kaml:kaml:0.92.0")
}
