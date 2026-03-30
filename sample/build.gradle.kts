plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
}

val detektRulesJar = project(":detekt-rules")
    .tasks
    .named<Jar>("jar")
    .flatMap { it.archiveFile }

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}

detekt {
    config.setFrom(project.file("detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    dependsOn(":detekt-rules:jar")
}

dependencies {
    detektPlugins(files(detektRulesJar))
}
