package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

public class Item {

    private String lotNumber;
    private String name;
    private String category;
    private String period;
    private String description;
    private String savePath;

    public Item() {}

    public Item(String lotNumber, String name, String category, String period, String description, String savePath) {
        this.lotNumber = lotNumber;
        this.name = name;
        this.category = category;
        this.period = period;
        this.description = description;
        this.savePath = savePath;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPeriod() {
        return period;
    }

    public String getDescription() {
        return description;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @NonNull
    @Override
    public String toString() {
        return  "{"
                + "\n\tcategory: " + this.category
                + "\n\tdescription: " + this.description
                + "\n\tlotNumber: " + this.lotNumber
                + "\n\tname: " + this.name
                + "\n\tperiod: " + this.period
                + "\n\tsavePath: " + this.savePath
                + "\n}";
    }
}
