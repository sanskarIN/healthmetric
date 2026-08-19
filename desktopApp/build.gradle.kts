import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

group = "io.github.sanskarin.healthmetric"
version = "0.1.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "io.github.sanskarin.healthmetric.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HealthMetric"
            packageVersion = "0.1.0"
            description = "Privacy-first adult health measurement calculator"
            vendor = "Sanskar"

            macOS {
                packageVersion = "1.0.0"
            }
        }
    }
}
