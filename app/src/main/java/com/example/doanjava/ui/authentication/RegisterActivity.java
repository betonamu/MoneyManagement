package com.example.doanjava.ui.authentication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.doanjava.MainActivity;
import com.example.doanjava.R;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    EditText mEmail, mPassword, mFullName, mPhoneNumber, mRePassword;
    Button mBtnRegister;
    ImageView mImageRegister;
    TextView signInTextLink;
    ProgressBar progressBar;
    int REQUEST_CODE = 1;
    static int PReqCode = 1;
    Uri pickedImgUri;

    //Firebase database
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;


    private String username, phoneNumber, password, fullName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Anh xa view
        mEmail = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRePassword = (EditText) findViewById(R.id.re_password);
        mBtnRegister = (Button) findViewById(R.id.register);
        mImageRegister = (ImageView) findViewById(R.id.img_register);
        mFullName = (EditText) findViewById(R.id.full_name);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number);
        signInTextLink = (TextView) findViewById(R.id.redirect_login);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        mBtnRegister.setEnabled(true);

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        signInTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        mImageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            mImageRegister.setImageURI(pickedImgUri);
        }
    }

    public void Register() {
        username = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        phoneNumber = mPhoneNumber.getText().toString().trim();
        fullName = mFullName.getText().toString().trim();
//
//        if (TextUtils.isEmpty(fullName)) {
//            mEmail.setError(getResources().getString(R.string.required_name));
//            return;
//        }
//        if (TextUtils.isEmpty(username)) {
//            mEmail.setError(getResources().getString(R.string.required_username));
//            return;
//        } else if (!isValidEmail(username)) {
//            mEmail.setError(getResources().getString(R.string.invalid_username));
//            return;
//        }
//        if (TextUtils.isEmpty(phoneNumber)) {
//            mEmail.setError(getResources().getString(R.string.required_phone));
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            mPassword.setError(getResources().getString(R.string.required_password));
//            return;
//        }
//        if (password.length() < 6) {
//            mPassword.setError(getResources().getString(R.string.invalid_password));
//            return;
//        }

        progressBar.setVisibility(View.VISIBLE);

        uploadPhoto(new ICallBackFireStore() {
            @Override
            public void onCallBack(List lstObject, Object value) {
                firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            //Add custom field to Firebase FireStore
                            UserModel userModel = new UserModel();
                            userModel.email = username;
                            userModel.fullName = fullName;
                            userModel.password = password;
                            userModel.phoneNumber = phoneNumber;
                            userModel.photoUrl = value.toString();

                            //Set the value for the node with uId = uId of the current user to firebase
                            db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(userModel);

                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Created with error " + task.getException(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        //Authentication user

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void uploadPhoto(ICallBackFireStore callBack) {
        if (pickedImgUri != null) {
            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storage.getReferenceFromUrl("gs://doanjava-843c8.appspot.com").child("image" + calendar.getTimeInMillis());
            mountainsRef.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           Uri downloadUri = uri;
                            callBack.onCallBack(null,downloadUri);
                            Log.d("A",downloadUri.toString());
                        }
                    });
                }
            });
        }
    }
}

