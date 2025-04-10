package com.example.safethrough;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class SafeThroughApplication extends Application {
    public static final String EMERGENCY_CHANNEL_ID = "emergency_channel";
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        
        // Initialize NetworkConfig with base URL
        NetworkConfig.getInstance().updateBaseUrl(BuildConfig.API_BASE_URL);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Emergency notifications channel
            NotificationChannel emergencyChannel = new NotificationChannel(
                EMERGENCY_CHANNEL_ID,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            emergencyChannel.setDescription("Notifications for emergency situations");
            emergencyChannel.enableVibration(true);
            emergencyChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            emergencyChannel.enableLights(true);

            // Register the channel with the system
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(emergencyChannel);
        }
    }
}
