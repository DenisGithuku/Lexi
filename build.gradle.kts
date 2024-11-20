import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply {
        plugin("com.diffplug.spotless")
        plugin("jacoco")
    }

    configure<SpotlessExtension> {
        val lintVersion = "0.49.0"

        kotlin {
            target("**/*.kt")
            ktlint(lintVersion)
                .editorConfigOverride(
                    mapOf(
                        "ktlint_standard_package-name" to "disabled",
                        "ktlint_standard_filename" to "disabled",
                        "ij_kotlin_allow_trailing_comma" to "false",
                        "ij_kotlin_allow_trailing_comma_on_call_site" to "false",
                    ),
                )
            ktfmt().googleStyle().configure {
                it.setRemoveUnusedImport(true)
            }
            licenseHeaderFile("${project.rootProject.projectDir}/license-header.txt")
            trimTrailingWhitespace()
            indentWithTabs(2)
            indentWithSpaces(4)
            toggleOffOn()
        }

        kotlinGradle {
            target("*.gradle.kts")
            ktlint(lintVersion)
                .editorConfigOverride(
                    mapOf(
                        "ij_kotlin_allow_trailing_comma" to "false",
                        "ij_kotlin_allow_trailing_comma_on_call_site" to "false",
                    )
                )
            ktfmt().googleStyle()
            indentWithTabs(2)
            indentWithSpaces(4)

        }

        format("xml") {
            target("**/*.xml")
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
        }

    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                when (requested.group) {
                    "org.jacoco" -> useVersion("0.8.11")
                }
            }
        }
    }

    tasks.withType<Test> {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*", "**org.hl7*")
        }
    }
}