package com.example.safethrough.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.app.NotificationCompat;
import com.example.safethrough.R;
import com.example.safethrough.SafeThroughApplication;
import com.google.android.gms.maps.model.LatLng;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    
    // Constants
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    public static final int NOTIFICATION_ID = 1001;
    public static final int MAP_ZOOM_LEVEL = 15;
    
    // Date formatter
    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * Check if the device has an active internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Convert Location to LatLng
     */
    public static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    /**
     * Format location coordinates as string
     */
    public static String formatLocation(Location location) {
        if (location == null) return "";
        return String.format(Locale.getDefault(), 
            "%.6f, %.6f", 
            location.getLatitude(), 
            location.getLongitude());
    }

    /**
     * Get formatted current timestamp
     */
    public static String getCurrentTimestamp() {
        return dateFormat.format(new Date());
    }

    /**
     * Show emergency notification
     */
    public static void showEmergencyNotification(Context context, String title, String message) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, 
            SafeThroughApplication.EMERGENCY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Calculate distance between two locations in meters
     */
    public static float calculateDistance(Location location1, Location location2) {
        if (location1 == null || location2 == null) return -1;
        return location1.distanceTo(location2);
    }

    /**
     * Format distance in appropriate units
     */
    public static String formatDistance(float distanceInMeters) {
        if (distanceInMeters < 0) return "Unknown";
        if (distanceInMeters < 1000) {
            return String.format(Locale.getDefault(), "%.0f m", distanceInMeters);
        } else {
            return String.format(Locale.getDefault(), "%.1f km", distanceInMeters / 1000);
        }
    }

    /**
     * Validate emergency request inputs
     */
    public static boolean validateEmergencyInputs(String emergencyType, 
                                                String location, 
                                                String details) {
        return !emergencyType.isEmpty() && 
               !location.isEmpty() && 
               !details.isEmpty();
    }

    /**
     * Get appropriate error message based on error type
     */
    public static String getErrorMessage(Context context, Throwable error) {
        if (!isNetworkAvailable(context)) {
            return "No internet connection. Please check your network settings.";
        }
        return error.getMessage();
    }
}
