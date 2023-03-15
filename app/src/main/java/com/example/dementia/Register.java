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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener,
		AdapterView.OnItemSelectedListener, OnSuccessListener<DocumentSnapshot> {
	
	private EditText EmailRegisterEditText,PasswordRegisterEditText;
	private Button buttonRegister;
	private final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
	private DocumentReference dref,dref2;
	private ProgressDialog progressDialog;
	private String Email,userType,Password,UserId;
	private FirebaseAuth fireBaseAuth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		fireBaseAuth = FirebaseAuth.getInstance();
		if (fireBaseAuth.getCurrentUser()!=null){
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
		}else{
			EmailRegisterEditText=findViewById(R.id.editTextTextEmailAddress);
			PasswordRegisterEditText=findViewById(R.id.editTextTextPassword);
			buttonRegister =findViewById(R.id.buttonRegister);
			buttonRegister.setOnClickListener(this);
			progressDialog = new ProgressDialog(this);
			Spinner spinnerUserType=findViewById(R.id.spinnerUserType);
			spinnerUserType.setOnItemSelectedListener(this);
			ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,
					R.array.userType, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
			spinnerUserType.setAdapter(adapter);
		}
	}
	
	@Override
	public void onClick(View view) {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			if(view == buttonRegister) {
				registerUser();
			}
		}else{
			Toast.makeText(Register.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
		}
	
	}
	
	private void registerUser() {
		Password = PasswordRegisterEditText.getText().toString().trim();
		Email = EmailRegisterEditText.getText().toString().trim();
		if(TextUtils.isEmpty(Email)){
			Toast.makeText(this,"Please Enter email",Toast.LENGTH_SHORT).show();
			return;
		}else if(TextUtils.isEmpty(Password)){
			Toast.makeText(this,"Please Enter password",Toast.LENGTH_SHORT).show();
			return;
		}
		
		progressDialog.setMessage("Registering User");
		progressDialog.show();
		fireBaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, task -> {
			if(task.isSuccessful()){
				finish();
				UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
				dref = fstore.collection("User").document("UserId")
						.collection(UserId).document("Details");
				Map<String,Object> note = new HashMap<>();
				note.put("Email",Email);
				note.put("Password", Password);
				note.put("User Type",userType);
				dref.set(note).addOnSuccessListener(aVoid -> {
					
					if (Objects.equals(userType, "Care Taker")){
						finish();
						startActivity(new Intent(getApplicationContext(), CareTakerHome.class));
					} else if (Objects.equals(userType, "Patient")) {
						finish();
						startActivity(new Intent(getApplicationContext(), PatientHome.class));
					}
					
				});
				
			}else{
				Toast.makeText(Register.this,"Could not Register",Toast.LENGTH_SHORT).show();
			}
			progressDialog.dismiss();
		});
	}
	
	
	
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
		buttonRegister.setEnabled(true);
		userType=adapterView.getItemAtPosition(i).toString();
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		buttonRegister.setEnabled(false);
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
	
	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {
		super.onPointerCaptureChanged(hasCapture);
	}
}