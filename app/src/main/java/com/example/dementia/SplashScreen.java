package com.example.dementia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {
	private Button Next;
	private String UserId,userType;
	private FirebaseAuth fireBaseAuth = FirebaseAuth.getInstance();
	private DocumentReference dref;
	private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		Next = findViewById(R.id.buttonNext);
		Next.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
			startActivity(new Intent(this,MainActivity.class));
	}
	
	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {
		super.onPointerCaptureChanged(hasCapture);
	}
}