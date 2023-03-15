package com.example.dementia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegisterPatient extends AppCompatActivity implements View.OnClickListener,
		OnSuccessListener<DocumentSnapshot> {
	
	private EditText editTextpatientEmail;
	private Button buttonRegisterPatient;
	private TextView textViewStatus;
	private FirebaseAuth fireBaseAuth;
	private final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
	private DocumentReference dref,dref2;
	
	private String Email,userType,Password,UserId;
	private String PatientUserID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_patient);
		
		fireBaseAuth = FirebaseAuth.getInstance();
		editTextpatientEmail=findViewById(R.id.editTextEnterPatientEmail);
		textViewStatus=findViewById(R.id.textViewStatus);
		buttonRegisterPatient=findViewById(R.id.buttonRegisterPatientToCareTaker);
		buttonRegisterPatient.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		
		if (view==buttonRegisterPatient){
			String Email=editTextpatientEmail.getText().toString().trim();
			if(TextUtils.isEmpty(Email)){
				Toast.makeText(this,"Please Enter email",Toast.LENGTH_SHORT).show();
			}else {
				UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
				dref = fstore.collection("Users").document(Email);
				dref.get().addOnSuccessListener(this);
				
			}
			
		}
		
	}
	
	@Override
	public void onSuccess(DocumentSnapshot documentSnapshot) {
		if (documentSnapshot.exists()){
			PatientUserID = documentSnapshot.getString("UserID");
			updateCaretakerPatient();
		}
	}
	
	private void updateCaretakerPatient() {
		dref = fstore.collection("User").document("UserId")
				.collection(UserId).document("Details");
		Map<String,Object> note = new HashMap<>();
		note.put("Patient User ID",PatientUserID);
		dref.update(note);
	}
}