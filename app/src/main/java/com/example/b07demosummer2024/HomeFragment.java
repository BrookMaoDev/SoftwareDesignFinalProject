package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        Button buttonRecyclerView = view.findViewById(R.id.buttonRecyclerView);
        Button buttonScroller = view.findViewById(R.id.buttonScroller);
        Button buttonSpinner = view.findViewById(R.id.buttonSpinner);
        Button buttonManageItems = view.findViewById(R.id.buttonManageItems);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        applyAdminPerms(uid, buttonManageItems);

        buttonRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new RecyclerViewFragment());
            }
        });

        buttonScroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ScrollerFragment());
            }
        });

        buttonSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SearchItemFragment());
            }
        });

        buttonManageItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new ManageItemsFragment());}
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void applyAdminPerms(String uid, Button b){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com");
        DatabaseReference dbref = db.getReference("admins/" + uid);

        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean isAdmin;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isAdmin = snapshot.exists();

                Log.d("isAdmin (inside isAdmin method)", String.valueOf(isAdmin));

                if(isAdmin){
                    b.setEnabled(true);
                    b.setVisibility(View.VISIBLE);
                }
                else{
                    b.setEnabled(false);
                    b.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
