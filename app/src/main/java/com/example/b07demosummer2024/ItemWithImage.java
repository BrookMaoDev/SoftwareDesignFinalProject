package com.example.b07demosummer2024;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.URL;

public class ItemWithImage extends Item {

    private Bitmap image;

    public ItemWithImage(String lotNumber, String name, String category, String period,
                         String description, String savePath, Bitmap image) {
        super(lotNumber, name, category, period, description, savePath);
        this.image = image;
    }

    public static ItemWithImage fromItemAndImage(Item item, Bitmap img) {
        return new ItemWithImage(item.getLotNumber(), item.getName(), item.getCategory(),
                item.getPeriod(), item.getDescription(), item.getSavePath(), img);
    }

    public Bitmap getImage() {
        return this.image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public abstract static class Loader extends AsyncTask<String, Void, ItemWithImage> {
        private Item item;

        public Loader(Item item) {
            this.item = item;
        }

        @Override
        protected ItemWithImage doInBackground(String... urls) {
            try {
                Bitmap img = BitmapFactory.decodeStream(
                        new URL(this.item.getSavePath()).openStream());
                this.onLoad(ItemWithImage.fromItemAndImage(this.item, img));
            } catch (Exception e) {

            }
            return null;
        }

        public abstract void onLoad(ItemWithImage item);
    }

}
