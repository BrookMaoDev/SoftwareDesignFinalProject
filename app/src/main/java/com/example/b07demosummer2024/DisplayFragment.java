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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DisplayFragment extends Fragment {
    private final static int NUM_OF_ITEMS_PER_PAGE = 5;

    private Button buttonReport, buttonBack, buttonNextPage, buttonPrevPage;
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
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonNextPage = view.findViewById(R.id.buttonNextPage);
        buttonPrevPage = view.findViewById(R.id.buttonPrevPage);
        buttonReport = view.findViewById(R.id.buttonReport);
        currentItems = new ArrayList<>();
        tableLayout = view.findViewById(R.id.tableLayout);
        pageNumber = view.findViewById(R.id.pageNumber);
        itemCatalogue = DatabaseManager.getInstance().createItemCatalogue().withFilter(filter);
        itemCatalogue.applyFilter();

        tableRowList = new ArrayList<>(NUM_OF_ITEMS_PER_PAGE);
        for (int i = 0; i < NUM_OF_ITEMS_PER_PAGE; i++) {
            TableRow row = (TableRow) inflater.inflate(R.layout.item_row, tableLayout, false);
            tableLayout.addView(row);
            tableRowList.add(row);
        }

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

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPage(int currentPage){
        this.currentPage = currentPage;
        int startIndex = (currentPage - 1) * NUM_OF_ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + NUM_OF_ITEMS_PER_PAGE - 1,
                itemCatalogue.getNumOfItems());

        currentItems.clear();
        for(int i = startIndex; i <= endIndex; i++){
            currentItems.add(itemCatalogue.getItems().get(i));
        }

        for(int i = 0; i < NUM_OF_ITEMS_PER_PAGE; i++){
            TableRow tableRow = tableRowList.get(i);
            Item item = currentItems.get(i);
            if(i + startIndex <= endIndex){
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
        buttonNextPage.setEnabled(currentPage > 1);
        buttonPrevPage.setEnabled(currentPage < numberOfPages && currentPage > 1);
    }

    private void bindItemToRow(Item item, TableRow row) {
        // how to bind check box to row?
        ((TextView) row.findViewById(R.id.nameTextView)).setText(item.getName());
        ((TextView) row.findViewById(R.id.lotNumberTextView)).setText(item.getLotNumber());
        ((TextView) row.findViewById(R.id.categoryTextView)).setText(item.getCategory());
        ((TextView) row.findViewById(R.id.periodTextView)).setText(item.getPeriod());
        // loadImage((ImageView) row.findViewById(R.id.pictureImageView), item.getPictureUrl());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
