import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = Constants.packageName
    compileSdk = Constants.compileSdk

    defaultConfig {
        applicationId = Constants.packageName
        minSdk = Constants.minSdk
        targetSdk = Constants.targetSdk
        versionCode = Constants.versionCode
        versionName = Constants.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "NATIVE_APP_KEY",
            gradleLocalProperties(rootDir).getProperty("native.app.key"),
        )

        manifestPlaceholders["NATIVE_APP_KEY"] =
            gradleLocalProperties(rootDir).getProperty("nativeAppKey")

        buildConfigField(
            "String",
            "IAMPORT_BASE_URL",
            gradleLocalProperties(rootDir).getProperty("iamport.base.url"),
        )
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                gradleLocalProperties(rootDir).getProperty("dev.base.url"),
            )
            buildConfigField(
                "String",
                "AMPLITUDE_KEY",
                gradleLocalProperties(rootDir).getProperty("dev.amplitude.key"),
            )
        }
        release {
            buildConfigField(
                "String",
                "BASE_URL",
                gradleLocalProperties(rootDir).getProperty("base.url"),
            )
            buildConfigField(
                "String",
                "AMPLITUDE_KEY",
                gradleLocalProperties(rootDir).getProperty("amplitude.key"),
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:buy"))
    implementation(project(":feature:main"))
    implementation(project(":feature:sell"))
    implementation(project(":feature:setting"))

    KotlinDependencies.run {
        implementation(kotlin)
        implementation(coroutines)
        implementation(jsonSerialization)
    }

    AndroidXDependencies.run {
        implementation(coreKtx)
        implementation(appCompat)
        implementation(hilt)
        implementation(workManager)
        implementation(hiltWorkManager)
    }

    KaptDependencies.run {
        kapt(hiltCompiler)
        kapt(hiltWorkManagerCompiler)
    }

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }

    ThirdPartyDependencies.run {
        implementation(platform(okHttpBom))
        implementation(okHttp)
        implementation(okHttpLoggingInterceptor)
        implementation(retrofit)
        implementation(retrofitJsonConverter)
        implementation(timber)
    }

    KakaoDependencies.run {
        implementation(user)
    }
}
