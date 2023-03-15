package com.example.dementia;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.Manifest;
import android.os.Looper;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.collection.CircularArray;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {
	
	private FusedLocationProviderClient fusedLocationClient;
	private DatabaseReference mDatabase;
	private FirebaseAuth mAuth;
	private LocationCallback locationCallback;
	private static final int LOCATION_UPDATE_INTERVAL = 5000; // update location every 5 seconds
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
		
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		
		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				if (locationResult == null) {
					return;
				}
				for (Location location : locationResult.getLocations()) {
					// Upload location to Firebase Realtime Database
					if (location != null) {
						double latitude = location.getLatitude();
						double longitude = location.getLongitude();
						mDatabase.child("latitude").setValue(latitude);
						mDatabase.child("longitude").setValue(longitude);
					}
				}
			}
		};
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Request location permission
		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission is not granted, request it
			ActivityCompat.requestPermissions((Activity) getApplicationContext(),
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		} else {
			startLocationUpdates();
		}
		return START_STICKY;
	}
	
	private void startLocationUpdates() {
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
		locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		fusedLocationClient.removeLocationUpdates(locationCallback);
	}
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
