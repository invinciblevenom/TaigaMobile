import java.util.Properties
import com.android.build.api.dsl.AndroidSourceSet

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

val composeVersion = "1.1.1"

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"

    namespace = "io.eugenethedev.taigamobile"

    defaultConfig {
        applicationId = namespace!!
        minSdk = 21
        targetSdk = 31
        versionCode = 30
        versionName = "2.0"
        project.base.archivesName.set("TaigaMobile-$versionName")
    }


    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    lint { 
        abortOnError = false
    }
}

dependencies {
    // Enforce correct kotlin version for all dependencies
    implementation(enforcedPlatform(kotlin("bom")))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation(kotlin("reflect"))

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.0")

    // ============================================================================================
    // CAREFUL WHEN UPDATING COMPOSE RELATED DEPENDENCIES - THEY CAN USE DIFFERENT COMPOSE VERSION!
    // ============================================================================================

    // Main Compose dependencies
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    // Material You
    implementation("androidx.compose.material3:material3:1.0.0-alpha09")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    // compose activity
    implementation("androidx.activity:activity-compose:1.4.0")
    // view model support
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    // compose constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // Accompanist
    val accompanistVersion = "0.23.1"
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")

    // Coil
    implementation("io.coil-kt:coil-compose:1.3.2")

    // Navigation Component (with Compose)
    implementation("androidx.navigation:navigation-compose:2.5.0-rc01")

    // Paging (with Compose)
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")

    // Coroutines
    val coroutinesVersion = "1.6.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // Moshi
    val moshiVersion = "1.13.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // Retrofit 2
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    // OkHttp
    val okHttpVersion = "4.9.3"
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    // Dagger 2
    val daggerVersion = "2.42"
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Markdown support (Markwon)
    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:image-coil:$markwonVersion")

    // Compose material dialogs (color picker)
    implementation("io.github.vanpra.compose-material-dialogs:color:0.7.0")

    // since we need to connect to test db instance
    val postgresDriverVersion = "42.3.6"
    testRuntimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    androidTestRuntimeOnly("org.postgresql:postgresql:$postgresDriverVersion")

    // manual json parsing when filling test instance
    implementation("com.google.code.gson:gson:2.9.0")

}

tasks.register<Exec>("launchTestInstance") {
    commandLine("../taiga-test-instance/launch-taiga.sh")
}

tasks.register<Exec>("stopTestInstance") {
    commandLine("../taiga-test-instance/stop-taiga.sh")
}
