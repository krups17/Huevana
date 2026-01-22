import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.versions)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

group = "org.example"
version = "1.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.json)
    implementation(libs.datetime)
    implementation(libs.slf4j)

    testImplementation(kotlin("test"))
    implementation("com.github.sarxos:webcam-capture:0.3.12")
    implementation("org.openpnp:opencv:4.7.0-0")
    implementation(compose.desktop.uiTestJUnit4)

    // added for supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.6"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-cio:3.3.1")


    // added for palette matching
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")

    // added for chatbot
    implementation("org.json:json:20230227")
    implementation(compose.materialIconsExtended)
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "org.example.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Deb) // Specify desired formats
            packageName = "Huevana App"
            packageVersion = "1.0.0"
        }

    }
}