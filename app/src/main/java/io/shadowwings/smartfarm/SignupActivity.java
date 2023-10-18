package io.shadowwings.smartfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    RelativeLayout BTN_LOGIN, BTN_HELP;
    CardView BTN_SIGNUP; DatabaseReference ROOT;
    EditText ET_EMAIL, ET_NAME, ET_PHONE, ET_PASSWORD;
    String sEmail = "", sName = "", sPhone = "", sPassword = "";
    ProgressBar PROGRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.GLARE_WHITE));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        init();

    }

    public void init(){
        BTN_HELP = findViewById(R.id.BTN_HELP);
        BTN_SIGNUP = findViewById(R.id.BTN_SIGNUP);
        BTN_LOGIN = findViewById(R.id.BTN_LOGIN);
        ET_EMAIL = findViewById(R.id.ET_EMAIL);
        ET_NAME = findViewById(R.id.ET_NAME);
        ET_PHONE = findViewById(R.id.ET_PHONE);
        ET_PASSWORD = findViewById(R.id.ET_PASSWORD);
        PROGRESS = findViewById(R.id.PROGRESS);
        PROGRESS.setVisibility(View.GONE);
        ROOT = FirebaseDatabase.getInstance("https://smart-farm-c9ae4-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        onClick();
    }

    public void onClick(){
        BTN_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        BTN_SIGNUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CHECK_INPUTS();
            }
        });
    }

    public void CHECK_INPUTS(){
        sEmail = ET_EMAIL.getText().toString();
        sName = ET_NAME.getText().toString();
        sPhone = ET_PHONE.getText().toString();
        sPassword = ET_PASSWORD.getText().toString();

        if (sEmail.equals("") || sName.equals("") || sPhone.equals("") || sPassword.equals("")){
            Toast.makeText(this, "Please enter required information", Toast.LENGTH_SHORT).show();
            return;
        }

        if(sPhone.length() != 10){
            Toast.makeText(this, "Phone number must be of 10 digit", Toast.LENGTH_SHORT).show();
            return;
        }

        if(sPassword.length() < 8){
            Toast.makeText(this, "Password must greater than 8 character", Toast.LENGTH_SHORT).show();
            return;
        }

        PROGRESS.setVisibility(View.VISIBLE);
        CREATE_ACCOUNT();

    }

    public void CREATE_ACCOUNT() {
        try {
            HashMap<String, String> MAP = new HashMap<String, String>();
            MAP.put("EMAIL", sEmail);
            MAP.put("NAME", sName);
            MAP.put("PHONE", sPhone);
            MAP.put("PASSWORD", sPassword);

            ROOT.child("USERS").child(sPhone).setValue(MAP).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    SharedPreferences.Editor editor = getSharedPreferences("SMART_FARM", MODE_PRIVATE).edit();
                    editor.putString("AUTH","true");
                    editor.putString("USERNAME",sPhone);
                    editor.apply();
                    Toast.makeText(SignupActivity.this, "Account Create Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignupActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                    PROGRESS.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}