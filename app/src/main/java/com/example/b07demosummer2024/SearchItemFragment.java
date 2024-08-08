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
import androidx.fragment.app.FragmentTransaction;

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
    private Button buttonResult, buttonBack;
    private Spinner spinnerCategory, spinnerPeriod;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private StorageReference storageRef;
    private Bitmap selectedImageBitmap;
    private ItemCatalogue items;
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
        buttonBack = view.findViewById(R.id.buttonBack);

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

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void searchItem() {
        String lotNumberTemp = editTextLotNumber.getText().toString().trim();
        if (lotNumberTemp.isEmpty()) {
            lotNumberTemp = null;
        }
        final String lotNumber = lotNumberTemp;

        String nameTemp = editTextName.getText().toString().toLowerCase().trim();
        if (nameTemp.isEmpty()) {
            nameTemp = null;
        }
        final String name = nameTemp;

        category = null;
        if(spinnerCategory != null && spinnerCategory.getSelectedItem() != null ) {
            category = spinnerCategory.getSelectedItem().toString().toLowerCase();
        }
//        }
        period = null;
        if(spinnerPeriod != null && spinnerPeriod.getSelectedItem() != null ) {
            period = spinnerPeriod.getSelectedItem().toString().toLowerCase();
        }

        this.items = DatabaseManager.getInstance().createItemCatalogue();
        this.items.init();
        this.items.onUpdate(() -> {
            this.items.changeFilter(new ItemCatalogue.Filter()
                    .categoryEquals(category)
                    .lotNumberEquals(lotNumber)
                    .name(name)
                    .periodEquals(period));
            this.items.applyFilter();
            if (this.items.getNumOfItems() == 0) {
                Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            } else {
                loadFragment(DisplayFragment.makeInstance(this.items.getFilter()));
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

