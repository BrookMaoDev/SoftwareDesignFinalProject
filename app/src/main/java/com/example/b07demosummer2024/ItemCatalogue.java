package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ItemCatalogue {

    // A query that dictates the total search space
    Query dir;

    // The filter to use on our catalogue
    Filter filter;

    // The items that should display in the catalogue
    private final ArrayList<Item> items;

    private ItemCatalogue() {
        this.items = new ArrayList<>();
    }

    /**
     * Create a new catalogue from a query to a database.
     * @param dir   the query to use
     * @return      the new catalogue
     */
    public static ItemCatalogue fromDatabaseDirectory(Query dir) {
        ItemCatalogue self = new ItemCatalogue();
        self.dir = dir;
        return self;
    }

    /**
     * Set our catalogue to use a filter.
     * @param filter    the filter to create the catalogue from
     * @return          this
     */
    public ItemCatalogue withFilter(Filter filter) {
        this.filter = filter;
        this.applyFilter();
        return this;
    }

    /**
     * Add a new listener to this ItemCatalogue
     * @param listener  the listener to add
     */
    void addValueEventListener(ValueEventListener listener) {
        this.dir.addValueEventListener(listener);
    }

    /**
     * Refresh internal data with a given snapshot.
     * @param snapshot  the snapshot to use
     */
    void collectFromSnapshot(DataSnapshot snapshot) {
        this.items.clear();
        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
            this.items.add(childSnapshot.getValue(Item.class));
        }
        this.applyFilter();
    }

    /**
     * Apply the filter to the catalogue.
     */
    void applyFilter() {
        this.filter.applyToList(this.items);
    }

    /**
     * @return the items in this catalogue
     */
    ArrayList<Item> getItems() {
        return this.items;
    }

    /**
     * Print the items in this catalogue in a JSON-like format.
     */
    void display() {
        for (int i = 0; i < this.items.size(); ++i) {
            System.out.println((i + 1) + ": " + this.items.get(i) + ",");
        }
    }

    /**
     * A utility class used to fluently build a filter.
     */
    public static class Filter {

        // Filter parameters
        String category;
        String lotNumber;
        String period;
        String namePrefix;

        // Attributes to sort by
        String[] keys;

        // Ordered ascending or descending?
        boolean descending;

        public Filter() {}

        private Filter(String category, String lotNumber, String period, String namePrefix,
                       String[] keys, boolean descending) {
            this.category = category;
            this.lotNumber = lotNumber;
            this.period = period;
            this.namePrefix = namePrefix;
            this.keys = keys;
            this.descending = descending;
        }

        public Filter category(String category) {
            this.category = category;
            return this;
        }

        public Filter lotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
            return this;
        }

        public Filter period(String period) {
            this.period = period;
            return this;
        }

        public Filter namePrefix(String prefix) {
            this.namePrefix = prefix;
            return this;
        }

        public Filter orderBy(String[] keys) {
            this.keys = keys;
            return this;
        }

        public Filter ascending() {
            this.descending = false;
            return this;
        }

        public Filter descending() {
            this.descending = true;
            return this;
        }

        /**
         * @return a copy of this builder
         */
        public Filter duplicate() {
            return new Filter(this.category, this.lotNumber, this.period, this.namePrefix,
                    this.keys, this.descending);
        }

        /**
         * Apply this filter to a list of items.
         * @param items the list of items to apply this filter to
         */
        public void applyToList(List<Item> items) {
            items.removeIf(this::rejects);
            if (this.keys == null || this.keys.length == 0) return;
            items.sort(this.descending ?
                    Collections.reverseOrder(this::compareItems)
                    : this::compareItems);
        }

        /**
         * @param item  the item to check
         * @return whether or not this filter accepts an item
         */
        public boolean accepts(Item item) {
            if (this.category != null && !item.getCategory().equalsIgnoreCase(this.category)) {
                return false;
            }
            if (this.lotNumber != null && !item.getLotNumber().equalsIgnoreCase(this.lotNumber)) {
                return false;
            }
            if (this.period != null && !item.getPeriod().equalsIgnoreCase(this.period)) {
                return false;
            }
            if (this.namePrefix != null
                    && !item.getName().toUpperCase().startsWith(this.namePrefix.toUpperCase())) {
                return false;
            }
            return true;
        }

        /**
         * @param item  the item to check
         * @return whether or not this filter rejects an item
         */
        public boolean rejects(Item item) {
            return !this.accepts(item);
        }

        /**
         * Compare two items by the keys in this filter.
         * @param a the first item
         * @param b the second item
         * @return  -1 if a < b, 0 if a == 0, 1 otherwise
         */
        public int compareItems(Item a, Item b) {
            for (int i = 0; i < this.keys.length; ++i) {
                int val = compareByKey(a, b, this.keys[i]);
                if (val != 0) return val;
            }
            return 0;
        }

        /**
         * Compare two items by a key.
         * @param a     the first item
         * @param b     the second item
         * @param key   the key to use
         * @return      -1 if a < b, 0 if a == 0, 1 otherwise
         */
        public static int compareByKey(Item a, Item b, String key) {
            switch (key) {
                case "category":
                    return a.getCategory().compareToIgnoreCase(b.getCategory());
                case "lotNumber":
                    try {
                        return Integer.valueOf(a.getLotNumber())
                                .compareTo(Integer.valueOf(b.getLotNumber()));
                    } catch (Exception e) {
                        return a.getLotNumber().compareToIgnoreCase(b.getLotNumber());
                    }
                case "period":
                    return a.getPeriod().compareToIgnoreCase(b.getPeriod());
                case "name":
                    return a.getName().compareToIgnoreCase(b.getName());
                default:
                    return 0;
            }
        }

    }

}
