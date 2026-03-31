import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.plugins.signing.Sign

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    `java-library`
    `maven-publish`
    signing
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}

val pomName = providers.gradleProperty("POM_NAME").orElse("detekt-rules")
val pomDescription = providers.gradleProperty("POM_DESCRIPTION").orElse("Custom detekt rules by Michael Bel")
val pomUrl = providers.gradleProperty("POM_URL").orElse("https://github.com/michaelbel/detekt-rules")
val pomScmUrl = providers.gradleProperty("POM_SCM_URL").orElse("https://github.com/michaelbel/detekt-rules")
val pomScmConnection = providers.gradleProperty("POM_SCM_CONNECTION")
    .orElse("scm:git:https://github.com/michaelbel/detekt-rules.git")
val pomScmDeveloperConnection = providers.gradleProperty("POM_SCM_DEVELOPER_CONNECTION")
    .orElse("scm:git:ssh://git@github.com/michaelbel/detekt-rules.git")
val pomLicenseName = providers.gradleProperty("POM_LICENSE_NAME").orElse("UNSPECIFIED")
val pomLicenseUrl = providers.gradleProperty("POM_LICENSE_URL").orElse("https://example.invalid/license")
val pomDeveloperId = providers.gradleProperty("POM_DEVELOPER_ID").orElse("michaelbel")
val pomDeveloperName = providers.gradleProperty("POM_DEVELOPER_NAME").orElse("Michael Bel")
val pomDeveloperEmail = providers.gradleProperty("POM_DEVELOPER_EMAIL")
val pomDeveloperUrl = providers.gradleProperty("POM_DEVELOPER_URL").orElse("https://github.com/michaelbel")
val signingKeyId = providers.gradleProperty("signingKeyId")
val signingKey = providers.gradleProperty("signingKey")
val signingPassword = providers.gradleProperty("signingPassword")

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
                name.set(pomName)
                description.set(pomDescription)
                url.set(pomUrl)

                licenses {
                    license {
                        name.set(pomLicenseName)
                        url.set(pomLicenseUrl)
                    }
                }

                developers {
                    developer {
                        id.set(pomDeveloperId)
                        name.set(pomDeveloperName)
                        email.set(pomDeveloperEmail.orNull)
                        url.set(pomDeveloperUrl)
                    }
                }

                scm {
                    url.set(pomScmUrl)
                    connection.set(pomScmConnection)
                    developerConnection.set(pomScmDeveloperConnection)
                }
            }
        }
    }
}

signing {
    val key = signingKey.orNull
    val password = signingPassword.orNull

    if (!key.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKeyId.orNull, key, password)
    }

    sign(publishing.publications["mavenJava"])
}

tasks.withType<Sign>().configureEach {
    onlyIf("signingKey is configured") {
        !signingKey.orNull.isNullOrBlank()
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
        val missing = buildList {
            if (providers.gradleProperty("sonatypeUsername").orNull.isNullOrBlank()) {
                add("sonatypeUsername (Central Portal token username)")
            }
            if (providers.gradleProperty("sonatypePassword").orNull.isNullOrBlank()) {
                add("sonatypePassword (Central Portal token password)")
            }
            if (signingKey.orNull.isNullOrBlank()) {
                add("signingKey (ASCII-armored private key)")
            }
            if (signingPassword.orNull.isNullOrBlank()) {
                add("signingPassword (private key password)")
            }
            if (providers.gradleProperty("POM_LICENSE_NAME").orNull.isNullOrBlank()) {
                add("POM_LICENSE_NAME (for example: The Apache License, Version 2.0)")
            }
            if (providers.gradleProperty("POM_LICENSE_URL").orNull.isNullOrBlank()) {
                add("POM_LICENSE_URL (for example: https://www.apache.org/licenses/LICENSE-2.0.txt)")
            }
        }

        check(missing.isEmpty()) {
            buildString {
                appendLine("Remote publishing is not fully configured.")
                appendLine("Missing Gradle properties:")
                missing.forEach { appendLine(" - $it") }
            }
        }
    }
}
