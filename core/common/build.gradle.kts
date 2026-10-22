plugins {
    id("automotive.android.library")
    id("automotive.android.hilt")
}

android {
    namespace = "com.android.car.automotiveapp.core.common"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
}
