# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers   #指定不去忽略非公共库的类成员
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keepattributes Exceptions,InnerClasses

-dontwarn
-printmapping out.map
-renamesourcefileattribute SourceFile
-keepparameternames

-ignorewarnings                # 抑制警告

#----------------------------------------------------------------------------


#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}




#---------------------------------第三方库------------------------------------------------------
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}

#okhttp
-dontwarn com.network.okhttp.**
-keep class com.network.okhttp.**{*;}
-keep interface com.network.okhttp.**{*;}

#okio
-dontwarn com.network.okio.**
-keep class com.network.okio.**{*;}
-keep interface com.network.okio.**{*;}

-keep class org.apache.http**{*;}


# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class com.third.google.gson.** { *;}
# Application classes that will be serialized/deserialized over Gson




#这句非常重要，主要是滤掉 com.demo.demo.bean包下的所有.class文件不进行混淆编译,com.demo.demo是你的包名

#---------------------------------自己代码-----------------------------------------------------

-keep class com.lib.commonlib.CommonLib{
    public *;
}

-keep class com.lib.commonlib.uuid.UUIDGenerator{
    public *;
}

-keep class com.lib.commonlib.net.param.**{
    *;
}
-keep class com.lib.commonlib.net.**{
    public *;
}

-keep class com.lib.commonlib.http.**{
    public *;
}
-keep interface com.lib.commonlib.http.HttpRequestCallback{
    *;
}

-keep class com.lib.commonlib.dispatch.MessageDispatch{
    public *;
}

-keep interface com.lib.commonlib.dispatch.MessageCallback{
    *;
}

-keep interface com.lib.commonlib.dispatch.MessageTimeout{
    *;
}

-keep class com.lib.commonlib.log.**{
    public *;
}
-keepclassmembers class com.lib.commonlib.log.LogStorage$LogFileCallback {
   public *;
}

-keep class com.lib.commonlib.broadcast.**{
    *;
}

-keep class com.lib.commonlib.utils.**{
    *;
}

-keep class com.lib.commonlib.task.**{
    *;
}

-libraryjars E:\AndroidDev\sdk\platforms\android-23\android.jar






