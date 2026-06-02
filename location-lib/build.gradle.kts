plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    `maven-publish`
}

group = "com.abyxcz.viewpoint.location"
version = "1.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
    
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    
    sourceSets {
        val commonMain = getByName("commonMain")
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        
        val appleMain = maybeCreate("appleMain").apply {
            dependsOn(commonMain)
        }
        
        listOf(
            "iosX64", "iosArm64", "iosSimulatorArm64",
            "watchosArm32", "watchosArm64", "watchosSimulatorArm64"
        ).forEach { targetName ->
            maybeCreate("${targetName}Main").dependsOn(appleMain)
        }
    }
}

android {
    namespace = "com.abyxcz.viewpoint.location"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        // Kotlin Multiplatform plugin automatically creates publications for targets.
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tjmtic/KMP-Locations")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: System.getenv("GPR_USER") ?: project.findProperty("gpr.user") as String? ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: System.getenv("GPR_KEY") ?: project.findProperty("gpr.key") as String? ?: ""
            }
        }
    }
}

