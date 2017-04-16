package com.juniperphoton.myersplash.extension

import android.content.Context
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Build
import android.view.Display
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.WindowManager

fun Context.getDpi(): Float {
    return resources.displayMetrics.density
}

fun Context.getDimenInPixel(valueInDP: Int): Int {
    return (valueInDP * getDpi()).toInt()
}

fun Context.hasNavigationBar(): Boolean {
    var hasBackKey = try {
        KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    } catch (e: Exception) {

    }
    val size = getNavigationBarSize()
    return size.y > 0
}

fun Context.getNavigationBarSize(): Point {
    val appUsableSize = getAppUsableScreenSize()
    val realScreenSize = getRealScreenSize()

    // navigation bar on the right
    if (appUsableSize.x < realScreenSize.x) {
        return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
    }

    // navigation bar at the bottom
    if (appUsableSize.y < realScreenSize.y) {
        return Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
    }

    // navigation bar is not present
    return Point()
}

fun Context.getAppUsableScreenSize(): Point {
    val display = (getSystemService(android.content.Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

fun Context.getRealScreenSize(): Point {
    val windowManager = getSystemService(android.content.Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    if (Build.VERSION.SDK_INT >= 17) {
        display.getRealSize(size)
    } else if (Build.VERSION.SDK_INT >= 14) {
        try {
            size.x = Display::class.java.getMethod("getRawWidth").invoke(display) as Int
            size.y = Display::class.java.getMethod("getRawHeight").invoke(display) as Int
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    return size
}

fun Context.usingWifi(): Boolean {
    val manager = getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = manager.activeNetworkInfo
    return info?.type == ConnectivityManager.TYPE_WIFI
}

fun Context.getVersionCode(): Int {
    return try {
        val manager = packageManager
        val info = manager.getPackageInfo(packageName, 0)
        return info.versionCode
    } catch (e: Exception) {
        e.printStackTrace()
        return -1
    }
}

fun Context.getVersionName(): String? {
    return try {
        val manager = packageManager
        val info = manager.getPackageInfo(packageName, 0)
        return info.versionName
    } catch (e: Exception) {
        return null
    }
}