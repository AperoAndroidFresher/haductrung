package com.example.haductrung.home.settingScreen

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

fun setAppLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocales(LocaleList(locale))
    } else {
        config.locale = locale
    }
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}