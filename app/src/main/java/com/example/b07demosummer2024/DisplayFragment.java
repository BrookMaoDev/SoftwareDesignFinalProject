package com.example.b07demosummer2024;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DisplayFragment extends Fragment {
    private final static int NUM_OF_ITEMS_PER_PAGE = 5;

    private Button buttonView, buttonBack, buttonNextPage, buttonPrevPage;
    private TableLayout tableLayout;
    private int currentPage = 1;
    private int numberOfPages;
    private EditText pageNumber;
    private ItemCatalogue itemCatalogue;
    private ArrayList<Item> currentItems;
    private ItemCatalogue.Filter filter;
    private ArrayList<TableRow> tableRowList;

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
        buttonNextPage = view.findViewById(R.id.buttonNextPage);
        buttonPrevPage = view.findViewById(R.id.buttonPrevPage);
        currentItems = new ArrayList<>();
        tableLayout = view.findViewById(R.id.tableLayout);
        pageNumber = view.findViewById(R.id.pageNumber);
        itemCatalogue = DatabaseManager.getInstance().createItemCatalogue().withFilter(filter);
        itemCatalogue.applyFilter();

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
            Item item = currentItems.get(i);
            if(i + startIndex < endIndex){
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
        ((TextView) tableRow.findViewById(R.id.nameTextView)).setText(item.getName());
        ((TextView) tableRow.findViewById(R.id.lotNumberTextView)).setText(item.getLotNumber());
        ((TextView) tableRow.findViewById(R.id.categoryTextView)).setText(item.getCategory());
        ((TextView) tableRow.findViewById(R.id.periodTextView)).setText(item.getPeriod());
        // loadImage((ImageView) row.findViewById(R.id.pictureImageView), item.getPictureUrl());
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
            Item item;
            for(int i = 0; i < currentItems.size(); i++){
                TableRow tableRow = tableRowList.get(i);
                CheckBox checkBox = tableRow.findViewById(R.id.checkBox);
                if(checkBox.isChecked()){
                    item = currentItems.get(i);
                }
            }
            // pass the item to view item
            // but idk which one is for view item lol
        }
    }
}
