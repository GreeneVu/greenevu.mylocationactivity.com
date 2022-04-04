package com.example.mylocationandactivityapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String dataTableName;
    double latitude;
    double longitude;
    ArrayList<MyLocation> myLocationList = new ArrayList<MyLocation>();
    ArrayList<Marker> markerList = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        dataTableName = extras.getString("dataTableName");
//        latitude = extras.getDouble("latitude");
//        longitude = extras.getDouble("longitude");

        readDataFromRealtimeDatabase();
    }

    private void readDataFromRealtimeDatabase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference(dataTableName);
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                double latitude = (double) snapshot.child("latitude").getValue();
                double longitude = (double) snapshot.child("longitude").getValue();
                String address = (String) snapshot.child("address").getValue();
                String lastUpdateTime = (String) snapshot.child("lastUpdateTime").getValue();
                String activity = (String) snapshot.child("activity").getValue();

                 MyLocation myLocation = new MyLocation(latitude, longitude, address, lastUpdateTime, activity);
                 myLocation.setKey(snapshot.getKey());

                 myLocationList.add(myLocation);

//                 LatLng latLng = new LatLng(latitude, longitude);
//                 locationList.add(latLng);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                PolylineOptions options = new PolylineOptions().geodesic(true);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                int counter = 0;
                for (MyLocation location: myLocationList){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.addPolyline(options.add(latLng));
                    builder.include(latLng);

                    final Marker marker  = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(location.getActivity())
                    .snippet("Where: "+ location.getAddress()+"When "+ location.getLastUpdateTime())
                    );

                    if (counter >0) {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }

                    counter++;
                    markerList.add(marker);

                }
                LatLngBounds bounds = builder.build();

                int padding = 20;
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,padding));
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = (TextView) infoWindow.findViewById(R.id.textViewTitle);
                TextView snippet = (TextView) infoWindow.findViewById(R.id.textViewSnippet);
                ImageView image = (ImageView) infoWindow.findViewById(R.id.imageView);

                for (Marker myMarker : markerList) {
                    if (marker.getId().equals(myMarker.getId())) {
                        title.setText(marker.getTitle());
                        snippet.setText(marker.getSnippet());
                        image.setImageDrawable(getResources()
                                .getDrawable(R.mipmap.ic_uc, getTheme()));
                    }
                }
                return infoWindow;
            }
        });

        mMap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker){

                for (int i = 0; i< myLocationList.size(); i++){
                    if (marker.getId().equals(markerList.get(i).getId())){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("myLocation", myLocationList.get(i));
                        intent.putExtra("dataTableName", dataTableName);
                        startActivity(intent);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_listview1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.action_listview2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}