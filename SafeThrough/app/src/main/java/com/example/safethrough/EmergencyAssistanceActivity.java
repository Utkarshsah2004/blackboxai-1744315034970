package com.example.safethrough;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyAssistanceActivity extends AppCompatActivity {
    private AutoCompleteTextView emergencyTypeSpinner;
    private TextInputEditText locationInput;
    private TextInputEditText detailsInput;
    private MaterialButton submitButton;
    private MapView mapView;
    private Location currentLocation;

    private static final String[] EMERGENCY_TYPES = {
        "Medical Emergency",
        "Vehicle Breakdown",
        "Fuel Outage",
        "Safety Concern",
        "Other"
    };

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OpenStreetMap configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_emergency_assistance);

        initializeViews();
        setupSpinner();
        setupSubmitButton();
        setupMap();
        checkLocationPermission();
    }

    private void initializeViews() {
        emergencyTypeSpinner = findViewById(R.id.emergency_type_spinner);
        locationInput = findViewById(R.id.location_input);
        detailsInput = findViewById(R.id.details_input);
        submitButton = findViewById(R.id.submit_button);
        mapView = findViewById(R.id.map);

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            EMERGENCY_TYPES
        );
        emergencyTypeSpinner.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> submitEmergencyRequest());
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, 
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        // For demo purposes, setting a default location (New York)
        updateLocation(40.7128, -74.0060);
    }

    private void updateLocation(double latitude, double longitude) {
        currentLocation = new Location("");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
        
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapView.getController().setCenter(startPoint);

        // Add marker
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Current Location");
        mapView.getOverlays().clear();
        mapView.getOverlays().add(marker);
        
        // Update location input
        String locationText = String.format("%.6f, %.6f", latitude, longitude);
        locationInput.setText(locationText);
    }

    private void submitEmergencyRequest() {
        String emergencyType = emergencyTypeSpinner.getText().toString();
        String location = locationInput.getText().toString();
        String details = detailsInput.getText().toString();

        // Validate inputs
        if (emergencyType.isEmpty()) {
            emergencyTypeSpinner.setError("Please select emergency type");
            return;
        }
        if (location.isEmpty()) {
            locationInput.setError("Location is required");
            return;
        }

        submitButton.setEnabled(false);
        EmergencyRequest request = new EmergencyRequest(emergencyType, location, details);
        
        NetworkConfig.getInstance()
            .getEmergencyApiService()
            .submitEmergencyRequest(request)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, 
                                     @NonNull Response<ResponseBody> response) {
                    submitButton.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(EmergencyAssistanceActivity.this, 
                            getString(R.string.help_on_way), 
                            Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(EmergencyAssistanceActivity.this, 
                            "Failed to submit request. Please try again.", 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    submitButton.setEnabled(true);
                    Toast.makeText(EmergencyAssistanceActivity.this, 
                        "Error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, 
                                         @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && 
            grantResults.length > 0 && 
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
}
