plugins {
    `java-library`
    id("velocity-checkstyle") apply false
    id("velocity-spotless") apply false
    id("maven-publish")
}

val repoType = if (version.toString().contains("SNAPSHOT")) "snapshots" else "releases"

subprojects {
    apply<JavaLibraryPlugin>()

    apply(plugin = "velocity-checkstyle")
    apply(plugin = "velocity-spotless")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    if (project.name.contains("api")) {
        publishing {
            repositories {
                mavenLocal()
                maven {
                    name = "gommeRepo"
                    url = uri("https://repo.gomme.dev/repository/$repoType/")
                    credentials(PasswordCredentials::class)
                }
            }
            publications {
                create<MavenPublication>("java") {
                    from(components["java"])
                }
            }
        }
    }

    dependencies {
        testImplementation(rootProject.libs.junit)
    }

    testing.suites.named<JvmTestSuite>("test") {
        useJUnitJupiter()
        targets.all {
            testTask.configure {
                reports.junitXml.required = true
            }
        }
    }
}
