package io.shadowwings.smartfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity{

    ImageView BTN_BACK, IMG_PLACEHOLDER;
    Uri FILE_PATH; CardView BTN_UPLOAD;
    EditText ET_TITLE; String Title = "";
    DatabaseReference ROOT; String POST_TYPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FILE_PATH = Uri.parse(extras.getString("PATH"));
            POST_TYPE = extras.getString("POST_TYPE");
        }else {
            Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        IMG_PLACEHOLDER = findViewById(R.id.IMG_PLACEHOLDER);
        BTN_BACK = findViewById(R.id.BTN_BACK);
        BTN_UPLOAD = findViewById(R.id.BTN_UPLOAD);
        ET_TITLE = findViewById(R.id.ET_TITLE);
        ROOT = FirebaseDatabase.getInstance("https://smart-farm-c9ae4-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        if (POST_TYPE.equals("VIDEO")){
            MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
            mMMR.setDataSource(getApplicationContext(), FILE_PATH);
            Bitmap bmp = mMMR.getFrameAtTime();
            IMG_PLACEHOLDER.setImageBitmap(bmp);
        }

        if (POST_TYPE.equals("IMAGE")){
            IMG_PLACEHOLDER.setImageURI(FILE_PATH);
        }

        BTN_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        BTN_UPLOAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title = ET_TITLE.getText().toString();
                if (Title.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                UPLOAD_VIDEO();
            }
        });
    }

    private void UPLOAD_VIDEO() {
        if (FILE_PATH != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Post");
            progressDialog.show();

            SharedPreferences preferences = getSharedPreferences("SMART_FARM", Context.MODE_PRIVATE);
            String USERNAME = preferences.getString("USERNAME", "");
            long MILLI_SEC = System.currentTimeMillis();

            StorageReference riversRef = FirebaseStorage.getInstance("gs://smart-farm-c9ae4.appspot.com").getReference().child("POSTS/" + USERNAME + "/" + MILLI_SEC);
            riversRef.putFile(FILE_PATH).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ROOT.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, String> MAP = new HashMap<>();
                                    MAP.put("POST_TYPE", POST_TYPE);
                                    MAP.put("URL", uri + "");
                                    MAP.put("TITLE", Title);
                                    MAP.put("TIME", MILLI_SEC + "");
                                    MAP.put("USERNAME",USERNAME);

                                    ROOT.child("POSTS").child(MILLI_SEC + "").setValue(MAP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, String> MAP_1 = new HashMap<>();
                                            MAP.put("POST_TYPE", POST_TYPE);
                                            MAP.put("URL", uri + "");
                                            MAP.put("TITLE", Title);
                                            MAP.put("TIME", MILLI_SEC + "");

                                            ROOT.child("USERS_POSTS").child(USERNAME).child(MILLI_SEC + "").setValue(MAP_1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Post Uploaded Successfully", Toast.LENGTH_LONG).show();
                                                    onBackPressed();
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Unknown error occurred", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Unknown error occurred", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}