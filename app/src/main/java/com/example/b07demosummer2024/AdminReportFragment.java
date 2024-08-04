package com.example.b07demosummer2024;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class AdminReportFragment extends Fragment{
    private EditText editTextLotNumber, editTextName, editTextCategory, editTextPeriod;
    private Button buttonLotNum, buttonName, buttonCategory, buttonPeriod;
    private Button buttonGenerate1, buttonGenerate2, buttonGenerate3, buttonGenerate4;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private StorageReference storageRef;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
            }
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_report_file, container, false);

        db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com/");
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://softwaredesignfinalproje-5aa70.appspot.com");
        this.storageRef = storage.getReference();

        // Initialize edit texts
        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        editTextName = view.findViewById(R.id.editTextName);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextPeriod = view.findViewById(R.id.editTextPeriod);

        // Initialize buttons
        buttonLotNum = view.findViewById(R.id.buttonLotNum);
        buttonName = view.findViewById(R.id.buttonName);
        buttonCategory = view.findViewById(R.id.buttonCategory);
        buttonPeriod = view.findViewById(R.id.buttonPeriod);
        buttonGenerate1 = view.findViewById(R.id.buttonGenerate1);
        buttonGenerate2 = view.findViewById(R.id.buttonGenerate2);
        buttonGenerate3 = view.findViewById(R.id.buttonGenerate3);
        buttonGenerate4 = view.findViewById(R.id.buttonGenerate4);

        // Set click listeners using lambda expressions (Java 8+)
        buttonLotNum.setOnClickListener(v -> handleButtonClick("LotNum"));
        buttonName.setOnClickListener(v -> handleButtonClick("Name"));
        buttonCategory.setOnClickListener(v -> handleButtonClick("Category"));
        buttonPeriod.setOnClickListener(v -> handleButtonClick("Period"));
        buttonGenerate1.setOnClickListener(v -> handleButtonClick("Generate1"));
        buttonGenerate2.setOnClickListener(v -> handleButtonClick("Generate2"));
        buttonGenerate3.setOnClickListener(v -> handleButtonClick("Generate3"));
        buttonGenerate4.setOnClickListener(v -> handleButtonClick("Generate4"));

        return view;
    }

    private void addInformation(){
        String lotNumber = editTextLotNumber.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String period = editTextPeriod.getText().toString().trim();
    }

    private void handleButtonClick(String buttonName) {
        switch (buttonName) {
            case "LotNum":
                // Action for buttonLotNum
                break;
            case "Name":
                // Action for buttonName
                break;
            case "Category":
                // Action for buttonCategory
                break;
            case "Period":
                // Action for buttonPeriod
                break;
            case "Generate1":
                // Action for buttonGenerate1
                break;
            case "Generate2":
                // Action for buttonGenerate2
                break;
            case "Generate3":
                // Action for buttonGenerate3
                break;
            case "Generate4":
                // Action for buttonGenerate4
                break;
            default:
                throw new IllegalArgumentException("Unexpected button name: " + buttonName);
        }
    }

}
