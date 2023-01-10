package vungnv.com.foodappmerchant.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.database.DbDishes;
import vungnv.com.foodappmerchant.model.DishesModel;

public class DishesDAO {
    private final SQLiteDatabase db;

    public DishesDAO(Context context) {
        DbDishes dbHelper = new DbDishes(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(DishesModel obj) {
        ContentValues values = new ContentValues();
        values.put("name", obj.name);

        return db.insert("Dishes", null, values);
    }


    public int delete(String name) {
        return db.delete("Dishes", "name=?", new String[]{name});
    }


    public List<DishesModel> getALL() {
        String sql = "SELECT * FROM Dishes";
        return getData(sql);
    }


    @SuppressLint("Range")
    private List<DishesModel> getData(String sql, String... selectionArgs) {
        List<DishesModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            DishesModel obj = new DishesModel();
            obj.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));

            list.add(obj);

        }
        return list;

    }
}
