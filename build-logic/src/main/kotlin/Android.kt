
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

fun Project.configureAndroid(
    namespace: String,
    androidOptions: AndroidOptions
) {
  plugins.apply("com.android.library")
  extensions.findByName("android")?.apply {
    this as CommonExtension<*, *, *, *, *>

    compileSdk = getCatalogVersion("android.sdkversion.compile").toInt()
    this.namespace = namespace

    defaultConfig {
      minSdk = if (androidOptions.withCompose) {
        getCatalogVersion("android.sdkversion.min").toInt()
      } else {
        getCatalogVersion("android.sdkversion.compose.min").toInt()
      }
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (this is LibraryExtension) {
      @Suppress("UnstableApiUsage")
      testOptions.targetSdk = getCatalogVersion("android.sdkversion.target").toInt()
    }

    if (androidOptions.withCompose) {
      buildFeatures {
        compose = true
      }

      composeOptions {
        kotlinCompilerExtensionVersion = getCatalogVersion("compose.compiler")
      }
    }
  }
}