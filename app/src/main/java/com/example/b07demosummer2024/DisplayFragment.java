package com.example.b07demosummer2024;

import com.bumptech.glide.Glide;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;


public class DisplayFragment extends Fragment {
    private final static int NUM_OF_ITEMS_PER_PAGE = 5;

    private Button buttonView, buttonBack, buttonNextPage, buttonPrevPage, buttonReport;
    private TableLayout tableLayout;
    private int currentPage = 1;
    private int numberOfPages;
    private EditText pageNumber;
    private ItemCatalogue itemCatalogue;
    private ArrayList<Item> currentItems;
    private ItemCatalogue.Filter filter;
    private ArrayList<TableRow> tableRowList;
    private TextView textView;

    public static DisplayFragment makeInstance(ItemCatalogue.Filter filter){
        DisplayFragment displayFragment = new DisplayFragment();
        displayFragment.filter = filter;
        return displayFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_display_item, container, false);
        buttonView = view.findViewById(R.id.buttonView);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonReport = view.findViewById(R.id.buttonReport);
        buttonNextPage = view.findViewById(R.id.buttonNextPage);
        buttonPrevPage = view.findViewById(R.id.buttonPrevPage);
        currentItems = new ArrayList<>();
        tableLayout = view.findViewById(R.id.tableLayout);
        pageNumber = view.findViewById(R.id.pageNumber);
        itemCatalogue = DatabaseManager.getInstance().createItemCatalogue().withFilter(filter);
        itemCatalogue.applyFilter();
        textView = view.findViewById(R.id.textView);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();


        Button[] adminButtons = {buttonReport};
        applyAdminPerms(uid, adminButtons);


        tableRowList = new ArrayList<>(NUM_OF_ITEMS_PER_PAGE);
        for (int i = 0; i < NUM_OF_ITEMS_PER_PAGE; i++) {
            TableRow tableRow = (TableRow) inflater.inflate(R.layout.item_row, tableLayout, false);
            tableLayout.addView(tableRow);
            tableRowList.add(tableRow);
        }


        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewItem();
            }
        });


        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new AdminReportFragment()); }
        });


        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());


        buttonNextPage.setOnClickListener(v -> loadNextPage());


        buttonPrevPage.setOnClickListener(v -> loadPrevPage());


        itemCatalogue.onUpdate(() -> {
            itemCatalogue.applyFilter();
            numberOfPages = (int) Math.ceil(itemCatalogue.getNumOfItems() * 1.0
                    / NUM_OF_ITEMS_PER_PAGE);
            loadPage(1);
        });


        itemCatalogue.init();

        if(filter != null){
            textView.setText("Search results");
        }

        return view;
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadPage(int currentPage){
        this.currentPage = currentPage;
        int startIndex = (currentPage - 1) * NUM_OF_ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + NUM_OF_ITEMS_PER_PAGE,
                itemCatalogue.getNumOfItems());


        currentItems.clear();
        for(int i = startIndex; i < endIndex; i++){
            currentItems.add(itemCatalogue.getItems().get(i));
        }


        for(int i = 0; i < NUM_OF_ITEMS_PER_PAGE; i++){
            TableRow tableRow = tableRowList.get(i);
            if(i + startIndex < endIndex){
                Item item = currentItems.get(i);
                bindItemToRow(item, tableRow);
                tableRow.setVisibility(View.VISIBLE);
            }
            else{
                tableRow.setVisibility(View.INVISIBLE);
            }
        }


        updatePage();
    }


    private void loadNextPage(){
        loadPage(currentPage + 1);
    }


    private void loadPrevPage(){
        loadPage(currentPage - 1);
    }


    private void updatePage(){
        String pageNumberText = "Page " + currentPage + " / " + numberOfPages;
        pageNumber.setText(pageNumberText);
        buttonNextPage.setEnabled(currentPage < numberOfPages);
        buttonPrevPage.setEnabled(currentPage > 1);
    }


    private void bindItemToRow(Item item, TableRow tableRow) {
        ((CheckBox) tableRow.findViewById(R.id.checkBox)).setChecked(false);
        ((TextView) tableRow.findViewById(R.id.nameTextView)).setText(item.getName());
        ((TextView) tableRow.findViewById(R.id.lotNumberTextView)).setText(item.getLotNumber());
        ((TextView) tableRow.findViewById(R.id.categoryTextView)).setText(item.getCategory());
        ((TextView) tableRow.findViewById(R.id.periodTextView)).setText(item.getPeriod());

        // Load image using Glide
        ImageView imageView = tableRow.findViewById(R.id.imageView);
        String imageUrl = item.getSavePath();
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }


    private void viewItem(){
        int count = 0;
        for(int i = 0; i < currentItems.size(); i++){
            TableRow tableRow = tableRowList.get(i);
            CheckBox checkBox = tableRow.findViewById(R.id.checkBox);
            if(checkBox.isChecked()) count++;
        }
        if(count == 0){
            Toast.makeText(getContext(), "Please check a box you want to view", Toast.LENGTH_SHORT).show();
        }
        else if(count > 1){
            Toast.makeText(getContext(), "You can only view one item at a time", Toast.LENGTH_SHORT).show();
        }
        else{
            Item item = null;
            for(int i = 0; i < currentItems.size(); i++){
                TableRow tableRow = tableRowList.get(i);
                CheckBox checkBox = tableRow.findViewById(R.id.checkBox);
                if(checkBox.isChecked()){
                    item = currentItems.get(i);
                }
            }


            new ItemWithImage.Loader(item) {
                @Override
                public void onLoad(ItemWithImage item) {
                    loadFragment(ItemInfoFragment.fromItem(item));
                }
            }.execute();
        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void applyAdminPerms(String uid, Button[] B) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com");
        DatabaseReference dbref = db.getReference("admins/" + uid);


        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAdmin = snapshot.exists();


                for (Button b : B) {
                    b.setEnabled(isAdmin);
                    if (isAdmin) {
                        b.setVisibility(View.VISIBLE);
                    } else {
                        b.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}