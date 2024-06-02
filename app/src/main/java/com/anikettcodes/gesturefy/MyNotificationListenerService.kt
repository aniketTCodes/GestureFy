package com.anikettcodes.gesturefy

import android.content.Context
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat

class MyNotificationListenerService : NotificationListenerService() {

    companion object{
        fun isEnabled(context: Context):Boolean{
            return NotificationManagerCompat
                .getEnabledListenerPackages(context)
                .contains(context.packageName)
        }
    }
}
