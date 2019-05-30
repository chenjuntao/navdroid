# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#包名不混合大小写
-dontusemixedcaseclassnames

#混淆时记录日志
-verbose

#不要进行混淆的类及方法
-keep class cjt.astar.AStar { public *; }
-keep class cjt.astar.CjtGraph { public *; }
-keep class cjt.astar.CjtNode { public *; }
-keep class cjt.astar.CjtEdge { public *; }

#-keep class cjt.astar.AStar {
#    public static boolean initV8();
#    public static void releaseV8();
#    public static void astarReadGraph(CjtGraph);
#    public static List<CjtNode> astarNavRoad(CjtGraph, String, String);
#}