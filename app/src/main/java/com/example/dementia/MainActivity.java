package com.example.dementia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private Button Register,Login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Register=findViewById(R.id.buttonRegister);
			Login=findViewById(R.id.buttonLoginMain);
			Register.setOnClickListener(this);
			Login.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		if (view==Login){
			startActivity(new Intent(this,Login.class));
		}else if (view==Register){
			startActivity(new Intent(this,Register.class));
		}
	}
}