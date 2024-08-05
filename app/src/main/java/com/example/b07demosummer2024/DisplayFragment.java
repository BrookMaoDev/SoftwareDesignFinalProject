package com.example.b07demosummer2024;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DisplayFragment extends Fragment {
    private final static int NUM_OF_ITEMS_PER_PAGE = 5;

    private Button buttonView, buttonBack, buttonNextPage, buttonPrevPage;
    private RecyclerView recyclerView;
    private int currentPage = 1;
    private int numberOfPages;
    private EditText pageNumber;
    private ItemCatalogue itemCatalogue;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> currentItems;
    private ItemCatalogue.Filter filter;

    public static DisplayFragment makeInstance(ItemCatalogue.Filter filter){
        DisplayFragment displayFragment = new DisplayFragment();
        displayFragment.filter = filter;
        return displayFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_display_item, container, false);

        buttonView = buttonView.findViewById(R.id.buttonView);
        buttonBack = buttonBack.findViewById(R.id.buttonBack);
        buttonNextPage = buttonNextPage.findViewById(R.id.buttonNextPage);
        buttonPrevPage = buttonPrevPage.findViewById(R.id.buttonPrevPage);
        recyclerView = recyclerView.findViewById(R.id.recyclerView);
        currentItems = new ArrayList<>();
        itemAdapter = new ItemAdapter(currentItems);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemCatalogue = DatabaseManager.getInstance().createItemCatalogue().withFilter(filter);
        itemCatalogue.applyFilter();
        /* buttonView.setOnClickListener({
                @Override
                public void onClick(View v){ loadFragment(new ViewFragment())
                };
        })*/

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

        itemAdapter.notifyDataSetChanged();
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
        buttonPrevPage.setEnabled(currentPage < numberOfPages);
    }

}
