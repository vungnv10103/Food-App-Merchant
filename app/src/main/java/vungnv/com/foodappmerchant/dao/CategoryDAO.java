package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbCategories;
import vungnv.com.foodappmerchant.model.CategoryModel;

public class CategoryDAO {
    private final SQLiteDatabase db;

    public CategoryDAO(Context context) {
        DbCategories dbHelper = new DbCategories(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(CategoryModel obj) {
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("img", obj.img);

        return db.insert("Category", null, values);
    }

    public int updateAll(CategoryModel obj) {
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("img", obj.img);

        return db.update("Category", values, "id=?", new String[]{String.valueOf(obj.id)});
    }

    public String getUriImg(String name) {
        String sql = "SELECT * FROM Category WHERE name=?";
        List<CategoryModel> list = getData(sql, name);
        return list.get(0).img;
    }


    public int delete(String name) {
        return db.delete("Category", "name=?", new String[]{name});
    }


    public List<CategoryModel> getALL() {
        String sql = "SELECT * FROM Category";
        return getData(sql);
    }


    @SuppressLint("Range")
    private List<CategoryModel> getData(String sql, String... selectionArgs) {
        List<CategoryModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            CategoryModel obj = new CategoryModel();
            obj.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            obj.img = cursor.getString(cursor.getColumnIndex("img"));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));

            list.add(obj);

        }
        return list;

    }
}
