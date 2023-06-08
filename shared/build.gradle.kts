plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.jakarta.persistence)
//    implementation(libs.jakarta.persistence)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
