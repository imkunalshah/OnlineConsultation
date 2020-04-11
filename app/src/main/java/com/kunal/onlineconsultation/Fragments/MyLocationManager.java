package com.kunal.onlineconsultation.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MyLocationManager extends AppCompatActivity {
    public LocationManager locationManager;
    public LocationListener locationListener;
    Geocoder geocoder;
    Location currentLocation;
    String currentAddress = "";



    String currentCity = "";
    Activity context;
    private static final double INF = 400000000;

    public MyLocationManager(LocationManager locationManager, final Geocoder geocoder, Activity context) {
        this.locationManager = locationManager;
        this.context = context;
        this.geocoder = geocoder;
        currentLocation = new Location(LocationManager.GPS_PROVIDER);
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                currentLocation.set(location);
                                try {
                                    List<Address> list = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                                    if(list.get(0).toString().length() > 2){
                                        currentAddress = list.get(0).getAddressLine(0);
                                        currentCity = list.get(0).getLocality();
                                        Log.i("CITY",currentCity);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.i("Curr Loc",currentLocation.toString());
                                Log.i(" Curr Add",currentAddress);
                            }
                        }
                    });
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation.set(location);
                try {
                    List<Address> list = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                    if(list.get(0).toString().length() > 2){
                        if(list.get(0).getFeatureName()!=null && !list.get(0).getAddressLine(0).contains(list.get(0).getFeatureName())) {
                            currentAddress = list.get(0).getFeatureName() +", " + list.get(0).getAddressLine(0);
                            currentCity = list.get(0).getLocality();
                            //Log.i("CITY",currentCity);
                            Log.i("Loc", currentLocation.toString());
                        }else{
                            currentAddress = list.get(0).getAddressLine(0);
                            currentCity = list.get(0).getLocality();
//                            Log.i("CITY",currentCity);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Log.i("Curr Loc changed",currentLocation.toString());
//                Log.i(" Curr Add changed",currentAddress);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    public LatLng getCurrentLocationLatLng(){
        return new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
    }

    public String getAddressFromLatLng(LatLng latLng){
        String add = "";
        try {
            List<Address> list = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(list.get(0).toString().length() > 2){
                if(list.get(0).getFeatureName()!=null && !list.get(0).getAddressLine(0).contains(list.get(0).getFeatureName())) {
                    add = list.get(0).getFeatureName() +", " + list.get(0).getAddressLine(0);
                    Log.i("Add from latlng if1",add);
                    return add;
                }else{
                    add = list.get(0).getAddressLine(0);
                    return add;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return add;
    }
    public String getAddressFromLocation(Location location){
        String add = "";
        try {
            List<Address> list = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
            if(list.get(0).toString().length() > 2){
                if(list.get(0).getFeatureName()!=null && !list.get(0).getAddressLine(0).contains(list.get(0).getFeatureName())) {
                    add = list.get(0).getFeatureName() +", " + list.get(0).getAddressLine(0);
                    return add;
                }else{
                    add = list.get(0).getAddressLine(0);
                    return add;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return add;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public Geocoder getGeocoder() {
        return geocoder;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public String getCurrentCity(){
        //Log.i("CITY",currentCity);
        return currentCity;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    public double getDistanceTo(String latitude, String longitude){
        Location toWhere = new Location(LocationManager.GPS_PROVIDER);
        toWhere.setLatitude(Double.parseDouble(latitude));
        toWhere.setLongitude(Double.parseDouble(longitude));
        if(currentLocation.getLatitude()!=0) {
            return (currentLocation.distanceTo(toWhere)/1000);
        }
        return INF ;
    }

    public double getDistanceTo(double latitude, double longitude){
        try {
            Location toWhere = new Location("gps");
            toWhere.setLatitude(latitude);
            toWhere.setLongitude(longitude);
            if (currentLocation.getLatitude() != 0 && latitude > 0 && longitude > 0) {
                return (currentLocation.distanceTo(toWhere) / 1000);
            }else {
                return INF;
            }
        }catch(Exception e){
            e.printStackTrace();
            return INF;
        }
    }

    public double getDistanceTo(Location toWhere){
        try{
            return currentLocation.distanceTo(toWhere);
        }catch(Exception e){
            e.printStackTrace();
            return INF;
        }
    }

    public double getDistanceFromAtoB(String latitudeA, String longitudeA, String latitudeB, String longitudeB ){
        try {
            Location locationA = new Location(LocationManager.GPS_PROVIDER);
            locationA.setLatitude(Double.parseDouble(latitudeA));
            locationA.setLongitude(Double.parseDouble(longitudeA));
            Location locationB = new Location(LocationManager.GPS_PROVIDER);
            locationB.setLatitude(Double.parseDouble(latitudeB));
            locationB.setLongitude(Double.parseDouble(longitudeB));
            return locationA.distanceTo(locationB)/1000;
        }catch (Exception e){
            return INF;
        }
    }

    public double getDistanceFromAtoB(double latitudeA, double longitudeA, double latitudeB, double longitudeB ){
        try {
            Location locationA = new Location(LocationManager.GPS_PROVIDER);
            locationA.setLatitude(latitudeA);
            locationA.setLongitude(longitudeA);
            Location locationB = new Location(LocationManager.GPS_PROVIDER);
            locationB.setLatitude(latitudeB);
            locationB.setLongitude(longitudeB);
            return locationA.distanceTo(locationB)/1000;
        }catch (Exception e){
            return INF;
        }
    }

    public double getDistanceFromAtoB(Location A, Location B){
        try{
            return A.distanceTo(B);
        }catch (Exception e){
            e.printStackTrace();
            return INF;
        }
    }

    public void setGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


}
