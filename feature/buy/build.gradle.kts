import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "co.orange.buy"
    compileSdk = Constants.compileSdk

    defaultConfig {
        minSdk = Constants.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "IAMPORT_CODE",
            gradleLocalProperties(rootDir).getProperty("iamport.code"),
        )
    }
    buildTypes {
        debug {
            buildConfigField(
                "String",
                "PAYMENT_UID",
                gradleLocalProperties(rootDir).getProperty("payment.test.uid"),
            )
        }
        release {
            buildConfigField(
                "String",
                "PAYMENT_UID",
                gradleLocalProperties(rootDir).getProperty("payment.uid"),
            )
        }
    }

    compileOptions {
        sourceCompatibility = Versions.javaVersion
        targetCompatibility = Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Versions.jvmVersion
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    KotlinDependencies.run {
        implementation(kotlin)
        implementation(coroutines)
        implementation(jsonSerialization)
        implementation(dateTime)
    }

    AndroidXDependencies.run {
        implementation(coreKtx)
        implementation(appCompat)
        implementation(constraintLayout)
        implementation(fragment)
        implementation(startup)
        implementation(legacy)
        implementation(security)
        implementation(hilt)
        implementation(lifeCycleKtx)
        implementation(lifecycleJava8)
        implementation(splashScreen)
        implementation(workManager)
        implementation(hiltWorkManager)
    }

    KaptDependencies.run {
        kapt(hiltCompiler)
        kapt(hiltWorkManagerCompiler)
    }

    GoogleDependencies.run {
        implementation(materialDesign)
    }

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }

    ThirdPartyDependencies.run {
        implementation(coil)
        implementation(timber)
        implementation(lottie)
    }

    JitpackDependencies.run {
        implementation(iamport)
    }

    FirebaseDependencies.run {
        implementation(platform(firebaseBom))
        implementation(crashlytics)
        implementation(analytics)
    }
}
