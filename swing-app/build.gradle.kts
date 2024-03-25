plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

group = "red.razvan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation(libs.kotlinx.serialization.json)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}