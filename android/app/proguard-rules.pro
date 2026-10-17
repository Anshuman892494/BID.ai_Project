# Proguard rules for Bid.ai Android App
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.bidai.app.data.model.** { *; }
