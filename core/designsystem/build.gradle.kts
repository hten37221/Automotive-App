plugins {
    id("automotive.android.library")
    id("automotive.android.compose")
}

android {
    namespace = "com.android.car.automotiveapp.core.designsystem"
}

dependencies {
    implementation(project(":core:common"))

    androidTestImplementation(libs.androidx.junit)
}
