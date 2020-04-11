package com.kunal.onlineconsultation.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kunal.onlineconsultation.Fragments.HomeFragment;
import com.kunal.onlineconsultation.Fragments.MyLocationManager;
import com.kunal.onlineconsultation.Intro.Intro;
import com.kunal.onlineconsultation.MainActivity;
import com.kunal.onlineconsultation.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity {
    String[] cities;
    ArrayAdapter<String> adapter;
    ListView citylist;

    List<String> location_list;
    LinearLayout getCurrentLocation;
    Task<LocationSettingsResponse> result ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        citylist = findViewById(R.id.citylist);
        cities = getResources().getStringArray(R.array.india_top_places);
        adapter = new ArrayAdapter<String>(this, R.layout.cities, R.id.citytv, cities);
        citylist.setAdapter(adapter);

        citylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               try {
                   if (getIntent().getExtras().get("home").toString().equals("home")){
                       FirebaseDatabase database = FirebaseDatabase.getInstance();
                       DatabaseReference location_ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                       HashMap<String,Object> map = new HashMap<>();
                       map.put("location",cities[position]);
                       location_ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Intent intent = new Intent(Location.this,MainActivity.class);
                                   startActivity(intent);
                                   finish();
                                   FancyToast.makeText(getApplicationContext(),"Location Updated Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                               }
                               else {
                                   FancyToast.makeText(getApplicationContext(),"Failed to update location",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                               }
                           }
                       });
                   }
               }catch (Exception e){
                   Intent intent = new Intent(Location.this, Intro.class);
                   intent.putExtra("location",cities[position]);
                   startActivity(intent);
               }

            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                checkLocationPermission();
            }
        });

        getCurrentLocation = findViewById(R.id.getcurrentlocation);
        getCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
                }
                else {
                    Context context = getApplicationContext();
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereLocation(location.getLatitude(),location.getLongitude());
                        try {
                            if (getIntent().getExtras().get("home").toString().equals("home")){
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference location_ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                HashMap<String,Object> map = new HashMap<>();
                                map.put("location",city);
                                location_ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(Location.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            FancyToast.makeText(getApplicationContext(),"Location Updated Successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                        }
                                        else {
                                            FancyToast.makeText(getApplicationContext(),"Failed to update location",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                        }
                                    }
                                });
                            }
                        }catch (Exception e){
                            Intent intent = new Intent(Location.this,Intro.class);
                            intent.putExtra("location",city);
                            startActivity(intent);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    }

                }
            }
        });
    }

    private String hereLocation(double lat, double lon){
        String cityName ="";
        String countryName="";
        String stateName = "";
        String address = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat,lon,10);
            if (addresses.size()>0){
                for (Address adr : addresses){
                    if (adr.getLocality()!=null && adr.getLocality().length()>0 && adr.getCountryName() != null && adr.getCountryName().length()>0 && adr.getAddressLine(0)!=null && adr.getAddressLine(0).length()>0){
                        cityName = adr.getLocality();
                        stateName = adr.getAdminArea();
                        countryName = adr.getCountryName();
                        address = adr.getAddressLine(0);
                        break;
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    public void checkLocationPermission(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        Location.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (getIntent().getExtras().get("home").toString().equals("home")){
                Intent intent = new Intent(Location.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else if (getIntent().getExtras().get("home").toString().equals("splash")){
                Intent intent = new Intent(Location.this, Location.class);
                intent.putExtra("home","splash");
                startActivity(intent);
                finish();
            }
        }catch (Exception e){

        }
    }

    protected static MyLocationManager mLocationManager;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    android.location.LocationManager locationManager = (android.location.LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    mLocationManager = new MyLocationManager(locationManager, geocoder, this);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 2000, 0, mLocationManager.locationListener);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            if (getIntent().getExtras().get("home").toString().equals("home")){
                                FancyToast.makeText(getApplicationContext(),"GPS Enabled by user",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                                Intent intentt = new Intent(Location.this,Location.class);
                                intentt.putExtra("home","home");
                                startActivity(intentt);
                                finish();
                            }
                            else if (getIntent().getExtras().get("home").toString().equals("splash")){
                                FancyToast.makeText(getApplicationContext(),"GPS Enabled by user",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                                Intent intentt = new Intent(Location.this,Location.class);
                                intentt.putExtra("home","splash");
                                startActivity(intentt);
                                finish();
                            }
                        }catch (Exception e){

                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        try {
                            if (getIntent().getExtras().get("home").toString().equals("home"))
                            {
                                FancyToast.makeText(getApplicationContext()," User rejected GPS request",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                                Intent intent = new Intent(Location.this, MainActivity.class);
                                intent.putExtra("home","home");
                                startActivity(intent);
                                finish();
                            }
                        }catch (Exception e){
                            FancyToast.makeText(getApplicationContext()," User rejected GPS request",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                            Intent intent = new Intent(Location.this, com.kunal.onlineconsultation.Location.Location.class);
                            startActivity(intent);
                            finish();
                        }

                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
