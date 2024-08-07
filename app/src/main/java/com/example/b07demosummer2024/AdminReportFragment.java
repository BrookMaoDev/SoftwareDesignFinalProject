package com.example.b07demosummer2024;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.graphics.pdf.PdfDocument;
import android.text.Html;

public class AdminReportFragment extends Fragment {
    private EditText editTextLotNumber, editTextName, editTextCategory, editTextPeriod;
    private Button buttonLotNumber, buttonName, buttonCategory, buttonPeriod;
    private Button buttonGenerate1, buttonGenerate2, buttonGenerate3, buttonGenerate4, buttonCancel;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private StorageReference storageRef;
    private ItemCatalogue itemCatalogue;
    private ArrayList<Item> items;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_report, container, false);

        db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com/");
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://softwaredesignfinalproje-5aa70.appspot.com");
        this.storageRef = storage.getReference();

        // Initialize edit texts
        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        editTextName = view.findViewById(R.id.editTextName);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextPeriod = view.findViewById(R.id.editTextPeriod);

        // Initialize buttons
        buttonLotNumber = view.findViewById(R.id.buttonLotNumber);
        buttonName = view.findViewById(R.id.buttonName);
        buttonCategory = view.findViewById(R.id.buttonCategory);
        buttonPeriod = view.findViewById(R.id.buttonPeriod);
        buttonGenerate1 = view.findViewById(R.id.buttonGenerate1);
        buttonGenerate2 = view.findViewById(R.id.buttonGenerate2);
        buttonGenerate3 = view.findViewById(R.id.buttonGenerate3);
        buttonGenerate4 = view.findViewById(R.id.buttonGenerate4);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        // Set click listeners using lambda expressions (Java 8+)
        buttonLotNumber.setOnClickListener(v -> handleButtonClick("LotNumber"));
        buttonName.setOnClickListener(v -> handleButtonClick("Name"));
        buttonCategory.setOnClickListener(v -> handleButtonClick("Category"));
        buttonPeriod.setOnClickListener(v -> handleButtonClick("Period"));
        buttonGenerate1.setOnClickListener(v -> handleButtonClick("Generate1"));
        buttonGenerate2.setOnClickListener(v -> handleButtonClick("Generate2"));
        buttonGenerate3.setOnClickListener(v -> handleButtonClick("Generate3"));
        buttonGenerate4.setOnClickListener(v -> handleButtonClick("Generate4"));
        buttonCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        itemCatalogue = DatabaseManager.getInstance().createItemCatalogue();
        itemCatalogue.onUpdate(() -> {
            items = itemCatalogue.getItems();
        });
        itemCatalogue.init();

        return view;
    }

    private void handleButtonClick(String buttonName) {
        String lotNumber = editTextLotNumber.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String period = editTextPeriod.getText().toString().trim();
        items = itemCatalogue.getItems();

        switch (buttonName) {
            case "LotNumber":
                if (lotNumber.isEmpty()) {
                    Toast.makeText(requireContext(), "Lot Number cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    itemCatalogue.changeFilter(new ItemCatalogue.Filter().lotNumber(lotNumber));
                    if (items.isEmpty()) {
                        Toast.makeText(requireContext(), "Invalid Lot Number", Toast.LENGTH_SHORT).show();
                    } else {
                        generatePdf("Report of lot number");
                    }
                }
                break;
            case "Name":
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    itemCatalogue.changeFilter(new ItemCatalogue.Filter().name(name));
                    if (items.isEmpty()) {
                        Toast.makeText(requireContext(), "Invalid Name", Toast.LENGTH_SHORT).show();
                    } else {
                        generatePdf("Report of name");
                    }
                }
                break;
            case "Category":
                if (category.isEmpty()) {
                    Toast.makeText(requireContext(), "Category cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    itemCatalogue.changeFilter(new ItemCatalogue.Filter().category(category));
                    if (items.isEmpty()) {
                        Toast.makeText(requireContext(), "Invalid Category", Toast.LENGTH_SHORT).show();
                    } else {
                        generatePdf("Report of category");
                    }
                }
                break;
            case "Period":
                if (period.isEmpty()) {
                    Toast.makeText(requireContext(), "Period cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    itemCatalogue.changeFilter(new ItemCatalogue.Filter().period(period));
                    if (items.isEmpty()) {
                        Toast.makeText(requireContext(), "Invalid Period", Toast.LENGTH_SHORT).show();
                    } else {
                        generatePdf("Report of period");
                    }
                }
                break;
            case "Generate1":
                itemCatalogue.changeFilter(new ItemCatalogue.Filter().category(category));
                if (category.isEmpty()) {
                    Toast.makeText(requireContext(), "Category cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (items.isEmpty()) {
                    Toast.makeText(requireContext(), "Invalid Category", Toast.LENGTH_SHORT).show();
                } else {
                    generatePdf("Report of category, description and picture only");
                }
                break;
            case "Generate2":
                itemCatalogue.changeFilter(new ItemCatalogue.Filter().period(period));
                if (period.isEmpty()) {
                    Toast.makeText(requireContext(), "Period cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (items.isEmpty()) {
                    Toast.makeText(requireContext(), "Invalid Period", Toast.LENGTH_SHORT).show();
                } else {
                    generatePdf("Report of period, description and picture only");
                }
                break;
            case "Generate3":
                generatePdf("Report of all items");
                break;
            case "Generate4":
                generatePdf("Report of all items with description and picture only");
                break;
            default:
                throw new IllegalArgumentException("Unexpected button name: " + buttonName);
        }
    }

    private View createDynamicLayoutForItem(Item item) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.fragment_generated_report, null);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewContent = view.findViewById(R.id.textViewContent);

        // Set data for each item with improved formatting
        String title = "Item Report";
        String content = "<html><body>" +
                "<h2>Item Details</h2>" +
                "<p><strong>Category:</strong> " + item.getCategory() + "</p>" +
                "<p><strong>Description:</strong> " + item.getDescription() + "</p>" +
                "<p><strong>Lot Number:</strong> " + item.getLotNumber() + "</p>" +
                "<p><strong>Name:</strong> " + item.getName() + "</p>" +
                "<p><strong>Period:</strong> " + item.getPeriod() + "</p>" +
                "<p><strong>Save Path:</strong> " + item.getSavePath() + "</p>" +
                "</body></html>";

        textViewTitle.setText(title);
        textViewContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)); // For HTML formatting

        return view;
    }

    private void generatePdf(String reportName) {
        try {
            File pdfFile = new File(requireContext().getExternalFilesDir(null), reportName + ".pdf");

            PdfDocument document = new PdfDocument();

            for (int i = 0; i < items.size(); i++) {
                // Create a page for each item
                View view = createDynamicLayoutForItem(items.get(i)); // Pass each item to the layout creation
                Bitmap bitmap = createBitmapFromView(view);

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i + 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                canvas.drawBitmap(bitmap, 0, 0, null);
                document.finishPage(page);
            }

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                document.writeTo(fos);
            }
            document.close();

            // Download the generated PDF
            downloadPDF(requireContext(), pdfFile);
            Toast.makeText(requireContext(), "PDF Generated: " + pdfFile.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private View createDynamicLayout(String reportName) {
        // Inflate your layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.fragment_generated_report, null);

        // Modify the layout based on reportName or other data
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewContent = view.findViewById(R.id.textViewContent);

        // Example data based on reportName
        String title = reportName;
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            content.append(items.get(i).toString());
        }

        textViewTitle.setText(title);
        textViewContent.setText(content.toString());

        return view;
    }

    private Bitmap createBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

    private void saveBitmapToPDF(Bitmap bitmap, File pdfFile) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            document.writeTo(fos);
        }
        document.close();
    }

    private void downloadPDF(Context context, File pdfFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFile.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            try (OutputStream os = resolver.openOutputStream(uri)) {
                FileInputStream fis = new FileInputStream(pdfFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // For Android 9 and below
            try {
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File destinationFile = new File(downloadDir, pdfFile.getName());
                FileInputStream fis = new FileInputStream(pdfFile);
                FileOutputStream fos = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}