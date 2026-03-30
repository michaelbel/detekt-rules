plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
}

group = "org.michaelbel"
version = "0.1.2-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.named("check") {
    dependsOn(
        ":detekt-rules:check",
        ":sample:check",
    )
}
