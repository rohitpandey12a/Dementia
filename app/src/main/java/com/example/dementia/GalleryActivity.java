package com.example.dementia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class GalleryActivity extends AppCompatActivity {
	
	private FirebaseAuth fireBaseAuth;
	ArrayList<String> imagelist;
	RecyclerView recyclerView;
	StorageReference root;
	ProgressBar progressBar;
	GalleryAdapter adapter;
	String UserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		fireBaseAuth = FirebaseAuth.getInstance();
		if (fireBaseAuth.getCurrentUser()==null){
			finish();
			startActivity(new Intent(getApplicationContext(),Login.class));
		}
		
		UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
		
		imagelist=new ArrayList<>();
		recyclerView=findViewById(R.id.recyclerview);
		adapter=new GalleryAdapter(imagelist,this);
		recyclerView.setLayoutManager(new LinearLayoutManager(null));
		progressBar=findViewById(R.id.progress);
		progressBar.setVisibility(View.VISIBLE);
		StorageReference listRef = FirebaseStorage.getInstance().getReference().child(
				"images");
		listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
			@Override
			public void onSuccess(ListResult listResult) {
				for(StorageReference file:
						listResult.getItems()){
					file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							imagelist.add(uri.toString());
							Log.e("Itemvalue",uri.toString());
						}
					}).addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							recyclerView.setAdapter(adapter);
							progressBar.setVisibility(View.GONE);
						}
					});
				}
			}
		});
	}
}