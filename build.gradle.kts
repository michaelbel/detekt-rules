plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.nexus.publish)
}

group = "io.github.michaelbel"
version = "1.2.0-alpha01"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

tasks.named("check") {
    dependsOn(
        ":detekt-rules:check",
    )
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}
