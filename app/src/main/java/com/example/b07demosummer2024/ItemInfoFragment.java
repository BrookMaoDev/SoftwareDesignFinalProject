package com.example.b07demosummer2024;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemInfoFragment extends Fragment {

    // Store references to all views
    private TextView textViewName;
    private TextView textViewLot;
    private TextView textViewCategory;
    private TextView textViewPeriod;
    private TextView textViewDescription;
    private ImageView imageView;

    // Reference to item contains all information along with loaded bitmap
    private ItemWithImage item;

    // Hide empty constructor
    private ItemInfoFragment() {}

    /**
     * Creates a new ItemInfoFragment to display information about an item.
     * @param item  The item to display information for.
     */
    public static ItemInfoFragment fromItem(ItemWithImage item) {
        ItemInfoFragment self = new ItemInfoFragment();
        self.item = item;
        return self;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_info, container, false);

        this.imageView = view.findViewById(R.id.imageViewItem);
        this.textViewName = view.findViewById(R.id.textViewItemName);
        this.textViewLot = view.findViewById(R.id.textViewItemLot);
        this.textViewCategory = view.findViewById(R.id.textViewItemCategory);
        this.textViewPeriod = view.findViewById(R.id.textViewItemPeriod);
        this.textViewDescription = view.findViewById(R.id.textViewItemDescription);

        this.imageView.setImageBitmap(this.item.getImage());

        this.textViewName.setText(this.item.getName());

        String lotText = "Lot " + this.item.getLotNumber();
        this.textViewLot.setText(lotText);

        String prefix = "Category: ";
        SpannableString categoryText = new SpannableString(prefix + this.item.getCategory());
        categoryText.setSpan(new StyleSpan(Typeface.BOLD), 0, prefix.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.textViewCategory.setText(categoryText);

        prefix = "Period: ";
        SpannableString periodText = new SpannableString(prefix + this.item.getPeriod());
        periodText.setSpan(new StyleSpan(Typeface.BOLD), 0, prefix.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.textViewPeriod.setText(periodText);

        this.textViewDescription.setText(this.item.getDescription());

        view.findViewById(R.id.buttonItemBack).setOnClickListener(
                v -> getParentFragmentManager().popBackStack());

        return view;
    }
}