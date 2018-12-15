package com.example.mustafa.assignment3mse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;

public class ProfileActivity extends AppCompatActivity {


    private  static final int CHOOSE_IMAGE = 101;
    ImageView imageView;
    EditText editText;
    ProgressBar progressBar;
    String profileImageUrl;
    FirebaseAuth mAuth;



    Uri uriprofileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editText = (EditText)findViewById(R.id.editTextDisplayName);
        imageView = (ImageView)findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressbar);
        mAuth =FirebaseAuth.getInstance();

        loadUserInformation();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();

            }
        });

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();

            }
        });
    }

    private void loadUserInformation() {

        FirebaseUser user = mAuth.getCurrentUser();

        if(user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl().toString())
                    .into(imageView);
        }
        if(user.getDisplayName()!=null) {
    editText.setText((user.getDisplayName()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){

            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    private void saveUserInformation() {
        String displayName=editText.getText().toString();

        if(displayName.isEmpty()){
            editText.setError("Name Required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null && profileImageUrl!=null){

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(ProfileActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            uriprofileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriprofileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase() {
        StorageReference profileImageRef= FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis() + ".jpg");

        if(uriprofileImage!=null){
            progressBar.setVisibility(View.VISIBLE);

            profileImageRef.putFile(uriprofileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);


                    profileImageUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


                }
            });
        }
    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
}


