package com.example.safethrough;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private MaterialButton emergencyButton;
    private MaterialButton vehicleBreakdownButton;
    private MaterialButton fuelOutageButton;
    private MaterialButton medicalEmergencyButton;

    private final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        checkPermissions();
    }

    private void initializeViews() {
        emergencyButton = findViewById(R.id.emergency_button);
        vehicleBreakdownButton = findViewById(R.id.vehicle_breakdown_button);
        fuelOutageButton = findViewById(R.id.fuel_outage_button);
        medicalEmergencyButton = findViewById(R.id.medical_emergency_button);

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupClickListeners() {
        emergencyButton.setOnClickListener(v -> launchEmergencyAssistance("SOS"));
        vehicleBreakdownButton.setOnClickListener(v -> launchEmergencyAssistance("Vehicle Breakdown"));
        fuelOutageButton.setOnClickListener(v -> launchEmergencyAssistance("Fuel Outage"));
        medicalEmergencyButton.setOnClickListener(v -> launchEmergencyAssistance("Medical Emergency"));
    }

    private void launchEmergencyAssistance(String emergencyType) {
        if (!checkPermissions()) {
            return;
        }

        Intent intent = new Intent(this, EmergencyAssistanceActivity.class);
        intent.putExtra("emergency_type", emergencyType);
        startActivity(intent);
    }

    private boolean checkPermissions() {
        boolean allPermissionsGranted = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, 
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Snackbar.make(findViewById(android.R.id.content),
                    "All permissions granted", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                    "Permissions are required for full functionality", 
                    Snackbar.LENGTH_LONG)
                    .setAction("Settings", v -> openAppSettings())
                    .show();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
