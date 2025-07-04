@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.kapt)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.alonalbert.pad.app"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.alonalbert.pad.app"
    minSdk = 33
    //noinspection OldTargetApi
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "com.alonalbert.pad.app.HiltTestRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  kotlinOptions {
    jvmTarget = "21"
    freeCompilerArgs = listOf("-Xcontext-receivers")
  }

  buildFeatures {
    compose = true
    aidl = false
    buildConfig = false
    renderScript = false
    shaders = false
  }

  packaging {
    resources {
      excludes += "/META-INF/NOTICE.md"
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(project(":app:app-data"))
  implementation(project(":app:app-annotations"))
  implementation(project(":shared"))

  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.splashscreen)

  // Hilt Dependency Injection
  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)

  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)

  // Ktor
  implementation(libs.ktor.client.android)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.logging)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.client.content.negotiation)

  // Timber
  implementation(libs.timber)

  implementation(libs.androidx.datastore.preferences)
}
