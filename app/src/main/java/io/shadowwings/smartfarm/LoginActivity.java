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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout BTN_SIGNUP, BTN_HELP;
    CardView BTN_LOGIN; ProgressBar PROGRESS;
    EditText ET_PHONE, ET_PASSWORD;
    String sPhone = "", sPassword = "";
    DatabaseReference ROOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.GLARE_WHITE));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        init();

    }

    public void init(){
        BTN_SIGNUP = findViewById(R.id.BTN_SIGNUP);
        BTN_LOGIN = findViewById(R.id.BTN_LOGIN);
        BTN_HELP = findViewById(R.id.BTN_HELP);
        PROGRESS = findViewById(R.id.PROGRESS);
        PROGRESS.setVisibility(View.GONE);
        ET_PHONE = findViewById(R.id.ET_PHONE);
        ET_PASSWORD = findViewById(R.id.ET_PASSWORD);
        ROOT = FirebaseDatabase.getInstance("https://smart-farm-c9ae4-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        onClick();
    }

    public void onClick(){
        BTN_SIGNUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        BTN_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CHECK_INPUTS();
            }
        });
    }

    public void CHECK_INPUTS(){
        sPhone = ET_PHONE.getText().toString();
        sPassword = ET_PASSWORD.getText().toString();

        if (sPhone.equals("") || sPassword.equals("")){
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
        CHECK_USER();

    }

    public void CHECK_USER(){
        ROOT.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(sPhone).exists()){
                    if (snapshot.child(sPhone).child("PASSWORD").getValue().toString().equals(sPassword)){
                        SharedPreferences.Editor editor = getSharedPreferences("SMART_FARM", MODE_PRIVATE).edit();
                        editor.putString("AUTH","true");
                        editor.putString("USERNAME",sPhone);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "User login successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }else {
                        Toast.makeText(LoginActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "User doest not exist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        PROGRESS.setVisibility(View.GONE);
    }
}