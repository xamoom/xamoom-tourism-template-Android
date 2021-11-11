# **xamoom-tourism-template-Android**

## Firebase

1. Register your app in Firebase.
2. Upload ```google-services.json``` to the app folder.
3. Change ```applicationId``` in ```build.gradle``` file to appId registered in Firebase.

## Mapbox
1. Create file with name ```gradle.properties``` in the project.gradle.gradleUserHomeDir folder ($USER_HOME/.gradle). Learn more from gradle official documentation. https://docs.gradle.org/current/userguide/directory_layout.html
2. In ```gradle.properties``` create property named ```MAPBOX_DOWNLOADS_TOKEN``` and set mapbox downloads token.
3. In ```developer-config.xml``` set your ```mapbox_access_token```.

## Project configuration
1. Open file ```gen-strings.xml```.
2. Set your mapbox ```maps_key```.
3. Set ```beacon_major```, ```custom_webclient```, ```deep_link```,  (contact Xamoom support).
4. If you want to use Google Analytics, set ```tracking_id``` in format like UA-********
5. Set ```is_background_image``` = true, if you want to use background image for tabbar and navigation bar.
6. Set ```enable_quiz_feature``` = true, if you want to use quiz app feature (contact Xamoom support).
7. Set ```custom intern_urls```, your app can access.
8. Set ```non_intern_urls```, if you want to block custom urls.

## Gradle (Gradle JDK = 11.0)
1. Open ```gradle.properties``` file in project.
2. Change params ```RELEASE_STORE_FILE```, ```RELEASE_STORE_PASSWORD```, ```RELEASE_KEY_ALIAS```, ```RELEASE_KEY_PASSWORD``` for app signing.

## XamoomSDK
1. Open ```build.gradle``` in the project tree.
2. Find line ```implementation 'com.github.xamoom:xamoom-android-sdk:dev-SNAPSHOT' ```. XamoomSDK is implemented in the app as dependency. If you want to modify XamoomSDK, clone it from github repo https://github.com/xamoom/xamoom-android-sdk and implement as project module.
