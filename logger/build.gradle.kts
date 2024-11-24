plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish") // to publish library
}

val libraryVersionName = "1.1" // Define the version at the top level

android {
    namespace = "com.android.logger"
    compileSdk = 35

    defaultConfig {
        minSdk = 29
        buildConfigField("double", "LIBRARY_VERSION", libraryVersionName)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            consumerProguardFiles("proguard-rules.pro")
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17)) // Ensure consistent Java version
        }
    }

    buildFeatures {
        buildConfig = true
    }

    libraryVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "logger-${variant.name}-${libraryVersionName}.aar"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.ve00ym803"
            artifactId = "Android-Logger"
            version = libraryVersionName

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    testImplementation("junit:junit:4.13.2")
}
