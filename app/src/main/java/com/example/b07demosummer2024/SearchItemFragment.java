package com.example.b07demosummer2024;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchItemFragment extends Fragment {
    private EditText editTextLotNumber, editTextName;
    private Button buttonUpload, buttonResult;
    private Spinner spinnerCategory, spinnerPeriod;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private StorageReference storageRef;
    private Bitmap selectedImageBitmap;
    ArrayList<Item> filteredList;
    ItemAdapter adapter;
    private String category, period;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_items, container, false);

        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        editTextName = view.findViewById(R.id.editTextName);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        buttonResult = view.findViewById(R.id.buttonResult);
        buttonUpload = view.findViewById(R.id.buttonUpload);

        db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com/");

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://softwaredesignfinalproje-5aa70.appspot.com");
        this.storageRef = storage.getReference();

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.period_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapter);

        buttonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return view;
    }

    private void searchItem() {
        String lotNumber = editTextLotNumber.getText().toString().trim();
        String name = editTextName.getText().toString().toLowerCase().trim();

        category = null;
        if(spinnerCategory != null && spinnerCategory.getSelectedItem() != null ) {
            category = spinnerCategory.getSelectedItem().toString().toLowerCase();
        }
//        }
        period = null;
        if(spinnerPeriod != null && spinnerPeriod.getSelectedItem() != null ) {
            period = spinnerPeriod.getSelectedItem().toString().toLowerCase();
        }

        if (lotNumber.isEmpty() || name.isEmpty() || category == null || period == null ) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        itemsRef = db.getReference("categories/" + category);
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Item> filteredResults = null;
                boolean itemFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null || item.getName().toLowerCase().contains(name) &&
                            item.getLotNumber().contains(lotNumber) &&
                            item.getCategory().contains(category) ||
                            item.getPeriod().contains(period)) {
                        itemFound = true;
                        filteredResults.add(item);
                    }
                }
                if (!itemFound) {
                    Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
                }
                else {
                    //function displayItem() is in progress
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
