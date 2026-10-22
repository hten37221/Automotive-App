plugins {
    id("automotive.android.library")
    id("automotive.android.hilt")
}

android {
    namespace = "com.android.car.automotiveapp.core.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
}
