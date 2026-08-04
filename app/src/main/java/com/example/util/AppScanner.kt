package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isSystem: Boolean,
    var isSelected: Boolean = false
) {
    fun getComposeBitmap(): ImageBitmap? {
        return try {
            icon?.toBitmap(96, 96)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}

object AppScanner {
    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val apps = mutableListOf<AppInfo>()

        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.packageName
            if (pkgName == context.packageName) continue // Skip our own app

            val label = resolveInfo.loadLabel(pm).toString()
            val icon = try {
                resolveInfo.loadIcon(pm)
            } catch (e: Exception) {
                null
            }
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            apps.add(
                AppInfo(
                    packageName = pkgName,
                    appName = label,
                    icon = icon,
                    isSystem = isSystem,
                    isSelected = false
                )
            )
        }

        return apps.sortedBy { it.appName.lowercase() }
    }
}
