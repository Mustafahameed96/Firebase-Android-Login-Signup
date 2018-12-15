package com.example.mustafa.assignment3mse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    EditText editTextFirstName,editTextLastName;
    EditText editTextEmail,editTextPassword,editTextConfirmPassword;
    EditText editTextCnic,editTextContactNumber;
    ProgressBar progressBar;
    ImageView imageViewProfile;
    String profileImageUrl;
    EditText editText;
    Uri uriProfileImage;
    String testString;
    StorageReference profileImageRef;
    String ProfileUrl;



    private  static final int CHOOSE_IMAGE = 101;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextCnic = findViewById(R.id.editTextCnic);
        editTextContactNumber = findViewById(R.id.editTextContactNumber);
        editTextEmail= findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        progressBar = findViewById(R.id.progressbar);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){

            //handle the already login user

        }


    }

    private void registerUser(){




        final String FirstName = editTextFirstName.getText().toString().trim();
        final String LastName = editTextLastName.getText().toString().trim();
        final String CNIC = editTextCnic.getText().toString().trim();
        final String ContactNumber = editTextContactNumber.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String ConfirmPassword = editTextConfirmPassword.getText().toString().trim();




        if(FirstName.isEmpty()){

            editTextFirstName.setError("First Name is required");

            editTextFirstName.requestFocus();

            return;

        }

        if(LastName.isEmpty()){

            editTextLastName.setError("Last Name is required");

            editTextLastName.requestFocus();

            return;

        }
        if(CNIC.isEmpty()){

            editTextCnic.setError("CNIC is required");

            editTextCnic.requestFocus();

            return;

        }
        if(ContactNumber.isEmpty()){

            editTextContactNumber.setError("Contact Number is required");

            editTextContactNumber.requestFocus();

            return;

        }
        if(!Patterns.PHONE.matcher(ContactNumber).matches()){

            editTextContactNumber.setError("Please Enter Valid Contact Number");
            editTextContactNumber.requestFocus();
            return;

        }


        if(email.isEmpty()){

            editTextEmail.setError("Email is required");

            editTextEmail.requestFocus();

                return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            editTextEmail.setError("Please Enter Valid Email");
            editTextEmail.requestFocus();
            return;

        }
        if(password.isEmpty()){

            editTextPassword.setError("Password is required");

            editTextPassword.requestFocus();
            return;

        }
        if(password.length()<6){

            editTextPassword.setError("Minimum length of Password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        if(ConfirmPassword.isEmpty()){

            editTextConfirmPassword.setError("Confirm Password is required");

            editTextConfirmPassword.requestFocus();

            return;

        }

        if(!ConfirmPassword.equals(password)){

            editTextPassword.setError("Both Passwords must match");

            editTextPassword.requestFocus();

            editTextConfirmPassword.setError("Both Passwords must match");

            editTextConfirmPassword.requestFocus();

            return;

        }



        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override

            public void onComplete(@NonNull final Task<AuthResult> task) {



        uploadImageToFirebase();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {






                if (task.isSuccessful()) {




                    User user = new User(
                            FirstName,
                            LastName,
                            email,
                            ContactNumber,
                            CNIC,
                            ProfileUrl

                    );


                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){

                                        //  saveUserInformation();

                                        Toast.makeText(getApplicationContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                        finish();
                                        startActivity(new Intent(SignUpActivity.this,ProfileActivity.class));


                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();


                                    }
                                }
                            })
                    ;






                }

                else {

                    if(task.getException() instanceof FirebaseAuthUserCollisionException){

                        Toast.makeText(getApplicationContext(),"You're already registered",Toast.LENGTH_SHORT).show();


                    }
                    else {

                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }








            }
        },10000);



                }

        });
    }

    public void onClick(View view){
        switch (view.getId()){

            case R.id.buttonSignUp:

                registerUser();

            break;

            case R.id.textViewLogin:
                finish();

                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }

//    private void saveUserInformation() {
//
//
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if(user!=null && profileImageUrl!=null){
//
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setPhotoUri(Uri.parse(profileImageUrl))
//                    .build();
//
//            user.updateProfile(profile)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//
//                                Toast.makeText(SignUpActivity.this,"Profile Updated",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//
//
//    }







    private void showImageChooser(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            uriProfileImage = data.getData();
            imageViewProfile.setImageURI(uriProfileImage);



//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
//                imageViewProfile.setImageBitmap(bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
      }
 }

    private void uploadImageToFirebase() {


        profileImageRef= FirebaseStorage.getInstance().getReference().child("profilepics/"+System.currentTimeMillis() + ".jpg").child(uriProfileImage.getLastPathSegment());

        if(uriProfileImage!=null){

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImageUrl=uri.toString();
                            ProfileUrl=profileImageUrl;
                            Toast.makeText(SignUpActivity.this," MAA KA BHAROSA" + profileImageUrl,Toast.LENGTH_SHORT).show();



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this,"MAA KI CHOOD",Toast.LENGTH_SHORT).show();


                        }
                    });




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


                }
            });
        }
    }



}

