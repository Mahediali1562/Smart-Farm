package io.shadowwings.smartfarm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.shadowwings.smartfarm.Adapter.MainPostModel;

public class HomeFragment extends Fragment {

    RecyclerView MAIN_RECYCLER;
    DatabaseReference ROOT;
    ConstraintLayout PROGRESS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MAIN_RECYCLER = view.findViewById(R.id.MAIN_RECYCLERVIEW);
        PROGRESS = view.findViewById(R.id.PROGRESS);
        PROGRESS.setVisibility(View.VISIBLE);
        ROOT = FirebaseDatabase.getInstance("https://smart-farm-c9ae4-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        GET_POSTS();
    }

    public void GET_POSTS(){
        List<MainPostModel> LIST = new ArrayList<>();
        ROOT.child("POSTS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()){
                    LIST.add(new MainPostModel(snap.child("URL").getValue().toString(),snap.child("TITLE").getValue().toString(),
                            snap.child("POST_TYPE").getValue().toString(), snap.child("TIME").getValue().toString(),
                            snap.child("USERNAME").getValue().toString()));
                }
                io.shadowwings.smartfarm.MainPostAdapter adapter = new io.shadowwings.smartfarm.MainPostAdapter(LIST);
                MAIN_RECYCLER.setAdapter(adapter);
                MAIN_RECYCLER.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                PROGRESS.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        GET_POSTS();
    }
}