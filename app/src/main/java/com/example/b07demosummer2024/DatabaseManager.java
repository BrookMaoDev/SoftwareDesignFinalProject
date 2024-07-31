package com.example.b07demosummer2024;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Dictionary;
import java.util.HashMap;

public final class DatabaseManager {
    private FirebaseDatabase db;
    private static DatabaseManager instance;

    private DatabaseManager() {
        db = FirebaseDatabase.getInstance("https://softwaredesignfinalproje-5aa70-default-rtdb.firebaseio.com/");
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public DatabaseReference items() {
        return this.db.getReference().child("items");
    }

    public ItemCatalogue createItemCatalogue() {
        return ItemCatalogue.fromDatabaseDirectory(this.items());
    }
}
