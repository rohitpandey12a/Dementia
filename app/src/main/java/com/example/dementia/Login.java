package com.example.dementia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity implements View.OnClickListener,
		OnSuccessListener<DocumentSnapshot> {
	
	private EditText EmailEditText,PasswordEditText;
	private Button LoginInButton;
	private ProgressDialog progressDialog;
	
	private FirebaseAuth fireBaseAuth;
	private DocumentReference dref;
	private final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
	private String UserId,userType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		fireBaseAuth = FirebaseAuth.getInstance();
		if (fireBaseAuth.getCurrentUser()!=null){
			Log.d("Login","User Logged In");
			UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
			dref = fstore.collection("User").document("UserId")
					.collection(UserId).document("Details");
			dref.get().addOnSuccessListener(this);
			if (userType=="Care Taker"){
				finish();
				startActivity(new Intent(getApplicationContext(), CareTakerHome.class));
			} else if (userType == "Patient") {
				finish();
				startActivity(new Intent(getApplicationContext(), PatientHome.class));
			}
		}else {
			Log.d("Login","User Not Logged In");
			EmailEditText = findViewById(R.id.editTextTextEmailAddressLogin);
			PasswordEditText = findViewById(R.id.editTextTextPasswordLogin);
			LoginInButton = findViewById(R.id.buttonLoginSign);
			progressDialog = new ProgressDialog(this);
			LoginInButton.setOnClickListener(this);
		}
		
	}
	
	@Override
	public void onClick(View view) {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			if (view==LoginInButton){
				userLogin();
			}
		}
		else{
			Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
		}
	}
	
	private void userLogin() {
		String Email=EmailEditText.getText().toString().trim();
		String Password=PasswordEditText.getText().toString().trim();
		if(TextUtils.isEmpty(Email)){
			Toast.makeText(this,"Please Enter email",Toast.LENGTH_SHORT).show();
			return;
		}
		if(TextUtils.isEmpty(Password)){
			Toast.makeText(this,"Please Enter password",Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog.setMessage("Signing In");
		progressDialog.show();
		fireBaseAuth.signInWithEmailAndPassword(Email,Password)
				.addOnCompleteListener(this, task -> {
					
					if(task.isSuccessful()){
						logUserIn();
					}
					else {
						Toast.makeText(Login.this,"SignIn Error",Toast.LENGTH_SHORT).show();
						
					}
					progressDialog.dismiss();
				});
	}
	
	private void logUserIn() {
		UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
		dref = fstore.collection("User").document("UserId")
				.collection(UserId).document("Details");
		dref.get().addOnSuccessListener(this);
	}
	
	@Override
	public void onSuccess(DocumentSnapshot documentSnapshot) {
		if (documentSnapshot.exists()){
			userType=documentSnapshot.getString("User Type");
			if (Objects.equals(userType, "Care Taker")){
				finish();
				startActivity(new Intent(getApplicationContext(), CareTakerHome.class));
			} else if (Objects.equals(userType, "Patient")) {
				finish();
				startActivity(new Intent(getApplicationContext(), PatientHome.class));
			}
		}
	}
}