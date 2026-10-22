plugins {
    id("automotive.android.feature")
}

android {
    namespace = "com.android.car.automotiveapp.feature.home"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
}
