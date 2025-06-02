plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.androidx.room)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.kapt)
  alias(libs.plugins.ksp)
}

room {
  schemaDirectory("$projectDir/schemas")
}

android {
  namespace = "com.alonalbert.pad.app.data"
  compileSdk = 34

  defaultConfig {
    minSdk = 33

    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
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

  @Suppress("UnstableApiUsage")
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  implementation(project(":app:app-annotations"))
  implementation(project(":shared"))

  // Core
  implementation(libs.androidx.core.ktx)
  implementation(libs.javax.inject)
//    testImplementation(libs.androidx.test.core.ktx)
//    testImplementation(libs.androidx.test.ext)
//    testImplementation(libs.androidx.test.rules)

  // Room
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
//    testImplementation(libs.androidx.room.testing)

  // Ktor
  implementation(libs.ktor.client.android)
  implementation(libs.ktor.client.auth)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.logging)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.client.content.negotiation)

  // Timber
  implementation(libs.timber)

  // Hilt Dependency Injection
  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)

  implementation(libs.androidx.datastore.preferences)

  // Junit
  testImplementation(libs.junit4)

  // Robolectric
  testImplementation(libs.robolectric)

  // Truth
  testImplementation(libs.truth)
}