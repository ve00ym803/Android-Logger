# Android Logger Library

An easy-to-use logging library for Android applications to enhance debugging and streamline log management.

[![](https://jitpack.io/v/ve00ym803/Android-Logger.svg)](https://jitpack.io/#ve00ym803/Android-Logger)

## Features
- Simple and intuitive API for logging.
- Categorized logs for better debugging.
- Lightweight and efficient.

---

## Getting Started

### 1. Add JitPack Repository
To use this library, add the JitPack repository to your project-level `build.gradle` or `settings.gradle` file:

```gradle
// In settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

### 2. Add Logger Library
Next, add the library dependency in your app-level `build.gradle` file:

```gradle
//In build.gradle.kts (app-level)
dependencies {
    implementation("com.github.ve00ym803:Android-Logger:<LATEST-VERSION>")
}
```

### 3. Create Logger Instance

You can create a `Logger` instance by providing the necessary configurations either through Dependency Injection (Dagger / Hilt) or by directly instantiating it. Here's how you can do it:

```kotlin
@Provides
@Singleton
fun providesLogConfigurations(application: Application): LogConfiguration {
    val mediaDirs = application.externalMediaDirs
    val internalFilesDir = application.filesDir
    val loggerPath = "/logger_" + BuildConfig.VERSION_NAME
    return LogModel.builder()
        .setLoggerBaseDirectory(
            if (!mediaDirs.isNullOrEmpty()) mediaDirs[0].absolutePath + loggerPath else internalFilesDir.absolutePath + loggerPath
        )
        .setLoggerIsFileLogEnabled(BuildConfig.FILE_LOGGING_STATUS)
        .setLoggerIsSystemLogEnabled(BuildConfig.SYSTEM_LOGGING_STATUS)
        .setLoggerMaxFileSize(1024)
        .setLoggerPackageName(BuildConfig.APPLICATION_ID)
        .setLoggerFileName("log")
        .setLoggerDisposalMethod(
            LogDisposalMethod.DisposeLogsByArchive(
                14,
                application.filesDir.absolutePath + "/logBackUp"
            )
        )
        .build()
}

@Provides
@Singleton
fun providesLoggerModule(
    @ApplicationContext context: Context,
    logConfiguration: LogConfiguration?
): Logger {
    val lgr = Logger(context)
    lgr.setConfigurations(logConfiguration)
    return lgr
}
```
### 4. Using the Logger

To use the `Logger` in your `Activity`, `Fragment`, `ViewModel`, or `Service`, follow these steps:

1. **Import the Logger:**

```kotlin
import com.android.logger.Logger
```

2. **Inject the Logger instance:**
   
```kotlin
@Inject
lateinit var logger: Logger
```

3. Using the Logger Instance
Once the `Logger` instance is injected, you can use it in your classes like this:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Example of using the logger
    logger.d("Debug", "This is a debug log message!")
    logger.i("Info", "This is an info log message!")
    logger.e("Error", "This is an error log message!")
    logger.w("Warning", "This is a warning log message!")
    logger.v("Verbose", "This is a verbose log message!")
}
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
