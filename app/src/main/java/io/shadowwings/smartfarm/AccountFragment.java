package io.shadowwings.smartfarm;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    ImageView BTN_LOGOUT;
    TextView TXT_USERNAME;
    DatabaseReference ROOT;
    String PRIMARY_KEY = "";
    ConstraintLayout PROGRESS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    public void init(View view){
        BTN_LOGOUT = view.findViewById(R.id.BTN_LOGOUT);
        TXT_USERNAME = view.findViewById(R.id.TXT_USERNAME);
        PROGRESS = view.findViewById(R.id.PROGRESS);
        PROGRESS.setVisibility(View.VISIBLE);
        SharedPreferences preferences = getActivity().getSharedPreferences("SMART_FARM", Context.MODE_PRIVATE);
        PRIMARY_KEY = preferences.getString("USERNAME", "");
        ROOT = FirebaseDatabase.getInstance("https://smart-farm-c9ae4-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("USERS");

        onClick();
        setupUser();
    }

    public void onClick(){
        BTN_LOGOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("SMART_FARM", MODE_PRIVATE).edit();
                editor.clear().apply();
                startActivity(new Intent(getActivity(), SplashActivity.class));
            }
        });
    }

    public void setupUser(){
        ROOT.child(PRIMARY_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TXT_USERNAME.setText(snapshot.child("NAME").getValue().toString());
                PROGRESS.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}