package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbProductSlideShow;
import vungnv.com.foodappmerchant.model.ProductSlideShowModel;

public class ProductSlideShowDAO {
    private final SQLiteDatabase db;

    public ProductSlideShowDAO(Context context) {
        DbProductSlideShow dbHelper = new DbProductSlideShow(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(ProductSlideShowModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        values.put("type", obj.type);

        return db.insert("ProductSlideShow", null, values);
    }

    public int updateAll(ProductSlideShowModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        values.put("type", obj.type);

        return db.update("ProductSlideShow", values, "id=?", new String[]{String.valueOf(obj.id)});
    }

    public String getUriImg(String type) {
        String sql = "SELECT * FROM ProductSlideShow WHERE type=?";
        List<ProductSlideShowModel> list = getData(sql, type);
        return list.get(0).img;
    }


    public int delete(String type) {
        return db.delete("ProductSlideShow", "type=?", new String[]{type});
    }


    public List<ProductSlideShowModel> getALL() {
        String sql = "SELECT * FROM ProductSlideShow";
        return getData(sql);
    }


    @SuppressLint("Range")
    private List<ProductSlideShowModel> getData(String sql, String... selectionArgs) {
        List<ProductSlideShowModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            ProductSlideShowModel obj = new ProductSlideShowModel();
            obj.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            obj.img = cursor.getString(cursor.getColumnIndex("img"));
            obj.type = cursor.getString(cursor.getColumnIndex("type"));

            list.add(obj);

        }
        return list;

    }
}
