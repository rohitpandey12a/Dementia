package com.example.dementia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class UploadVideo extends AppCompatActivity {
	
	private Button btnChoose, btnUpload;
	ProgressDialog progressDialog;
	private String UserId;
	private FirebaseAuth fireBaseAuth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_video);
		
		// initialise layout
		btnChoose = (Button) findViewById(R.id.btnChooseV);
		btnChoose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Code for showing progressDialog while uploading
				progressDialog = new ProgressDialog(UploadVideo.this);
				choosevideo();
			}
		});
	}
	
	// choose a video from phone storage
	private void choosevideo() {
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 5);
	}
	
	Uri videouri;
	
	// startActivityForResult is used to receive the result, which is the selected video.
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
			videouri = data.getData();
			progressDialog.setTitle("Uploading...");
			progressDialog.show();
			uploadvideo();
		}
	}
	
	private String getfiletype(Uri videouri) {
		ContentResolver r = getContentResolver();
		// get the file type ,in this case its mp4
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
	}
	
	private void uploadvideo() {
		if (videouri != null) {
			// save the selected video in Firebase storage
			final StorageReference reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(videouri));
			reference.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
					while (!uriTask.isSuccessful()) ;
					// get the link of video
					String downloadUri = uriTask.getResult().toString();
					UserId = Objects.requireNonNull(fireBaseAuth.getCurrentUser()).getUid();
					
					DatabaseReference reference1 =
							FirebaseDatabase.getInstance().getReference(UserId+
							"/Video");
					HashMap<String, String> map = new HashMap<>();
					map.put("videolink", downloadUri);
					reference1.child("" + System.currentTimeMillis()).setValue(map);
					// Video uploaded successfully
					// Dismiss dialog
					progressDialog.dismiss();
					Toast.makeText(UploadVideo.this, "Video Uploaded!!", Toast.LENGTH_SHORT).show();
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					// Error, Image not uploaded
					progressDialog.dismiss();
					Toast.makeText(UploadVideo.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
				// Progress Listener for loading
				// percentage on the dialog box
				@Override
				public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
					// show the progress bar
					double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
					progressDialog.setMessage("Uploaded " + (int) progress + "%");
				}
			});
		}
	}
}