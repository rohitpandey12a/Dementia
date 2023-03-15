package com.example.dementia;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.dementia.databinding.ActivityMapsBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
		GoogleMap.OnMyLocationClickListener,
		OnMapReadyCallback,
		ActivityCompat.OnRequestPermissionsResultCallback, PlaceSelectionListener {
	
	private GoogleMap map;
	private LatLng searchResult;
	private ActivityMapsBinding binding;
	
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
	
	private boolean permissionDenied = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		binding = ActivityMapsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		View locationButton =
				((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
		RelativeLayout.LayoutParams rlp =
				(RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
		rlp.setMargins(0,0,30,300);
		String apikey = getString(R.string.api_key);
		if(!Places.isInitialized()){
			Places.initialize(getApplicationContext(),apikey);
		}
		
		PlacesClient placesClient = Places.createClient(this);
		AutocompleteSupportFragment autocompleteFragment =
				(AutocompleteSupportFragment) getSupportFragmentManager()
				.findFragmentById(R.id.autocomplete_fragment);
		
		autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
				Place.Field.LAT_LNG));
		autocompleteFragment.setOnPlaceSelectedListener(this);
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		map.setOnMyLocationButtonClickListener(this);
		map.setOnMyLocationClickListener(this);
		enableMyLocation();
	}
	@SuppressLint("MissingPermission")
	private void enableMyLocation() {
		// 1. Check if permissions are granted, if so, enable the my location layer
		if (ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED
				|| ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			map.setMyLocationEnabled(true);
			return;
		}
		
		// 2. Otherwise, request location permissions from the user.
		PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
	}
	
	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}
	
	@Override
	public void onMyLocationClick(@NonNull Location location) {
		Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			return;
		}
		
		if (PermissionUtils.isPermissionGranted(permissions, grantResults,
				android.Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
				.isPermissionGranted(permissions, grantResults,
						android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
			// Enable the my location layer if the permission has been granted.
			enableMyLocation();
		} else {
			// Permission was denied. Display an error message
			// Display the missing permission error dialog when the fragments resume.
			permissionDenied = true;
		}
	}
	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (permissionDenied) {
			// Permission was not granted, display error dialog.
			showMissingPermissionError();
			permissionDenied = false;
		}
	}
	private void showMissingPermissionError() {
		PermissionUtils.PermissionDeniedDialog
				.newInstance(true).show(getSupportFragmentManager(), "dialog");
	}
	
	@Override
	public void onError(@NonNull Status status) {
		Toast.makeText(this, "Place:\n" + status, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onPlaceSelected(@NonNull Place place) {
		Toast.makeText(this, "Place:\n" + place.getName() + ", " + place.getId(), Toast.LENGTH_LONG).show();
		if (place.getLatLng() != null) {
			map.clear();
			map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),13.3f));
			map.addMarker(new MarkerOptions().position(place.getLatLng()));
		}
	}
}