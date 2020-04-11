package com.kunal.onlineconsultation.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.login.Login;
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

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class GetCity extends AppCompatActivity implements LocationAdapter.ItemClickListener {
RecyclerView recyclerView_location;
LocationAdapter locationAdapter;
List<String> location_list;

    Task<LocationSettingsResponse> result ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_city);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                checkLocationPermission();
            }
        });



        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            LocationManager locationManager = (android.location.LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//            MyLocationManager myLocationManager = new MyLocationManager(locationManager, geocoder, this);
//            location_list.add(myLocationManager.getCurrentCity());
//            Log.d("location",myLocationManager.getCurrentCity());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
        }
        else {
            Context context = getApplicationContext();
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                location_list = new ArrayList<>();
                String city = hereLocation(location.getLatitude(),location.getLongitude());
                location_list.add(city);
            }catch (Exception e){
                e.printStackTrace();
                FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
            }
//            try {
//                location_list = new ArrayList<>();
//                MyLocationManager myLocationManager = new MyLocationManager(locationManager,geocoder,GetCity.this);
//                String city = myLocationManager.getCurrentCity();
//                location_list.add(city);
//                Log.i("CITY",city);
//                if (city.equals("")){
//                    FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
//                }
//                else {
//                    FancyToast.makeText(getApplicationContext(),city,FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                FancyToast.makeText(getApplicationContext(),"Not Found!",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
//            }

        }



        recyclerView_location = findViewById(R.id.recyclerView_location);
        recyclerView_location.setLayoutManager(new LinearLayoutManager(this));
        locationAdapter = new LocationAdapter(this,location_list);
        locationAdapter.setClickListener(this);
        recyclerView_location.setAdapter(locationAdapter);

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
                                        GetCity.this,
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
    public void onItemClick(View view, int position) {
        try {
            if (getIntent().getExtras().get("home").toString().equals("home")){
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference location_ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HashMap<String,Object> map = new HashMap<>();
                map.put("location",locationAdapter.getItem(position));
                location_ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(GetCity.this, MainActivity.class);
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
            Intent intent = new Intent(GetCity.this, Intro.class);
            intent.putExtra("location",locationAdapter.getItem(position));
            startActivity(intent);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (getIntent().getExtras().get("home").toString().equals("home")){
                Intent intent = new Intent(GetCity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception e){
            Intent intent = new Intent(GetCity.this, com.kunal.onlineconsultation.Location.Location.class);
            startActivity(intent);
            finish();
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
                        FancyToast.makeText(getApplicationContext(),"GPS Enabled by user",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                        Intent intentt = new Intent(GetCity.this,GetCity.class);
                        startActivity(intentt);
                        finish();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        try {
                            if (getIntent().getExtras().get("home").toString().equals("home"))
                            {
                                FancyToast.makeText(getApplicationContext()," User rejected GPS request",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                                Intent intent = new Intent(GetCity.this, com.kunal.onlineconsultation.Location.Location.class);
                                intent.putExtra("home","home");
                                startActivity(intent);
                                finish();
                            }
                        }catch (Exception e){
                            FancyToast.makeText(getApplicationContext()," User rejected GPS request",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                            Intent intent = new Intent(GetCity.this, com.kunal.onlineconsultation.Location.Location.class);
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
