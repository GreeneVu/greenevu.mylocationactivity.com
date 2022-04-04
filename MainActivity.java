package com.example.mylocationandactivityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationClient;
    double latitude;
    double longitude;
    String address = "";
    String activity = "";
    String dataTableName = "";
    String key = "";
//    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    34/*REQUEST_PERMISSION_REquest_code*/);
        }
        createLocationRequest();

        Bundle extras = getIntent().getExtras();
        dataTableName = extras.getString("dataTableName");

        if (extras.getSerializable("myLocation") != null) {
            MyLocation myLocation = (MyLocation) extras.getSerializable("myLocation");
            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
            address = myLocation.getAddress();
            activity = myLocation.getActivity();
            key = myLocation.getKey();

            TextView textViewLat = findViewById(R.id.textViewLatitude);
            textViewLat.setText(String.valueOf(latitude));
            TextView textViewLng = findViewById(R.id.textViewLongitude);
            textViewLng.setText(String.valueOf(longitude));
            TextView textViewAddress = findViewById(R.id.editTextAddress);
            textViewAddress.setText(address);
            TextView editTextActivity = findViewById(R.id.editTextActivity);
            editTextActivity.setText(activity);

        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        long ms = 10000; // milliseconds
        // Sets the desired interval for active location updates.
        locationRequest.setInterval(ms);
        // Sets the fastest rate for active location updates.
        locationRequest.setFastestInterval(ms/2);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @SuppressLint("MissingPermission")
    public void getLocationAddress(View view) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        fusedLocationClient.requestLocationUpdates(locationRequest,
                                new LocationCallback(), Looper.myLooper());
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            // Logic to handle location object
                            TextView textViewLatitude = findViewById(R.id.textViewLatitude);
                            textViewLatitude.setText("latitude: " + location.getLatitude());

                            TextView textViewLongitude = findViewById(R.id.textViewLongitude);
                            textViewLongitude.setText("longitude: " + location.getLongitude());

                            TextView textViewAddress = findViewById(R.id.editTextAddress);
                            address = getStreetAddress(location.getLatitude(), location.getLongitude());
                            textViewAddress.setText("address: " + address);

                        }
                    }
                });
    }

    private String getStreetAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String streetAddress = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address address = addresses.get(0);
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    streetAddress += address.getAddressLine(i) + "\n";
                }
            } else {
                streetAddress = "Unknown";
            }
        } catch (Exception e) {
            streetAddress = "Service not available.";
            e.printStackTrace();
        }
        return streetAddress;
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
//        intent.putExtra("latitude", latitude);
//        intent.putExtra("longitude", longitude);
        intent.putExtra("dataTableName", dataTableName);
        startActivity(intent);
    }

    public void saveLocationActivity(View view) {
        //get activity
        EditText editTextActivity = findViewById(R.id.editTextActivity);
        String activity = editTextActivity.getText().toString();

        //get Time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm:ss a" );
        String lastUpdateTime = formatter.format(new Date());

        // database access
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference(dataTableName);

        // add location and address
        key = dbRef.push().getKey();
        dbRef.child(key).child("latitude").setValue(latitude);
        dbRef.child(key).child("longitude").setValue(longitude);
        dbRef.child(key).child("address").setValue(address);
        dbRef.child(key).child("lastUpdateTime").setValue(lastUpdateTime);
        dbRef.child(key).child("activity").setValue(activity);
    }
}