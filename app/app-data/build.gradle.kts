import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.androidx.room)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

room {
  schemaDirectory("$projectDir/schemas")
}

configure<LibraryExtension> {
  namespace = "com.alonalbert.pad.app.data"
  compileSdk = 37

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

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }

  packaging {
    resources {
      excludes += "/META-INF/NOTICE.md"
      excludes += "/META-INF/LICENSE.md"
      excludes += "/META-INF/LICENSE.txt"
      excludes += "/META-INF/NOTICE.txt"
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_21)
    freeCompilerArgs.add("-Xcontext-parameters")
  }
}

dependencies {
  implementation(project(":app:app-annotations"))
  implementation(project(":shared"))

  // Core
  implementation(libs.androidx.core.ktx)
  implementation(libs.javax.inject)

  // Room
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

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
  ksp(libs.hilt.compiler)

  implementation(libs.androidx.datastore.preferences)

  // Junit
  testImplementation(libs.junit4)

  // Robolectric
  testImplementation(libs.robolectric)

  // Truth
  testImplementation(libs.truth)
}
