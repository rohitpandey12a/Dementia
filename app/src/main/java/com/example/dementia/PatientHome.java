package com.example.dementia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientHome extends AppCompatActivity implements View.OnClickListener {
	
	private Button buttonLocation,buttonReminder,buttonUploadImage,buttonUploadVideo,buttonGallery,buttonLogOut;
	private FirebaseUser user;
	private LocationService locationService;
	
	private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
	
	private DocumentReference dref2;
	private final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_home);
		user = FirebaseAuth.getInstance().getCurrentUser();
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// Permission is not granted, request it from the user
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
		} else {
			// Permission is already granted, start the LocationService
			Intent intent = new Intent(this, LocationService.class);
			startService(intent);
			Log.d("Patient Home", "No Location");
		}
		
		buttonLocation=findViewById(R.id.buttonLocation);
		buttonReminder=findViewById(R.id.buttonReminder);
		buttonUploadImage=findViewById(R.id.buttonUploadImage);
		buttonUploadVideo=findViewById(R.id.buttonUploadVideo);
		buttonGallery=findViewById(R.id.buttonGallery);
		buttonLogOut=findViewById(R.id.buttonLogOut);
		buttonLocation.setOnClickListener(this);
		buttonReminder.setOnClickListener(this);
		buttonUploadImage.setOnClickListener(this);
		buttonUploadVideo.setOnClickListener(this);
		buttonGallery.setOnClickListener(this);
		buttonLogOut.setOnClickListener(this);
		String Email=user.getEmail();
		String UserId=user.getUid();
		dref2 = fstore.collection("Users").document(Email);
		Map<String,Object> note1 = new HashMap<>();
		note1.put("UserID",UserId);
		dref2.set(note1);
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission is granted, start the LocationService
				Intent intent = new Intent(this, LocationService.class);
				startService(intent);
			} else {
				// Permission is denied, show a message to the user
				Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	@Override
	public void onClick(View view) {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			if (view == buttonLocation) {
				startActivity(new Intent(this, MapsActivity.class));
			} else if (view == buttonReminder) {
				startActivity(new Intent(this, ReminderActivity.class));
			} else if (view == buttonUploadImage) {
				startActivity(new Intent(this, UploadImage.class));
			} else if (view == buttonUploadVideo) {
				startActivity(new Intent(this, UploadVideo.class));
			} else if (view == buttonGallery) {
				startActivity(new Intent(this, GalleryActivity.class));
			}else if (view == buttonLogOut) {
				FirebaseAuth.getInstance().signOut();
				startActivity(new Intent(this, MainActivity.class));
			}
		}else{
			Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		
	}
}