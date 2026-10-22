plugins {
    id("automotive.android.library")
    id("automotive.android.hilt")
}

android {
    namespace = "com.android.car.automotiveapp.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
}
