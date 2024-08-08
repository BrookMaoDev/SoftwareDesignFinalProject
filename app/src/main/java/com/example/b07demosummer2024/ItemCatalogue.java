package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemCatalogue {

    // A query that dictates the total search space
    private Query dir;

    // The filter to use on our catalogue
    private Filter filter;

    // All items collected from query
    private final ArrayList<Item> allItems;

    // The items that should display in the catalogue
    private final ArrayList<Item> items;

    // The list of functions to call in order
    private final ArrayList<Runnable> routines;

    private ItemCatalogue() {
        this.allItems = new ArrayList<>();
        this.items = new ArrayList<>();
        this.routines = new ArrayList<>();
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
     * @param filter    the filter to use on the items
     * @return          this
     */
    public ItemCatalogue withFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Set our catalogue to use a filter and update accordingly.
     * @param filter    the filter to use on the items
     */
    public void changeFilter(Filter filter) {
        if (this.filter != null && this.filter.equals(filter)) {
            return;
        }
        this.filter = filter;
        this.applyFilter();
        this.update();
    }

    /**
     * Add a function to be called when database information is updated.
     * @param  fn the function to be called
     */
    public void onUpdate(Runnable fn) {
        this.routines.add(fn);
    }

    /**
     * Initialize the catalogue (polling for changes).
     * @return this
     */
    public ItemCatalogue init() {
        this.dir.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectFromSnapshot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return this;
    }

    /**
     * @return this catalogue's filter
     */
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Refresh internal data with a given snapshot.
     * @param snapshot  the snapshot to use
     */
    public void collectFromSnapshot(DataSnapshot snapshot) {
        this.allItems.clear();
        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
            this.allItems.add(childSnapshot.getValue(Item.class));
        }
        this.applyFilter();
        this.update();
    }

    /**
     * Apply the filter to the catalogue.
     */
    public void applyFilter() {
        this.items.clear();
        if (this.filter == null) {
            this.items.addAll(this.allItems);
            return;
        }
        this.filter.collectFilteredItems(this.allItems, this.items);
    }

    /**
     * Run all onUpdate functions.
     */
    public void update() {
        ArrayList<Runnable> failed = new ArrayList<>();

        for (Runnable fn : this.routines) {
            try {
                fn.run();
            } catch (Exception e) {
                failed.add(fn);
                System.err.println("ItemCatalogue interacted with unloaded fragment");
            }
        }

        this.routines.removeIf(failed::contains);
    }

    /**
     * Clears all "onUpdate" functions.
     */
    public void clearFunctionality() {
        this.routines.clear();
    }

    /**
     * @return the items in this catalogue
     */
    public ArrayList<Item> getItems() {
        return this.items;
    }

    /**
     *
     * @return the number of items in this catalogue
     */
    public int getNumOfItems(){return this.items.size();}

    /**
     * Print the items in this catalogue in a JSON-like format.
     */
    public void display() {
        for (int i = 0; i < this.items.size(); ++i) {
            System.out.println((i + 1) + ": " + this.items.get(i) + ",");
        }
    }

    /**
     * A utility class used to fluently build a filter.
     */
    public static class Filter {

        // Filter parameters
        StringChecker category;
        StringChecker lotNumber;
        StringChecker period;
        StringChecker name;

        // Attributes to sort by
        String[] keys;

        // Ordered ascending or descending?
        boolean descending;

        public Filter() {}

        private Filter(StringChecker category, StringChecker lotNumber, StringChecker period,
                       StringChecker name, String[] keys, boolean descending) {
            this.category = category;
            this.lotNumber = lotNumber;
            this.period = period;
            this.name = name;
            this.keys = keys;
            this.descending = descending;
        }

        public Filter category(String category) {
            this.category = new StringChecker(category, false);
            return this;
        }

        public Filter lotNumber(String lotNumber) {
            this.lotNumber = new StringChecker(lotNumber, false);
            return this;
        }

        public Filter period(String period) {
            this.period = new StringChecker(period, false);
            return this;
        }

        public Filter name(String name) {
            this.name = new StringChecker(name, false);
            return this;
        }

        public Filter categoryEquals(String category) {
            this.category = new StringChecker(category, true);
            return this;
        }

        public Filter lotNumberEquals(String lotNumber) {
            this.lotNumber = new StringChecker(lotNumber, true);
            return this;
        }

        public Filter periodEquals(String period) {
            this.period = new StringChecker(period, true);
            return this;
        }

        public Filter nameEquals(String name) {
            this.name = new StringChecker(name, true);
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
            return new Filter(this.category.duplicate(), this.lotNumber.duplicate(),
                    this.period.duplicate(), this.name.duplicate(), this.keys.clone(),
                    this.descending);
        }

        /**
         * Apply this filter to a list of items.
         * @param items the list of items to apply this filter to
         */
        public void collectFilteredItems(List<Item> allItems, List<Item> items) {
            allItems.stream().filter(this::accepts).forEach(items::add);
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
            if (this.category != null && !this.category.matches(item.getCategory())) {
                return false;
            }

            if (this.lotNumber != null && !this.lotNumber.matches(item.getLotNumber())) {
                return false;
            }

            if (this.period != null && !this.period.matches(item.getPeriod())) {
                return false;
            }

            if (this.name != null && !this.name.matches(item.getName())) {
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

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (o == this) {
                return true;
            }

            if (!(o instanceof Filter)) {
                return false;
            }

            Filter that = (Filter) o;
            return this.category.equals(that.category)
                    && this.lotNumber.equals(that.lotNumber)
                    && this.name.equals(that.name)
                    && this.period.equals(that.period)
                    && this.descending == that.descending;
        }

    }

}

class StringChecker {

    // The internal value of the checker
    String value;

    // Whether or not the checked value has to be exact
    boolean exact;

    public StringChecker(String value, boolean exact) {
        this.value = value;
        this.exact = exact;
    }

    /**
     * @param value the value to check
     * @return true iff this checker accepts `value`
     */
    public boolean matches(String value) {
        if (this.value == null) {
            return true;
        }

        if (this.exact) {
            return this.value.equalsIgnoreCase(value);
        } else {
            return StringUtil.containsIgnoreCase(value, this.value);
        }
    }

    /**
     * @return a copy of this StringChecker
     */
    public StringChecker duplicate() {
        return new StringChecker(this.value, this.exact);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof StringChecker)) {
            return false;
        }

        StringChecker that = (StringChecker) o;
        if (this.value == null && that.value == null) {
            return this.exact == that.exact;
        }

        if (this.value == null || that.value == null) {
            return false;
        }

        return this.value.equals(that.value) && this.exact == that.exact;
    }

}