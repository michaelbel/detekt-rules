plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    `java-library`
    `maven-publish`
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}

java {
    withSourcesJar()
    withJavadocJar()
}

detekt {
    config.setFrom(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    compileOnly(libs.detekt.api)

    detektPlugins(libs.detekt.ruleauthors)

    testImplementation(libs.detekt.test)
    testImplementation(libs.junit.jupiter)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "detekt-rules"

            pom {
                name.set("detekt-rules")
                description.set("Custom detekt rules by Michael Bel")
            }
        }
    }
}
