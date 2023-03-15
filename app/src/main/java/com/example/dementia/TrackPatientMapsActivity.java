package com.example.dementia;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.dementia.databinding.ActivityTrackPatientMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class TrackPatientMapsActivity extends FragmentActivity implements OnMapReadyCallback,
		OnSuccessListener<DocumentSnapshot>, OnFailureListener {
	
	private GoogleMap mMap;
	private ActivityTrackPatientMapsBinding binding;
	
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
	
	private FirebaseAuth fireBaseAuth;
	private DocumentReference dref;
	private final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
	private String UserId,PatientUserID;
	private double lat;
	private double lon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		binding = ActivityTrackPatientMapsBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapTrackPaitentMap);
		mapFragment.getMapAsync(this);
		
		fireBaseAuth=FirebaseAuth.getInstance();
		UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
		dref = fstore.collection("User").document("UserId")
				.collection(UserId).document("Details");
		dref.get().addOnSuccessListener(this).addOnFailureListener(this);
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
		
		enableMyLocation();
		// Add a marker in Sydney and move the camera
		Log.d("Latitude", String.valueOf(lat));
		Log.d("Longitude", String.valueOf(lon));
	}
	
	private void enableMyLocation() {
		// 1. Check if permissions are granted, if so, enable the my location layer
		if (ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED
				|| ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			mMap.setMyLocationEnabled(true);
			return;
		}
		
		// 2. Otherwise, request location permissions from the user.
		PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
	}
	
	@Override
	public void onSuccess(DocumentSnapshot documentSnapshot) {
		if (documentSnapshot.exists()){
			PatientUserID = documentSnapshot.getString("Patient User ID");
			Log.d("TrackMapActivity",PatientUserID);
			DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(PatientUserID);
			databaseReference.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					if (snapshot.exists()){
						lat = (double) snapshot.child("latitude").getValue();
						lon = (double) snapshot.child("longitude").getValue();
						Log.d("firebase", String.valueOf(lat));
						Log.d("firebase", String.valueOf(lon));
						
						Log.d("firebase", String.valueOf(snapshot.getValue()));
						LatLng patientLocation = new LatLng(lat,lon);
						mMap.addMarker(new MarkerOptions().position(patientLocation).title("Patient"));
						mMap.moveCamera(CameraUpdateFactory.newLatLng(patientLocation));
					}
				}
				
				@Override
				public void onCancelled(@NonNull DatabaseError error) {
				
				}
		});
	}
	}
	
	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {
		super.onPointerCaptureChanged(hasCapture);
	}
	
	@Override
	public void onFailure(@NonNull Exception e) {
	
	}
}