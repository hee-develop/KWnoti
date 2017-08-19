# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ilmag\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# --------------------------------------------------------------------------------
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
# --------------------------------------------------------------------------------

# *** Proguard에서 제외될 패키지 ***
-dontwarn javax.annotation.**
-dontwarn javax.xml.stream.**

# Retrofit2 & Okio
-dontnote retrofit2.Platform
-dontwarn retrofit2.**
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-dontwarn okio.**
-keep class retrofit2.** { *; }
-keep class retrofit2.converter.simplexml.** { *; }
-keep class org.simpleframework.xml.** { *; }

-keepclassmembers class kr.hee.kwnoti.** { *; }

# UCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

# JSoup
-keeppackagenames org.jsoup.nodes

# ZXing
-keeppackagenames com.google.zxing