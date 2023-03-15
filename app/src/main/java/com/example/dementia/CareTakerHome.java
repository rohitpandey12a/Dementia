package com.example.dementia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CareTakerHome extends AppCompatActivity implements View.OnClickListener {
	
	private Button buttonTrackLocation,buttonReminder,buttonLogOut,buttonRegisterPatient;
	private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_care_taker_home);
		
		if (user==null){
			finish();
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
		}
		
		buttonTrackLocation=findViewById(R.id.buttonTrackLocation);
		buttonReminder=findViewById(R.id.buttonReminder);
		buttonLogOut=findViewById(R.id.buttonLogOut);
		buttonRegisterPatient=findViewById(R.id.buttonRegisterPatient);
		buttonTrackLocation.setOnClickListener(this);
		buttonReminder.setOnClickListener(this);
		buttonLogOut.setOnClickListener(this);
		buttonRegisterPatient.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View view) {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			if (view == buttonTrackLocation) {
				startActivity(new Intent(this, TrackPatientMapsActivity.class));
			} else if (view == buttonReminder) {
				startActivity(new Intent(this, ReminderActivity.class));
			} else if (view == buttonRegisterPatient) {
				startActivity(new Intent(this, RegisterPatient.class));
			} else if (view == buttonLogOut) {
				FirebaseAuth.getInstance().signOut();
				startActivity(new Intent(this, MainActivity.class));
			}
		}else{
			Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
		}
		
	}
}